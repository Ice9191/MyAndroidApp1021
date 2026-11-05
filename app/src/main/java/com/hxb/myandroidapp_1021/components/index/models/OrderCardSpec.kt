package com.hxb.myandroidapp_1021.components.index.models

import androidx.compose.ui.graphics.Color

/**
 * 列表页一张单据卡片要展示什么、按钮点了干什么、怎么匹配搜索词。
 *
 * @param orderNo 取单号
 * @param fields 取卡片里的「标签 值」明细行
 * @param statusText 单号右侧的状态文字，返回 null 则不显示
 * @param statusColor 状态文字颜色
 * @param actionText 单号右侧按钮文字，null 则不显示按钮
 * @param onActionClick 按钮点击
 * @param searchMatcher 搜索匹配规则，null 表示不按搜索词过滤
 */
data class OrderCardSpec<T>(
    val orderNo: (T) -> String,
    val fields: (T) -> List<DisplayField>,
    val statusText: (T) -> String? = { null },
    val statusColor: (T) -> Color = { Color(0xFF1E71C8) },
    val actionText: String? = null,
    val onActionClick: (T) -> Unit = {},
    val searchMatcher: ((T, String) -> Boolean)? = null
)
