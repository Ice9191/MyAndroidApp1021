package com.hxb.myandroidapp_1021.core.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 通用筛选按钮：筛选抽屉的确定/重置和筛选标签共用。
 */
@Composable
internal fun FilterButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    prominent: Boolean = false,
    enabled: Boolean = true
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(4.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
        colors = ButtonDefaults.textButtonColors(
            containerColor = when {
                prominent -> MaterialTheme.colorScheme.primary
                selected -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.background
            },
            contentColor = when {
                prominent -> MaterialTheme.colorScheme.onPrimary
                selected -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    ) { Text(text, fontSize = 13.sp) }
}
