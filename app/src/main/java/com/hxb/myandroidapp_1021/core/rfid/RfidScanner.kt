package com.hxb.myandroidapp_1021.core.rfid

import kotlinx.coroutines.flow.Flow

interface RfidScanner {
    suspend fun prepareReader()

    suspend fun releaseReader()

    /**
     * 采集原始 EPC，停止收集时结束当前扫描会话。
     */
    fun scanEpcs(onInventoryChanged: (Boolean) -> Unit): Flow<String>

    /**
     * 监听实体扫描键，返回当前按键对应的读头。所有键松开时返回 null。
     */
    fun scanKeyInput(): Flow<ScanInput?>
}

enum class ScanInput {
    RFID,
    BARCODE
}
