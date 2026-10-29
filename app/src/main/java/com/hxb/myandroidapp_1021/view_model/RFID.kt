package com.hxb.myandroidapp_1021.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seuic.uhf.UHFService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Collections
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration.Companion.milliseconds

class RfidViewModel : ViewModel() {

    @Volatile
    private var uhfService: UHFService? = null

    // 硬件初始化任务：多个入口同时调用 initHardware() 时只会 open 一次
    private var initTask: Deferred<Boolean>? = null
    private var pollJob: Job? = null

    @Volatile
    private var startInProgress = false

    // 存放读取到的标签列表(使用Set自动去重)
    private val _scannedTags = MutableStateFlow<Set<String>>(emptySet())
    val scannedTags = _scannedTags.asStateFlow()

    // 扫描状态
    private val _isScanning = MutableStateFlow(false)
    val isScanning = _isScanning.asStateFlow()

    // 当前发射功率
    private val _power = MutableStateFlow(DEFAULT_POWER)
    val power = _power.asStateFlow()

    // 带缓冲：没有收集者时 tryEmit 也不会丢事件
    private val _errorMessage = MutableSharedFlow<String>(extraBufferCapacity = 8)
    val errorMessage = _errorMessage.asSharedFlow()

    // 重置列表时把硬件缓存里已有的 EPC 记进来，轮询时跳过，避免清空后立刻又被塞回去
    private val suppressed: MutableSet<String> = Collections.synchronizedSet(mutableSetOf())
    private val pendingClear = AtomicBoolean(false)

    // 初始化硬件
    fun initHardware() {
        if (initTask == null) {
            initTask = viewModelScope.async(Dispatchers.IO) { openHardware() }
        }
    }

    private suspend fun openHardware(): Boolean = try {
        val service = UHFService.getInstance()
        uhfService = service
        if (service.open()) {
            service.power = _power.value
            Log.i(TAG, "RFID硬件初始化成功，功率 ${_power.value} dBm")
            true
        } else {
            emitError("RFID硬件打开失败，请检查设备")
            false
        }
    } catch (t: Throwable) {
        // 缺 .so 时抛的是 UnsatisfiedLinkError(Error 而不是 Exception)，必须抓 Throwable
        if (t is CancellationException) throw t
        Log.e(TAG, "RFID硬件初始化异常", t)
        emitError("RFID硬件初始化异常：${t.message ?: t.javaClass.simpleName}")
        false
    }

    // 等初始化完成；失败过的初始化允许下次重试
    private suspend fun ensureServiceReady(): Boolean {
        if (initTask == null) initHardware()
        val task = initTask ?: return false
        val ready = task.await()
        if (!ready) initTask = null
        return ready
    }

    // 开始扫描
    fun startScan() {
        if (_isScanning.value || startInProgress) return
        startInProgress = true
        viewModelScope.launch {
            try {
                if (!ensureServiceReady()) {
                    emitError("硬件未准备好")
                    return@launch
                }
                val service = uhfService
                if (service == null) {
                    emitError("硬件未准备好")
                    return@launch
                }
                // 硬件调用统一放到 IO 线程，避免卡主线程
                val started = withContext(Dispatchers.IO) {
                    try {
                        service.inventoryStart()
                    } catch (t: Throwable) {
                        if (t is CancellationException) throw t
                        Log.e(TAG, "开启扫描异常", t)
                        false
                    }
                }
                if (!started) {
                    emitError("无法开启扫描，请重试")
                    return@launch
                }
                suppressed.clear() // 新一轮扫描，之前重置时的忽略记录作废
                _isScanning.value = true
                startPollLoop()
            } finally {
                startInProgress = false
            }
        }
    }

    // 停止扫描
    fun stopScan() {
        if (!_isScanning.value) return
        _isScanning.value = false
        pollJob?.cancel()
        pollJob = null
        viewModelScope.launch(Dispatchers.IO) {
            try {
                uhfService?.inventoryStop()
            } catch (t: Throwable) {
                if (t is CancellationException) throw t
                Log.e(TAG, "停止扫描异常", t)
            }
        }
    }

    // 动态设置功率
    fun updatePower(newPower: Int) {
        val clamped = newPower.coerceIn(MIN_POWER, MAX_POWER)
        if (clamped == _power.value) return
        _power.value = clamped
        viewModelScope.launch(Dispatchers.IO) {
            try {
                uhfService?.power = clamped
            } catch (t: Throwable) {
                if (t is CancellationException) throw t
                Log.e(TAG, "设置功率失败", t)
            }
        }
    }

    // 清空列表
    fun clearList() {
        _scannedTags.value = emptySet()
        // 让轮询协程在下一轮把硬件缓存快照进 suppressed
        pendingClear.set(true)
    }

    // 后台轮询读取数据
    private fun startPollLoop() {
        pollJob?.cancel()
        pollJob = viewModelScope.launch(Dispatchers.IO) {
            while (isActive && _isScanning.value) {
                try {
                    // 重置列表：把硬件缓存里已有的标签记为忽略，并把本地列表中这些条目移除
                    if (pendingClear.compareAndSet(true, false)) {
                        val cached = uhfService?.tagIDs
                            ?.mapNotNull { it.toString().takeIf(String::isNotBlank) }
                            .orEmpty()
                        if (cached.isNotEmpty()) {
                            suppressed.addAll(cached)
                            _scannedTags.update { it - cached.toSet() }
                        }
                    }
                    // 获取当前已经读取到的标签数量
                    if ((uhfService?.tagIDCount ?: 0) > 0) {
                        val tags = uhfService?.tagIDs
                        if (!tags.isNullOrEmpty()) {
                            val fresh = tags.asSequence()
                                .mapNotNull { it.toString().takeIf(String::isNotBlank) }
                                .filter { epc -> epc !in suppressed }
                                .toSet()
                            if (fresh.isNotEmpty()) {
                                // 更新UI状态
                                _scannedTags.update { it + fresh }
                            }
                        }
                    }
                } catch (t: Throwable) {
                    if (t is CancellationException) throw t
                    Log.e(TAG, "轮询读取异常", t)
                }
                delay(50.milliseconds)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollJob?.cancel()
        pollJob = null
        _isScanning.value = false
        startInProgress = false
        initTask?.cancel()
        initTask = null
        val service = uhfService
        uhfService = null
        if (service != null) {
            // 此时 viewModelScope 已取消，用独立作用域把硬件停掉并关闭
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    service.inventoryStop()
                } catch (t: Throwable) {
                    if (t !is CancellationException) Log.e(TAG, "关闭前停止扫描异常", t)
                }
                try {
                    service.close()
                } catch (t: Throwable) {
                    if (t !is CancellationException) Log.e(TAG, "关闭硬件异常", t)
                }
                Log.i(TAG, "RFID资源已释放")
            }
        }
    }

    private fun emitError(message: String) {
        if (!_errorMessage.tryEmit(message)) {
            Log.w(TAG, "错误事件被丢弃：$message")
        }
    }

    companion object {
        private const val TAG = "RfidViewModel"

        /** 功率范围，单位 dBm */
        const val MIN_POWER = 5
        const val MAX_POWER = 30
        const val DEFAULT_POWER = 20
    }
}
