package com.hxb.myandroidapp_1021.ui.inventory

import com.hxb.myandroidapp_1021.ui.common.ToolScanItem

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

/**
 * 盘点结果里的一件工器具：工具信息 + 盘点结果
 */
data class InventoryResultItem(
    val tool: ToolScanItem,
    val result: String
)
