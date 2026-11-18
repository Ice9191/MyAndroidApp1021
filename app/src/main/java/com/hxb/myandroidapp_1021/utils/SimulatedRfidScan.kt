package com.hxb.myandroidapp_1021.utils

/**
 * 模拟一次 RFID 读取：从当前候选中随机返回一个尚未扫描的 RFID。
 * 正式接入读头后，用真实扫描结果替换本工具。
 */
object SimulatedRfidScan {
    fun scan(candidates: List<String>, scannedRfids: Set<String>): String? =
        candidates.filterNot { it in scannedRfids }.randomOrNull()
}
