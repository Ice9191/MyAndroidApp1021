package com.hxb.myandroidapp_1021.utils

import android.os.IBinder
import android.util.Log
import com.hxb.myandroidapp_1021.utils.TestRfidScan.Companion.TAG
import com.seuic.scankey.IKeyEventCallback
import com.seuic.scankey.ScanKeyService
import com.seuic.uhf.UHFService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import kotlin.time.Duration.Companion.milliseconds

/**
 * RFID 扫描测试工具。
 *
 * 对外只提供 [start] 方法：
 * - 屏幕按钮每次点击调用 start()，内部按调用次数切换：第 1 次开始扫描，第 2 次停止并释放资源，第 3 次重新开始，如此反复。
 * - 构造时绑定 PDA 扳机与侧边 SCAN 键：按下开始扫描，全部松开后停止扫描并释放 UHF 资源。
 * - 扫描结果与状态通过 Logcat 标签 [TAG] 输出，便于在没有界面接入时验证硬件读签。
 */
class TestRfidScan {
    // UHF 与实体键操作统一在单线程执行，避免并发打开、关闭设备
    private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    private var service: UHFService? = null
    private var pollJob: Job? = null
    private var scanning = false
    private var startedByKey = false
    private var keyService: ScanKeyService? = null
    private var keyCallback: IKeyEventCallback? = null
    private val heldKeys = mutableSetOf<Int>()
    private val sessionTags = mutableSetOf<String>()

    init {
        // 提前绑定实体键，保证不点按钮也能直接按扳机/侧边键开始扫描
        scope.launch { registerKeys() }
    }

    /**
     * 唯一对外入口：按钮点击与业务流程都调用本方法。
     *
     * 内部按调用次数切换扫描状态：第 1、3、5… 次开启，第 2、4、6… 次关闭。
     * 关闭时停止盘点并关闭 UHF 设备，释放读头资源；实体键监听保留，便于下次按下重新开始。
     */
    fun start() {
        scope.launch {
            if (keyCallback == null) registerKeys()
            if (scanning) stopAndRelease() else startScan()
        }
    }

    /**
     * 打开 UHF 并开始盘点。
     */
    private fun startScan() {
        if (scanning) return
        try {
            val device = open()
            check(device.setParameters(UHFService.PARAMETER_CLEAR_EPCLIST, 1)) {
                "清空设备标签缓存失败"
            }
            check(device.inventoryStart()) { "启动盘点失败" }
            scanning = true
            sessionTags.clear()
            Log.i(TAG, "开始扫描")
            pollJob = scope.launch {
                try {
                    val binder = serverBinder(device)
                    while (isActive && scanning) {
                        // 系统 UHF 服务崩溃后 binder 会失效，此时停止并提示重新按键
                        check(binder?.isBinderAlive != false) {
                            "系统 UHF 服务连接已断开，请松开后重新按下扫描键"
                        }
                        val epcs = device.tagIDs
                            .orEmpty()
                            .mapNotNull { it?.getId()?.trim()?.takeIf(String::isNotEmpty) }
                        epcs.forEach { epc ->
                            if (sessionTags.add(epc)) {
                                Log.i(TAG, "读到标签：$epc")
                            }
                        }
                        delay(POLL_INTERVAL.milliseconds)
                    }
                } catch (t: Throwable) {
                    if (t is CancellationException) throw t
                    Log.e(TAG, "读取标签失败", t)
                    stopAndRelease()
                }
            }
        } catch (t: Throwable) {
            if (t is CancellationException) throw t
            Log.e(TAG, "开始扫描失败", t)
            stopAndRelease()
        }
    }

    /**
     * 停止盘点并释放 UHF 资源；实体键监听保留，下次按下可重新开始。
     */
    private fun stopAndRelease() {
        val wasScanning = scanning
        scanning = false
        startedByKey = false
        pollJob?.cancel()
        pollJob = null
        try {
            service?.let { device ->
                if (!device.inventoryStop()) {
                    Log.w(TAG, "停止盘点返回失败")
                }
            }
        } catch (t: Throwable) {
            Log.e(TAG, "停止盘点失败", t)
        }
        try {
            service?.close()
        } catch (t: Throwable) {
            Log.e(TAG, "关闭 UHF 设备失败", t)
        } finally {
            service = null
            if (wasScanning) {
                Log.i(TAG, "扫描已停止，UHF 资源已释放，本次共读到 ${sessionTags.size} 个标签")
            }
            sessionTags.clear()
        }
    }

