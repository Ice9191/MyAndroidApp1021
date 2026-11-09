package com.hxb.myandroidapp_1021.utils

import android.os.IBinder
import android.util.Log
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import kotlin.time.Duration.Companion.milliseconds

class RfidScanner {
    // 扫到的全部RFID
    private val _scannedTags = MutableStateFlow<Set<String>>(emptySet())
    val scannedTags: StateFlow<Set<String>> = _scannedTags.asStateFlow()

    // 是否在扫描
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    // 页面状态
    private val _status = MutableStateFlow("等待初始化")
    val status: StateFlow<String> = _status.asStateFlow()

    // 实体键回调状态
    private val _triggerStatus = MutableStateFlow("实体扫描键未连接")
    val triggerStatus: StateFlow<String> = _triggerStatus.asStateFlow()

    // 错误文案
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // 功率(dBm)
    private val _power = MutableStateFlow(DEFAULT_POWER)
    val power: StateFlow<Int> = _power.asStateFlow()

    // 事件流
    private val _tagEvents = MutableSharedFlow<String>(extraBufferCapacity = 64)
    val tagEvents: SharedFlow<String> = _tagEvents.asSharedFlow()

    // 内部
    private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private var service: UHFService? = null
    private var pollJob: Job? = null
    private var foreground = false
    private var keyService: ScanKeyService? = null
    private var keyCallback: IKeyEventCallback? = null
    private val heldKeys = mutableSetOf<Int>()

    /**
     * 进入页面初始化RFID扫描器
     */
    fun init(enableHardwareKey: Boolean = true) {
        scope.launch {
            foreground = true
            try {
                open()
            } catch (t: Throwable) {
                report(t)
            }
            if (enableHardwareKey) registerKeys()
        }
    }

    /**
     * 离开页面释放RFID扫描器
     */
    fun release() {
        scope.launch {
            foreground = false
            close()
        }
    }

    // 扫描控制
    fun startScan() {
        scope.launch { start() }
    }

    fun stopScan() {
        scope.launch { stop() }
    }

    /**
     * 清缓存和集合
     */
    fun clear() {
        scope.launch {
            try {
                service?.let {
                    check(
                        it.setParameters(
                            UHFService.PARAMETER_CLEAR_EPCLIST,
                            1
                        )
                    ) { "清空缓存失败" }
                }
                _scannedTags.value = emptySet()
            } catch (t: Throwable) {
                report(t)
            }
        }
    }

    /**
     * 设置功率
     */
    fun setPower(newPower: Int) {
        scope.launch {
            val value = newPower.coerceIn(MIN_POWER, MAX_POWER)
            try {
                service?.let { check(it.setPower(value)) { "设置功率失败" } }
                _power.value = value
            } catch (t: Throwable) {
                report(t)
            }
        }
    }

    /**
     * 彻底销毁
     */
    fun destroy() {
        scope.launch {
            try {
                foreground = false
                close()
            } finally {
                scope.cancel()
                dispatcher.close()
            }
        }
    }

    private fun report(t: Throwable) {
        if (t is CancellationException) throw t
        Log.e(TAG, "UHF调用失败", t)
        _errorMessage.value = "RFID失败：${t.message ?: t.javaClass.simpleName}"
        _status.value = "设备异常，可点击扫描重试"
    }

    private fun reconnectIfNeeded(device: UHFService) {
        val serverField = try {
            UHFService::class.java.getDeclaredField("server").apply { isAccessible = true }
        } catch (_: NoSuchFieldException) {
            return
        }
        val binder = serverField.get(device) as? IBinder
        if (binder?.isBinderAlive == true) return
        _status.value = "正在重新连接UHF服务"
        Log.w(TAG, "UHF Binder已失效，重新连接系统服务")
        val init = UHFService::class.java.getDeclaredMethod("init").apply { isAccessible = true }
        val connected = init.invoke(device) as? Boolean ?: false
        val refreshed = serverField.get(device) as? IBinder
        check(connected && refreshed?.isBinderAlive == true) {
            "系统UHF服务正在恢复，请稍后重新按下扫描键"
        }
        Log.i(TAG, "UHF服务重新连接成功")
    }

