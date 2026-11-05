package com.hxb.myandroidapp_1021.components.index.models

import androidx.compose.ui.graphics.Color

/**
 * 只读工器具表格里的一行。
 *
 * @param rfid 第一行：RFID
 * @param name 第二行：名称（表格 showName = false 时不显示）
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
