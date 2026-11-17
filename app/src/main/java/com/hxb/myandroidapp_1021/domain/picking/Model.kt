package com.hxb.myandroidapp_1021.domain.picking

import kotlinx.serialization.Serializable

@Serializable
data class PickingOrder(
    val orderNo: String,
    val status: PickingStatus,
    val workshop: String,
    val borrower: String,
    val pendingCount: Int,
    val plannedDate: String,
    val totalCount: Int,
    val pickedCount: Int,
    val workOrderNo: String = "",
    val createdBy: String = "",
    val createdAt: String = "",
    val borrowOrderNo: String = "",
    val team: String = ""
)

@Serializable
enum class PickingStatus(val label: String) {
    PICKED("已拣选"),
    UNPICKED("未拣选"),
    PARTIALLY_PICKED("部分拣选"),
    CLOSED("已关闭")
}

@Serializable
data class PickingTool(
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
data class PickingOrderDetail(val order: PickingOrder, val tools: List<PickingTool>)

@Serializable
data class PickingConfirmRequest(
    val orderNo: String,
    val rfids: List<String>
)
