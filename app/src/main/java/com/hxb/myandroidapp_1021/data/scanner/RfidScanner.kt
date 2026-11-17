package com.hxb.myandroidapp_1021.data.scanner

import android.os.IBinder
import android.util.Log
import com.hxb.myandroidapp_1021.domain.scanner.RfidReader
import com.seuic.scankey.IKeyEventCallback
import com.seuic.scankey.ScanKeyService
import com.seuic.uhf.UHFService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import kotlin.time.Duration.Companion.milliseconds

/**
 * 唯一控制入口为 start()：按钮点击切换扫描，实体键按住扫描、松开停止。
 * 结果在主线程回调；前台作用域取消时自动注销实体键并释放 UHF。
 */
class RfidScanner(
    private val lifetime: CoroutineScope,
    private val onTagRead: (String) -> Unit,
    private val onStateChanged: (Boolean, String) -> Unit,
    private val onError: (String?) -> Unit
) : RfidReader {
    private val scope = CoroutineScope(lifetime.coroutineContext + dispatcher)
    private var service: UHFService? = null
    private var pollJob: Job? = null
    private var active = false
    private var scanning = false
    private var startedByKey = false
    private var keyService: ScanKeyService? = null
    private var keyCallback: IKeyEventCallback? = null
    private val heldKeys = mutableSetOf<Int>()
    private val sessionTags = mutableSetOf<String>()

    init {
        scope.launch {
            try {
                // 所有会话共用扫描线程；先关闭旧会话，旧页面的延迟清理不能关闭新读头。
                current?.close()
                current = this@RfidScanner
                active = true
                publishState("RFID 待扫描")
                registerKeys()
                awaitCancellation()
            } finally {
                withContext(NonCancellable) {
                    if (current === this@RfidScanner) {
                        close()
                        current = null
                    }
                }
            }
        }
    }

    override fun start() {
        scope.launch {
            if (!active || current !== this@RfidScanner) return@launch
            if (keyCallback == null) registerKeys()
            if (scanning) stopAndRelease() else startScan()
        }
    }

    private fun startScan() {
        if (!active || scanning) return
        try {
            val device = open()
            if (!scope.isActive) throw CancellationException()
            check(device.setParameters(UHFService.PARAMETER_CLEAR_EPCLIST, 1)) {
                "清空设备标签缓存失败"
            }
            check(device.inventoryStart()) { "启动盘点失败" }
            scanning = true
            sessionTags.clear()
            publishError(null)
            publishState("正在读取 RFID 标签")
            pollJob = scope.launch {
                try {
                    val binder = serverBinder(device)
                    while (isActive && scanning) {
                        check(binder?.isBinderAlive != false) {
                            "系统 UHF 服务连接已断开，请松开后重新按下扫描键"
                        }
                        device.tagIDs.orEmpty()
                            .mapNotNull { it?.getId()?.trim()?.takeIf(String::isNotEmpty) }
                            .forEach { epc ->
                                if (sessionTags.add(epc)) {
                                    lifetime.launch(Dispatchers.Main.immediate) { onTagRead(epc) }
                                }
                            }
                        delay(POLL_INTERVAL.milliseconds)
                    }
                } catch (error: Throwable) {
                    if (error is CancellationException) throw error
                    stopAndRelease()
                    report(error)
                }
            }
        } catch (error: Throwable) {
            if (error is CancellationException) throw error
            stopAndRelease()
            report(error)
        }
    }

    /**
     * 每次停止均关闭 UHF；页面仍在前台时保留实体键监听，下一次 start() 可重新打开。
     */
    private fun stopAndRelease() {
        pollJob?.cancel()
        pollJob = null
        scanning = false
        startedByKey = false
        heldKeys.clear()
        try {
            service?.let { check(it.inventoryStop()) { "停止盘点失败" } }
        } catch (error: Throwable) {
            report(error)
        }
        try {
            service?.close()
        } catch (error: Throwable) {
            report(error)
        } finally {
            service = null
            sessionTags.clear()
            publishState("扫描已停止，UHF 已释放")
        }
    }

    private fun close() {
        active = false
        unregisterKeys()
        stopAndRelease()
    }

    /**
     * 保留已验证固件所需的打开间隔与功率重试；调用始终位于扫描线程。
     */
    private fun open(): UHFService {
        val device = UHFService.getInstance()
        try {
            reconnectIfNeeded(device)
            runCatching { device.close() }
            Thread.sleep(50L)
            check(device.open()) { "打开 UHF 设备失败" }
            Thread.sleep(100L)
            check(device.isOpen || device.open()) { "UHF 模块未就绪" }
            var powerReady = false
            for (attempt in 1..3) {
                if (device.setPower(DEFAULT_POWER)) {
                    powerReady = true
                    break
                }
                Thread.sleep(50L * attempt)
            }
            check(powerReady) { "设置发射功率失败" }
            service = device
            return device
        } catch (error: Throwable) {
            runCatching { device.close() }
            throw error
        }
    }

    /**
     * 当前固件的 UHFService 单例不会自行刷新失效 Binder；其他固件按标准接口连接。
     */
    private fun reconnectIfNeeded(device: UHFService) {
        val field = try {
            UHFService::class.java.getDeclaredField("server").apply { isAccessible = true }
        } catch (_: NoSuchFieldException) {
            return
        }
        if ((field.get(device) as? IBinder)?.isBinderAlive == true) return
        val init = UHFService::class.java.getDeclaredMethod("init").apply { isAccessible = true }
        val connected = init.invoke(device) as? Boolean ?: false
        check(connected && (field.get(device) as? IBinder)?.isBinderAlive == true) {
            "系统 UHF 服务正在恢复，请稍后重新按下扫描键"
        }
    }

    private fun serverBinder(device: UHFService): IBinder? = runCatching {
        UHFService::class.java.getDeclaredField("server").apply { isAccessible = true }
            .get(device) as? IBinder
    }.getOrNull()

    private fun registerKeys() {
        try {
            val keys = ScanKeyService.getInstance()
            val callback = object : IKeyEventCallback.Stub() {
                override fun onKeyDown(keyCode: Int) = handleKey(keyCode, true, this)
                override fun onKeyUp(keyCode: Int) = handleKey(keyCode, false, this)
            }
            keyService = keys
            keyCallback = callback
            keys.registerCallback(callback, SCAN_KEYS.joinToString(","))
        } catch (error: Throwable) {
            unregisterKeys()
            report(error)
        }
    }

    private fun handleKey(code: Int, down: Boolean, source: IKeyEventCallback) {
        scope.launch {
            if (!active || current !== this@RfidScanner || keyCallback !== source || code !in SCAN_KEYS) return@launch
            if (down) {
                if (heldKeys.add(code) && !scanning) {
                    startedByKey = true
                    startScan()
                }
            } else if (heldKeys.remove(code) && heldKeys.isEmpty() && startedByKey) {
                // 松开实体键只停止由实体键启动的扫描，不能中断按钮启动的扫描。
                stopAndRelease()
            }
        }
    }

    private fun unregisterKeys() {
        val callback = keyCallback
        keyCallback = null
        heldKeys.clear()
        try {
            if (callback != null) keyService?.unregisterCallback(callback)
        } catch (error: Throwable) {
            Log.e(TAG, "注销扫描键失败", error)
        } finally {
            keyService = null
        }
    }

    private fun report(error: Throwable) {
        if (error is CancellationException) throw error
        Log.e(TAG, "UHF 调用失败", error)
        publishError("RFID 失败：${error.message ?: error.javaClass.simpleName}")
        publishState("设备异常，可点击扫描重试")
    }

    private fun publishState(status: String) {
        val isScanning = scanning
        lifetime.launch(Dispatchers.Main.immediate) { onStateChanged(isScanning, status) }
    }

    private fun publishError(error: String?) {
        lifetime.launch(Dispatchers.Main.immediate) { onError(error) }
    }

    private companion object {
        const val TAG = "RfidScanner"
        const val POLL_INTERVAL = 100L
        const val DEFAULT_POWER = 20
        val SCAN_KEYS = setOf(142, 248, 249, 250, 252)
        val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        var current: RfidScanner? = null
    }
}
