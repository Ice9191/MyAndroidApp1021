package com.hxb.myandroidapp_1021.pages.picking.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.hxb.myandroidapp_1021.components.ConfirmDialog

/**
 * 关闭拣选单前的确认弹窗，提示剩余未拣选明细将无法继续处理。
 */
@Composable
fun CloseDialog(
    remaining: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    ConfirmDialog(
        title = "关闭拣选单",
        confirmText = "确认关闭",
        onConfirm = onConfirm,
        onDismiss = onDismiss
    ) {
        Text("剩余${remaining}条未拣选明细将无法继续拣选，是否确认关闭？")
    }
}
