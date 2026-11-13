package com.hxb.myandroidapp_1021.ui.commonmodel

import androidx.compose.ui.graphics.Color

/**
 * 两个以上业务模块使用的前端展示字段，不是数据库或接口模型。
 */
data class DisplayField(
    val label: String,
    val value: String
)

/**
 * 多模块共用的前端页签，不是工具业务状态。
 */
enum class ScanTab {
    All,      // 全部
    Read,     // 已读取
    Unread    // 未读取
}

/**
 * 只读工器具表格里的一行。
 *
 * @param rfid 第一行：RFID
 * @param name 第二行：名称，留空时只显示 RFID
 * @param quantity 右列显示的数量，value 为空时才显示
 * @param value 右列自定义文字（如盘点的「已盘 / 盘亏」），不为空时优先显示它
 * @param valueColor 右列文字颜色，null 用默认深色
 */
data class ToolInfoRow(
    val rfid: String,
    val name: String = "",
    val quantity: Int = 1,
    val value: String? = null,
    val valueColor: Color? = null
)
