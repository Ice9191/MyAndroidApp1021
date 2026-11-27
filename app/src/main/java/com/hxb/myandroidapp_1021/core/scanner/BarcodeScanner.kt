package com.hxb.myandroidapp_1021.core.scanner

import android.content.Context
import com.seuic.scanner.Scanner.Status
import com.seuic.scanner.ScannerFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class BarcodeScanner @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    /**
     * SDK 回调结束单次扫码，应用超时用于回调未到达时清理会话。
     */
    suspend fun scan(onStarted: () -> Unit): String? = withTimeoutOrNull(5_000L.milliseconds) {
        callbackFlow {
            val scanner = try {
                ScannerFactory.getScanner(context, false)
            } catch (failure: LinkageError) {
                throw IllegalStateException("当前设备不支持东集光学扫码 SDK", failure)
            }
            check(scanner.open()) { "无法打开光学扫码头" }
            try {
                scanner.setDecodeInfoCallBack { result ->
                    val code = result?.barcode?.trim().orEmpty()
                    if (code.isNotEmpty()) trySend(code)
                }
                scanner.setStatusCallBack { status, _ ->
                    when (status) {
                        Status.STATUS_DECODE_TIMEOUT, Status.STATUS_DECODE_CANCELED -> channel.close()
                        Status.STATUS_ERROR -> channel.close(IllegalStateException("光学扫码失败"))
                    }
                }
                scanner.startScan()
                onStarted()
                awaitClose()
            } finally {
                try {
                    scanner.stopScan()
                } finally {
                    scanner.setDecodeInfoCallBack(null)
                    scanner.setStatusCallBack(null)
                }
            }
        }.firstOrNull()
    }
}
