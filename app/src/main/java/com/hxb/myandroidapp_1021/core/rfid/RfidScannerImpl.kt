package com.hxb.myandroidapp_1021.core.rfid

import android.util.Log
import com.seuic.scankey.IKeyEventCallback
import com.seuic.scankey.ScanKeyService
import com.seuic.uhf.UHFService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class RfidScannerImpl @Inject constructor() : RfidScanner {
    private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val sessionMutex = Mutex()
    private var preparedReader: UHFService? = null

    override fun scanKeyInput(): Flow<ScanInput?> = callbackFlow {
        val keys = try {
            ScanKeyService.getInstance()
        } catch (failure: LinkageError) {
            throw IllegalStateException("当前设备不支持东集实体扫描键", failure)
        }
        val heldKeys = mutableSetOf<Int>()
        var activeInput: ScanInput? = null
        val callback = object : IKeyEventCallback.Stub() {
            override fun onKeyDown(keyCode: Int) = updatePressed(keyCode, true)

            override fun onKeyUp(keyCode: Int) = updatePressed(keyCode, false)

            private fun updatePressed(keyCode: Int, pressed: Boolean) {
                if (keyCode !in scanKeyCodes) return
                synchronized(heldKeys) {
                    if (pressed) heldKeys.add(keyCode) else heldKeys.remove(keyCode)
                    val input = when {
                        barcodeKeyCode in heldKeys -> ScanInput.BARCODE
                        heldKeys.isNotEmpty() -> ScanInput.RFID
                        else -> null
                    }
                    if (input == activeInput) return
                    activeInput = input
                    trySend(input)
                }
            }
        }
        try {
            keys.registerCallback(callback, scanKeyCodes.joinToString(","))
            awaitClose()
        } finally {
            runCatching { keys.unregisterCallback(callback) }
                .onFailure { Log.w("RfidScanner", "注销实体扫描键失败", it) }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun prepareReader() {
        withContext(dispatcher) {
            sessionMutex.withLock { openReader() }
        }
    }

    override suspend fun releaseReader() {
        withContext(dispatcher) {
            sessionMutex.withLock { closeReader() }
        }
    }

    override fun scanEpcs(onInventoryChanged: (Boolean) -> Unit): Flow<String> = flow {
        sessionMutex.withLock {
            val reader = openReader()
            try {
                check(reader.setParameters(UHFService.PARAMETER_CLEAR_EPCLIST, 1)) {
                    "无法清空 RFID 读头的上次扫描结果"
                }
                currentCoroutineContext().ensureActive()
                check(reader.inventoryStart()) { "无法开始 RFID 扫描" }
                try {
                    onInventoryChanged(true)
                    val seenEpcs = mutableSetOf<String>()
                    while (true) {
                        for (tag in reader.tagIDs.orEmpty()) {
                            val epc = tag?.getId()?.trim()?.uppercase().orEmpty()
                            if (epc.isNotEmpty() && seenEpcs.add(epc)) emit(epc)
                        }
                        delay(100L.milliseconds)
                    }
                } finally {
                    onInventoryChanged(false)
                    reader.inventoryStop()
                }
            } catch (failure: CancellationException) {
                throw failure
            } catch (failure: Exception) {
                closeReader()
                throw failure
            }
        }
    }.flowOn(dispatcher)

    private fun openReader(): UHFService {
        preparedReader?.let { return it }
        val reader = try {
            UHFService.getInstance()
        } catch (failure: LinkageError) {
            throw IllegalStateException("当前设备不支持东集 RFID 扫描", failure)
        }
        check(reader.open()) { "无法打开 RFID 读头" }
        preparedReader = reader
        return reader
    }

    private fun closeReader() {
        val reader = preparedReader ?: return
        preparedReader = null
        runCatching { reader.close() }.onFailure { Log.w("RfidScanner", "关闭 RFID 读头失败", it) }
    }

    private companion object {
        // UTouch 的 UHF 按键为 248，扳机为 250
        const val barcodeKeyCode = 249
        val scanKeyCodes = setOf(248, barcodeKeyCode, 250)
    }
}
