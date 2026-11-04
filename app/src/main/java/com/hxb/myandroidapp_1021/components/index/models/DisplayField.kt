package com.hxb.myandroidapp_1021.components.index.models

/**
 * 列表卡片里的一行「标签：值」。
 * 标签列宽度由 OrderListPage 统一控制，保证同一张卡片里多行值左对齐。
 */
data class DisplayField(val label: String, val value: String)
