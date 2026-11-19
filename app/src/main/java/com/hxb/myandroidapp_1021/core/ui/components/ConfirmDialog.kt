package com.hxb.myandroidapp_1021.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

/**
 * 调用页面控制显示状态并处理确认和取消事件，弹窗只负责展示。
 * dialogPadding 统一控制内容四周留白，默认比 Material3 AlertDialog 的 24dp 更紧凑。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmDialog(
    title: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    confirmText: String = "确认",
    dismissText: String = "取消",
    dialogPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.wrapContentHeight()
    ) {
        Surface(
            shape = RectangleShape,
            color = Color.White,
            tonalElevation = 0.dp
        ) {
            Column(modifier = Modifier.padding(dialogPadding)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                ProvideTextStyle(value = MaterialTheme.typography.bodyMedium) {
                    CompositionLocalProvider(
                        LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant
                    ) {
                        Box(modifier = Modifier.padding(bottom = 16.dp)) {
                            content()
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(shape = RoundedCornerShape(4.dp), onClick = onDismiss) {
                        Text(dismissText, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    TextButton(shape = RoundedCornerShape(4.dp), onClick = onConfirm) {
                        Text(confirmText, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
