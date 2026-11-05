package com.hxb.myandroidapp_1021.components.index.models

/**
 * 扫码页里的一件工器具（拣选 / 归还 / 送检 / 送修 / 报废 / 报废出库 / 盘点共用）
 *
 * @param pickDate 读取日期，不同业务写入的都是这一列（拣选日期 / 归还日期 / 送检日期…）
 * @param isChecked 「已读取 / 未读取」表格里的勾选状态
 */
data class ToolScanItem(
    val id: String,
    val name: String = "",
    val code: String = "",
    val rfid: String,
    var isChecked: Boolean = false,
    val pickDate: String = ""
)
