package com.hxb.myandroidapp_1021.domain.scanner

import kotlinx.coroutines.CoroutineScope

data class ScannerState(
    val isScanning: Boolean = false,
    val status: String = "等待初始化",
    val errorMessage: String? = null
)

fun interface RfidReader {
    fun start()
}

interface RfidReaderFactory {
    fun create(
        lifetime: CoroutineScope,
        onTagRead: (String) -> Unit,
        onStateChanged: (ScannerState) -> Unit
    ): RfidReader
}
