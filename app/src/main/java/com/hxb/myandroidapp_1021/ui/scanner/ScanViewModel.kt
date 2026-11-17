package com.hxb.myandroidapp_1021.ui.scanner

import androidx.lifecycle.ViewModel
import com.hxb.myandroidapp_1021.domain.scanner.RfidReader
import com.hxb.myandroidapp_1021.domain.scanner.RfidReaderFactory
import com.hxb.myandroidapp_1021.domain.scanner.ScannerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class ScanViewModel(private val factory: RfidReaderFactory) : ViewModel() {
    private var reader: RfidReader? = null
    private var session: CoroutineScope? = null
    private val _scannerState = MutableStateFlow(ScannerState())
    val scannerState = _scannerState.asStateFlow()

    protected abstract fun onTagRead(rfid: String)
    /**
     * 页面提供前台作用域；旧会话的回调不能覆盖恢复后的页面状态。
     */
    fun onScannerSession(lifetime: CoroutineScope?) {
        session = lifetime
        reader = lifetime?.let { current ->
            factory.create(
                current,
                onTagRead = { if (session === current) onTagRead(it) },
                onStateChanged = { if (session === current) _scannerState.value = it }
            )
        }
        if (lifetime == null) _scannerState.value = ScannerState(status = "扫描已停止")
    }

    fun startScanner() {
        reader?.start()
    }
}
