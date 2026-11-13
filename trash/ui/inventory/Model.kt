package com.hxb.myandroidapp_1021.ui.inventory

import androidx.compose.ui.graphics.Color
import com.hxb.myandroidapp_1021.ui.common.DisplayField

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

/**
 * 盘点页面使用的双列展示配置，颜色与排版不由后端返回。
 */
data class InfoRow(
    val left: DisplayField,
    val right: DisplayField,
    val leftBold: Boolean = false,
    val rightColor: Color? = null
)

/**
 * 盘点页面使用的工器具展示数据，不是接口模型。
 */
data class ToolScanItem(
    val id: String,
    val name: String = "",
    val code: String = "",
    val rfid: String,
    var isChecked: Boolean = false,
    val pickDate: String = ""
)