    /**
     * 打开并初始化 UHF 读头；失败时保证不残留半打开的设备。
     */
    private fun open(): UHFService {
        val device = service ?: UHFService.getInstance()
        try {
            reconnectIfNeeded(device)
            runCatching { device.close() }
            Thread.sleep(50L)
            check(device.open()) { "打开 UHF 设备失败" }
            Thread.sleep(100L)
            check(device.isOpen || device.open()) { "UHF 模块未就绪" }
            check(setPower(device)) { "设置发射功率失败" }
            service = device
            return device
        } catch (t: Throwable) {
            service = null
            runCatching { device.close() }
            throw t
        }
    }

    /**
     * 设备偶尔首次设置功率失败，按重试次数补设。
     */
    private fun setPower(device: UHFService): Boolean {
        for (attempt in 1..3) {
            if (device.setPower(DEFAULT_POWER)) return true
            Log.w(TAG, "设置功率第 $attempt 次失败，重试")
            Thread.sleep(50L * attempt)
        }
        return false
    }

    /**
     * 官方固件的 UHFService 单例在系统服务重启后不会刷新 Binder，这里按已知固件实现重新连接；
     * 其他固件没有 server 字段时直接走标准接口。
     */
    private fun reconnectIfNeeded(device: UHFService) {
        val serverField = try {
            UHFService::class.java.getDeclaredField("server").apply { isAccessible = true }
        } catch (_: NoSuchFieldException) {
            return
        }
        val binder = serverField.get(device) as? IBinder
        if (binder?.isBinderAlive == true) return
        Log.w(TAG, "UHF Binder 已失效，重新连接系统服务")
        val init = UHFService::class.java.getDeclaredMethod("init").apply { isAccessible = true }
        val connected = init.invoke(device) as? Boolean ?: false
        val refreshed = serverField.get(device) as? IBinder
        check(connected && refreshed?.isBinderAlive == true) {
            "系统 UHF 服务正在恢复，请稍后重新按下扫描键"
        }
        Log.i(TAG, "UHF 服务重新连接成功")
    }

    private fun serverBinder(device: UHFService): IBinder? = runCatching {
        UHFService::class.java.getDeclaredField("server").apply { isAccessible = true }
            .get(device) as? IBinder
    }.getOrNull()

    /**
     * 绑定扳机与侧边 SCAN 键；非 PDA 设备没有系统库时只记录日志，不影响按钮调用。
     */
    private fun registerKeys() {
        if (keyCallback != null) return
        try {
            val keys = ScanKeyService.getInstance()
            val callback = object : IKeyEventCallback.Stub() {
                override fun onKeyDown(keyCode: Int) = handleKey(keyCode, true)
                override fun onKeyUp(keyCode: Int) = handleKey(keyCode, false)
            }
            keyService = keys
            keyCallback = callback
            keys.registerCallback(callback, SCAN_KEYS.joinToString(","))
            Log.i(TAG, "实体键已绑定：按住扳机或侧边 SCAN 键开始扫描，松开停止")
        } catch (t: Throwable) {
            keyCallback = null
            keyService = null
            Log.e(TAG, "实体键绑定失败（非 PDA 设备可忽略）", t)
        }
    }

    /**
     * 实体键按下开始扫描，全部松开后停止并释放；按键事件统一投递到扫描线程处理。
     *
     * 只停止由实体键发起的扫描：按钮已开启扫描时，误碰扳机不会把它关掉，按钮的奇偶切换保持独立。
     */
    private fun handleKey(keyCode: Int, down: Boolean) {
        if (keyCode !in SCAN_KEYS) return
        scope.launch {
            if (down) {
                Log.i(TAG, "实体键 $keyCode 按下")
                heldKeys.add(keyCode)
                if (!scanning) {
                    startedByKey = true
                    startScan()
                }
            } else {
                Log.i(TAG, "实体键 $keyCode 松开")
                heldKeys.remove(keyCode)
                if (heldKeys.isEmpty() && scanning && startedByKey) {
                    stopAndRelease()
                }
            }
        }
    }

    companion object {
        private const val TAG = "TestRfidScan"
        private const val POLL_INTERVAL = 100L
        private const val DEFAULT_POWER = 20
        private val SCAN_KEYS = setOf(142, 248, 249, 250, 252)
    }
}
