package com.hxb.myandroidapp_1021.utils

import com.hxb.myandroidapp_1021.feature.putaway.domain.model.PutawayDetail

/**
 * 模拟上架：同一物资的所有 RFID 都已扫描时，视为该物资上架成功，从剩余明细中移除。
 * 正式接入接口后，用后端上架结果替换本工具。
 */
object SimulatedPutaway {
    fun complete(
        details: List<PutawayDetail>,
        scannedRfids: Set<String>,
        materialKey: (PutawayDetail) -> String
    ): List<PutawayDetail> {
        val completedMaterialKeys = details
            .groupBy(materialKey)
            .filterValues { group -> group.all { it.rfid in scannedRfids } }
            .keys
        return details.filterNot { materialKey(it) in completedMaterialKeys }
    }
}
