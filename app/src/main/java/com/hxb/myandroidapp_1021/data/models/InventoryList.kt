package com.hxb.myandroidapp_1021.data.models

/**
 * 盘点任务单（列表展示 + 盘点状态）
 */
data class InventoryRecord(
    val orderNo: String,
    val team: String,
    val person: String,
    val toolCount: Int,
    val inventoryDate: String,
    val status: String   // 已盘点 / 未盘点
)
