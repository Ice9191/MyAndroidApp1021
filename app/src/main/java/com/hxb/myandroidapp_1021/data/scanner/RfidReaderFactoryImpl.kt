package com.hxb.myandroidapp_1021.data.scanner

import com.hxb.myandroidapp_1021.domain.scanner.RfidReader
import com.hxb.myandroidapp_1021.domain.scanner.RfidReaderFactory
import com.hxb.myandroidapp_1021.domain.scanner.ScannerState
import com.hxb.myandroidapp_1021.utils.RfidScanner
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RfidReaderFactoryImpl @Inject constructor() : RfidReaderFactory {
    override fun create(
        lifetime: CoroutineScope,
        onTagRead: (String) -> Unit,
        onStateChanged: (ScannerState) -> Unit
    ): RfidReader {
        var state = ScannerState()
        return try {
            Class.forName("com.seuic.uhf.UHFService")
            RfidScanner(
                lifetime = lifetime,
                onTagRead = onTagRead,
                onStateChanged = { scanning, status ->
                    state = state.copy(isScanning = scanning, status = status)
                    onStateChanged(state)
                },
                onError = { error ->
                    state = state.copy(errorMessage = error)
                    onStateChanged(state)
                }
            )
        } catch (_: ClassNotFoundException) {
            onStateChanged(ScannerState(errorMessage = "当前设备未提供 UHF 服务，可使用人工核对或键盘扫描输入"))
            RfidReader {}
        } catch (_: LinkageError) {
            onStateChanged(ScannerState(errorMessage = "当前设备的 UHF 服务不兼容，可使用人工核对或键盘扫描输入"))
            RfidReader {}
        }
    }
}
