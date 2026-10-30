package com.hxb.myandroidapp_1021.view_model

import android.annotation.SuppressLint
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.ViewModel
import com.seuic.scankey.IKeyEventCallback
import com.seuic.scankey.ScanKeyService
import com.seuic.uhf.UHFService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import kotlin.time.Duration.Companion.milliseconds

/** 所有 SDK 调用在同一个线程执行，避免打开、盘点、关闭相互竞争。 */
class RfidViewModel : ViewModel() {
    private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private var service: UHFService? = null
    private var pollJob: Job? = null
    private var foreground = false
    private var keyService: ScanKeyService? = null
    private var keyCallback: IKeyEventCallback? = null
    private val heldKeys = mutableSetOf<Int>()
    private val _triggerStatus = MutableStateFlow("实体扫描键未连接")
    val triggerStatus = _triggerStatus.asStateFlow()

    private fun registerKeys() {
        if (keyCallback != null) return
        try {
            val keys = ScanKeyService.getInstance()
            val callback = object : IKeyEventCallback.Stub() {
                override fun onKeyDown(keyCode: Int) = handle(keyCode, true)
                override fun onKeyUp(keyCode: Int) = handle(keyCode, false)
                private fun handle(code: Int, down: Boolean) {
                    val source = this
                    scope.launch {
                        if (!foreground || keyCallback !== source) return@launch
                        if (code !in SCAN_KEYS) return@launch
                        Log.i("RfidTrigger", "key=$code down=$down")
                        _triggerStatus.value = "实体键 $code：${if (down) "按下" else "松开"}"
                        if (down) {
                            if (heldKeys.add(code)) start()
                        } else if (heldKeys.remove(code) && heldKeys.isEmpty()) {
                            stop()
                        }
                    }
                }
            }
            keyService = keys
            keyCallback = callback
            // 实机 ScanKeyManagerService 的 cookie 按原始 Linux 键码匹配。
            keys.registerCallback(callback, SCAN_KEYS.joinToString(","))
            _triggerStatus.value = "实体键已连接：按住扳机 / SCAN 扫描，松开停止"
            Log.i("RfidTrigger", "ScanKey callback registered")
        } catch (t: Throwable) {
            unregisterKeys()
            _triggerStatus.value = "实体键连接失败：${t.message ?: t.javaClass.simpleName}"
            Log.e("RfidTrigger", "注册扫描键失败", t)
        }
    }

    private fun unregisterKeys() {
        val callback = keyCallback
        keyCallback = null
        heldKeys.clear()
        try { if (callback != null) keyService?.unregisterCallback(callback) }
        catch (t: Throwable) { Log.e("RfidTrigger", "注销扫描键失败", t) }
        finally { keyService = null; _triggerStatus.value = "实体扫描键已释放" }
    }
    private val _scannedTags = MutableStateFlow<Set<String>>(emptySet())
    val scannedTags = _scannedTags.asStateFlow()
    private val _isScanning = MutableStateFlow(false)
    val isScanning = _isScanning.asStateFlow()
    private val _power = MutableStateFlow(DEFAULT_POWER)
    val power = _power.asStateFlow()
    private val _status = MutableStateFlow("等待初始化")
    val status = _status.asStateFlow()
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private fun report(t: Throwable) {
        if (t is CancellationException) throw t
        Log.e("RfidViewModel", "UHF 调用失败", t)
        _errorMessage.value = "RFID失败：${t.message ?: t.javaClass.simpleName}"
        _status.value = "设备异常，可点击扫描重试"
    }

