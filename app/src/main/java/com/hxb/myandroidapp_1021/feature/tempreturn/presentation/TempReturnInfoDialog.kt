package com.hxb.myandroidapp_1021.feature.tempreturn.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxb.myandroidapp_1021.core.ui.components.ConfirmDialog
import com.hxb.myandroidapp_1021.core.ui.components.InputFieldOne

/**
 * 归还信息登记弹窗：登记归还人、工单号、归还库位与备注。
 * 留白和按钮沿用 ConfirmDialog，必填项缺失时在弹窗内容区提示，确定后由调用页面保存。
 */
@Composable
fun TempReturnInfoDialog(
    returner: String,
    workOrderNo: String,
    returnLocation: String,
    remark: String,
    error: String?,
    onReturnerChange: (String) -> Unit,
    onWorkOrderNoChange: (String) -> Unit,
    onReturnLocationChange: (String) -> Unit,
    onRemarkChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    ConfirmDialog(
        title = "登记归还信息",
        onConfirm = onConfirm,
        onDismiss = onDismiss
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            InputFieldOne(
                value = returner,
                onValueChange = onReturnerChange,
                placeholder = "归还人（必填）",
                showSearchIcon = false,
                horizontalPadding = 0.dp
            )
            InputFieldOne(
                value = workOrderNo,
                onValueChange = onWorkOrderNoChange,
                placeholder = "工单号",
                showSearchIcon = false,
                horizontalPadding = 0.dp
            )
            InputFieldOne(
                value = returnLocation,
                onValueChange = onReturnLocationChange,
                placeholder = "归还库位（必填）",
                showSearchIcon = false,
                horizontalPadding = 0.dp
            )
            InputFieldOne(
                value = remark,
                onValueChange = onRemarkChange,
                placeholder = "备注",
                showSearchIcon = false,
                horizontalPadding = 0.dp
            )
            if (error != null) {
                Text(
                    text = error,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
