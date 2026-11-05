package com.hxb.myandroidapp_1021.components.index.models

/**
 * 扫码页三个 Tab 的文案，默认值就是拣选业务的措辞；
 * 归还 / 送检 / 送修 / 报废 / 报废出库 / 盘点 用同名参数覆盖即可。
 *
 * @param date 「全部」Tab 卡片里日期那一行的标签
 * @param readStatus 已读取时的状态文字
 * @param unreadStatus 未读取时的状态文字
 * @param readAction 已读取 Tab 右上角按钮文字
 * @param unreadAction 未读取 Tab 右上角按钮文字
 */
data class ScanTabsLabels(
    val date: String = "拣选日期",
    val readStatus: String = "已拣选",
    val unreadStatus: String = "未拣选",
    val readAction: String = "取消读取",
    val unreadAction: String = "手动读取"
)