    /**
     * UTouch 当前系统库的 binderDied() 只打印日志，不会重连。
     * 仅当其 server Binder 已失效时调用该厂商库自己的 init()，不终止系统服务。
     * 其他 SDK 版本没有这些成员时，保留标准接口路径。
     */
    private fun reconnectIfNeeded(device: UHFService) {
        val serverField = try {
            UHFService::class.java.getDeclaredField("server").apply { isAccessible = true }
        } catch (_: NoSuchFieldException) { return }
        val binder = serverField.get(device) as? IBinder
        if (binder?.isBinderAlive == true) return
        _status.value = "正在重新连接 UHF 服务"
        Log.w("RfidViewModel", "UHF Binder 已失效，重新连接系统服务")
        val init = UHFService::class.java.getDeclaredMethod("init").apply { isAccessible = true }
        val connected = init.invoke(device) as? Boolean ?: false
        val refreshed = serverField.get(device) as? IBinder
        check(connected && refreshed?.isBinderAlive == true) {
            "系统 UHF 服务正在恢复，请稍后重新按下扫描键"
        }
        Log.i("RfidViewModel", "UHF 服务重新连接成功")
    }
    private fun open(): UHFService {
        val device = service ?: UHFService.getInstance()
        try {
            reconnectIfNeeded(device)
            check(device.isOpen || device.open()) {
                "UHF 服务或模块未就绪，请稍后重试；若持续失败，请检查系统 UHF 工具及模块连接"
            }
            check(device.setPower(_power.value)) { "设置功率失败" }
            service = device
            _errorMessage.value = null
            _status.value = "RFID 已就绪"
            return device
        } catch (t: Throwable) {
            service = null
            runCatching { device.close() }
            throw t
        }
    }

    fun initHardware() { scope.launch {
        foreground = true
        try { open() } catch (t: Throwable) { report(t) }
        registerKeys()
    } }

    fun startScan() { scope.launch { start() } }

    private fun start() {
        if (!foreground || _isScanning.value) return
        try {
            val device = open()
            check(device.setParameters(UHFService.PARAMETER_CLEAR_EPCLIST, 1)) { "清空设备标签缓存失败" }
            check(device.inventoryStart()) { "启动盘点失败" }
            _isScanning.value = true
            _errorMessage.value = null
            _status.value = "正在读取 RFID 标签"
            pollJob = scope.launch {
                try {
                    val binder = runCatching {
                        UHFService::class.java.getDeclaredField("server").apply {
                            isAccessible = true
                        }.get(device) as? IBinder
                    }.getOrNull()
                    while (isActive && _isScanning.value) {
                        check(binder?.isBinderAlive != false) {
                            "系统 UHF 服务连接已断开，请松开后重新按下扫描键以重连"
                        }
                        // EPC.getId() 是编码；不要将 EPC 对象的 toString() 当作编码。
                        val ids = device.tagIDs.orEmpty().mapNotNull { it?.getId()?.trim()?.takeIf(String::isNotEmpty) }
                        if (ids.isNotEmpty()) _scannedTags.value = _scannedTags.value + ids
                        delay(100.milliseconds)
                    }
                } catch (t: Throwable) {
                    if (t !is CancellationException) { stop(); report(t) }
                    else throw t
                }
            }
        } catch (t: Throwable) { stop(); report(t) }
    }

    private fun stop() {
        pollJob?.cancel()
        pollJob = null
        _isScanning.value = false
        try {
            service?.let { check(it.inventoryStop()) { "停止盘点失败" } }
            _status.value = if (service != null) "扫描已停止" else "设备未连接"
        } catch (t: Throwable) { report(t) }
    }

    fun stopScan() { scope.launch { stop() } }

    fun updatePower(newPower: Int) { scope.launch {
        val value = newPower.coerceIn(MIN_POWER, MAX_POWER)
        try {
            service?.let { check(it.setPower(value)) { "设置功率失败" } }
            _power.value = value
        } catch (t: Throwable) { report(t) }
    } }

    /** 清除设备缓存后，仍在读区的标签可以重新读入。 */
    fun clearList() { scope.launch {
        try {
            service?.let { check(it.setParameters(UHFService.PARAMETER_CLEAR_EPCLIST, 1)) { "清空缓存失败" } }
            _scannedTags.value = emptySet()
        } catch (t: Throwable) { report(t) }
    } }

    private fun close() {
        unregisterKeys()
        stop()
        try { service?.close() } catch (t: Throwable) { report(t) }
        finally { service = null; _status.value = "RFID 已释放" }
    }

    fun releaseHardware() { scope.launch { foreground = false; close() } }

    @SuppressLint("EmptySuperCall")
    override fun onCleared() {
        scope.launch {
            try { foreground = false; close() }
            finally { scope.cancel(); dispatcher.close() }
        }
        super.onCleared()
    }

    companion object {
        private val SCAN_KEYS = setOf(142, 248, 249, 250, 252)
        const val MIN_POWER = 5
        const val MAX_POWER = 30
        const val DEFAULT_POWER = 20
    }
}
