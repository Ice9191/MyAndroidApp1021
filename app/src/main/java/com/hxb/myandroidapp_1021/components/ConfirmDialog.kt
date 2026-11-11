package com.hxb.myandroidapp_1021.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 调用页面控制显示状态并处理确认和取消事件，弹窗只负责展示。
 */
@Composable
fun ConfirmDialog(
    title: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    confirmText: String = "确认",
    dismissText: String = "取消",
    content: @Composable () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = content,
        containerColor = Color.White,
        confirmButton = {
            TextButton(shape = RoundedCornerShape(4.dp), onClick = onConfirm) {
                Text(confirmText, color = Color(0xFF1E71C8))
            }
        },
        dismissButton = {
            TextButton(shape = RoundedCornerShape(4.dp), onClick = onDismiss) {
                Text(dismissText, color = Color(0xFF888888))
            }
        }
    )
}