    private fun open(): UHFService {
        val device = service ?: UHFService.getInstance()
        try {
            reconnectIfNeeded(device)
            runCatching { device.close() }
            Thread.sleep(50L)
            check(device.open())
            Thread.sleep(100L)
            check(device.isOpen || device.open()) { "UHF服务或模块未就绪" }
            val targetPower = _power.value
            var success = false
            for (attempt in 1..3) {
                if (device.setPower(targetPower)) {
                    success = true
                    break
                }
                Log.w(TAG, "setPower attempt $attempt failed, retrying...")
                Thread.sleep(50L * attempt)
            }
            check(success) { "设置功率失败" }
            service = device
            _errorMessage.value = null
            _status.value = "RFID已就绪"
            return device
        } catch (t: Throwable) {
            service = null
            runCatching { device.close() }
            throw t
        }
    }

    private fun startInventory(device: UHFService): UHFService {
        check(device.setParameters(UHFService.PARAMETER_CLEAR_EPCLIST, 1)) {
            "清空设备标签缓存失败"
        }
        check(device.inventoryStart()) { "启动盘点失败" }
        return device
    }

    private fun start() {
        if (!foreground || _isScanning.value) return
        try {
            val openedDevice = try {
                service?.takeIf { it.isOpen }?.let(::startInventory)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.w(TAG, "已打开的UHF设备启动失败，尝试重新连接", e)
                null
            }
            val device = openedDevice ?: startInventory(open())
            _isScanning.value = true
            _errorMessage.value = null
            _status.value = "正在读取RFID标签"
            pollJob = scope.launch {
                try {
                    val binder = runCatching {
                        UHFService::class.java.getDeclaredField("server").apply {
                            isAccessible = true
                        }.get(device) as? IBinder
                    }.getOrNull()
                    while (isActive && _isScanning.value) {
                        check(binder?.isBinderAlive != false) { "系统UHF服务连接已断开，请松开后重新按下扫描键以重连" }
                        val ids = device.tagIDs
                            .orEmpty()
                            .mapNotNull { it?.getId()?.trim()?.takeIf(String::isNotEmpty) }
                        if (ids.isNotEmpty()) {
                            val prev = _scannedTags.value
                            val newIds = ids.toSet() - prev
                            _scannedTags.value = prev + ids
                            newIds.forEach { _tagEvents.tryEmit(it) }
                        }
                        delay(100.milliseconds)
                    }
                } catch (t: Throwable) {
                    if (t !is CancellationException) {
                        stop()
                        report(t)
                    } else throw t
                }
            }
        } catch (t: Throwable) {
            stop()
            report(t)
        }
    }

    private fun stop() {
        pollJob?.cancel()
        pollJob = null
        _isScanning.value = false
        try {
            service?.let { check(it.inventoryStop()) { "停止盘点失败" } }
            _status.value = if (service != null) "扫描已停止" else "设备未连接"
        } catch (t: Throwable) {
            report(t)
        }
    }

    private fun close() {
        unregisterKeys()
        stop()
        try {
            service?.close()
        } catch (t: Throwable) {
            report(t)
        } finally {
            service = null
            _status.value = "RFID已释放"
        }
    }

    /**
     * 实体键
     */
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
            keys.registerCallback(callback, SCAN_KEYS.joinToString(","))
            _triggerStatus.value = "实体键已连接：按住扳机 / SCAN 扫描，松开停止"
            Log.i(TAG_KEY, "ScanKey callback registered")
        } catch (t: Throwable) {
            unregisterKeys()
            _triggerStatus.value = "实体键连接失败：${t.message ?: t.javaClass.simpleName}"
            Log.e(TAG_KEY, "注册扫描键失败", t)
        }
    }

    private fun unregisterKeys() {
        val callback = keyCallback
        keyCallback = null
        heldKeys.clear()
        try {
            if (callback != null) keyService?.unregisterCallback(callback)
        } catch (t: Throwable) {
            Log.e(TAG_KEY, "注销扫描键失败", t)
        } finally {
            keyService = null; _triggerStatus.value = "实体扫描键已释放"
        }
    }

    companion object {
        private const val TAG = "RfidScanner"
        private const val TAG_KEY = "RfidTrigger"
        private val SCAN_KEYS = setOf(142, 248, 249, 250, 252)
        const val MIN_POWER = 5
        const val MAX_POWER = 30
        const val DEFAULT_POWER = 20
    }
}
