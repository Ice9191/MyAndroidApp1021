package com.hxb.myandroidapp_1021.domain.returns

import kotlinx.serialization.Serializable

@Serializable
data class ReturnOrder(
    val orderNo: String,
    val status: ReturnStatus,
    val workshop: String,
    val returner: String,
    val pendingCount: Int,
    val returnedDate: String,
    val totalCount: Int,
    val returnedCount: Int,
    val workOrderNo: String = "",
    val createdBy: String = "",
    val createdAt: String = "",
    val returnOrderNo: String = "",
    val team: String = ""
)

@Serializable
enum class ReturnStatus(val label: String) {
    RETURNED("已上架"),
    UNRETURNED("待上架"),
    PARTIALLY_RETURNED("部分上架")
}

@Serializable
data class ReturnTool(
    val id: Int,
    val name: String,
    val itemCode: String,
    val rfid: String,
    val location: String,
    val unit: String,
    val warehouse: String,
    val condition: String,
    val completed: Boolean,
    val completedAt: String,
    val canProcess: Boolean,
    val blockedReason: String = ""
)

@Serializable
data class ReturnOrderDetail(val order: ReturnOrder, val tools: List<ReturnTool>)

@Serializable
data class ReturnConfirmRequest(
    val orderNo: String,
    val lineNo: Int,
    val rfid: String,
    val locationCode: String
)
