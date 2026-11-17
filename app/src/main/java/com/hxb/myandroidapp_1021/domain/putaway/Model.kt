package com.hxb.myandroidapp_1021.domain.putaway

import kotlinx.serialization.Serializable

@Serializable
data class PutawayOrder(
    val inboundOrderNo: String,
    val orderNo: String,
    val status: String,
    val operator: String,
    val putawayTime: String,
    val details: List<PutawayDetail>
) {
    val completedCount: Int get() = details.count { it.isCompleted }
    val isCompleted: Boolean get() = details.isNotEmpty() && completedCount == details.size
}

@Serializable
data class PutawayDetail(
    val orderNo: String,
    val inboundLineNo: String,
    val lineNo: String,
    val status: String,
    val batchNo: String,
    val rfid: String,
    val itemCode: String,
    val description: String,
    val unit: String,
    val warehouse: String,
    val location: String,
    val inventoryStatus: String
) {
    val isCompleted: Boolean get() = status == "已上架"
}

@Serializable
data class PutawayRequest(
    val orderNo: String,
    val lineNo: String,
    val rfid: String,
    val location: String
)

@Serializable
data class PutawayLocation(val code: String, val warehouse: String)
