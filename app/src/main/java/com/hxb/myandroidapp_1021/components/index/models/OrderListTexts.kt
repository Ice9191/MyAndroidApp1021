package com.hxb.myandroidapp_1021.components.index.models

/**
 * 列表区的文案：搜索框提示、列表头左侧文字、空数据 / 无匹配提示。
 *
 * @param searchPlaceholder 搜索框提示文字
 * @param headerLabel 列表头左侧文字
 * @param emptyText 列表为空时的提示
 * @param noResultText 有搜索词但没匹配到时的提示
 */
data class OrderListTexts(
    val searchPlaceholder: String,
    val headerLabel: String? = null,
    val emptyText: String = "暂无数据",
    val noResultText: String = "暂无匹配数据"
)
