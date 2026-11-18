package com.hxb.myandroidapp_1021.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * 筛选面板里的单选项标签，选中态与事件由调用页面提供。
 */
@Composable
fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterButton(text = text, onClick = onClick, modifier = modifier, selected = selected)
}
