package com.hxb.myandroidapp_1021.ui.common

import androidx.compose.ui.graphics.Color

/** 两个以上业务模块使用的前端展示字段，不是数据库或接口模型。 */
data class DisplayField(
    val label: String,
    val value: String
)

/** 两个以上业务模块使用的双列展示配置，颜色与排版不由后端返回。 */
data class InfoRow(
    val left: DisplayField,
    val right: DisplayField,
    val leftBold: Boolean = false,
    val rightColor: Color? = null
)

/** 多模块共用的前端页签，不是工具业务状态。 */
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

/**
 * 归还、送检、送修、报废、报废出库和盘点共用的工器具数据
 */
data class ToolScanItem(
    val id: String,
    val name: String = "",
    val code: String = "",
    val rfid: String,
    var isChecked: Boolean = false,
    val pickDate: String = ""
)
