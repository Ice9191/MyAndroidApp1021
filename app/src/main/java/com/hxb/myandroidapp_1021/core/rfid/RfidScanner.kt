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
     * 每次实体键按下时发送对应的读头，松开后才能再次触发同一个键。
     */
    fun scanKeyInput(): Flow<ScanInput>
}

enum class ScanInput {
    RFID,
    BARCODE
}
