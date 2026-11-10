package com.hxb.myandroidapp_1021.data.models

/**
 * 盘点结果里的一件工器具：工具信息 + 盘点结果
 */
data class InventoryResultItem(
    val tool: ToolScanItem,
    val result: String
)
