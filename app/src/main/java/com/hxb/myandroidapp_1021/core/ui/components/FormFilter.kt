package com.hxb.myandroidapp_1021.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 筛选抽屉骨架：标题、关闭、内容区、错误提示和底部重置/确定。
 * 字段控件由调用页面通过 content 传入，组件不持有筛选状态。
 * heightFraction 非空时抽屉高度固定为可用高度的该比例，按钮固定在底部；未指定时随内容高度变化。
 * closeRequest 为 true 时先播放收起动画再回调 onClosed，供确认提交后需要先关抽屉再跳转的页面使用。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormFilter(
    error: String?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    onReset: (() -> Unit)? = null,
    title: String = "筛选",
    resetText: String = "重置",
    confirmText: String = "确定",
    confirmEnabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    heightFraction: Float? = null,
    closeRequest: Boolean = false,
    onClosed: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    // 收起动画播放完再通知调用方，避免调用方立即跳转时抽屉还没缩回。
    LaunchedEffect(closeRequest) {
        if (closeRequest) {
            sheetState.hide()
            onClosed()
        }
    }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        dragHandle = null
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .then(
                    if (heightFraction != null) Modifier.fillMaxHeight(heightFraction) else Modifier
                )
                .imePadding()
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, Modifier.weight(1f), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = onDismiss, shape = RoundedCornerShape(4.dp)) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            if (heightFraction == null) {
                Column(
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(contentPadding),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    content()
                    error?.let { Text(it, fontSize = 13.sp, color = MaterialTheme.colorScheme.error) }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    FilterActionsRow(
                        onReset = onReset,
                        onConfirm = onConfirm,
                        resetText = resetText,
                        confirmText = confirmText,
                        confirmEnabled = confirmEnabled
                    )
                }
            } else {
                Column(
                    Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(contentPadding),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    content()
                    error?.let { Text(it, fontSize = 13.sp, color = MaterialTheme.colorScheme.error) }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                FilterActionsRow(
                    onReset = onReset,
                    onConfirm = onConfirm,
                    resetText = resetText,
                    confirmText = confirmText,
                    confirmEnabled = confirmEnabled,
                    modifier = Modifier.padding(contentPadding)
                )
            }
        }
    }
}

/**
 * 筛选抽屉底部操作行，重置按钮可选、确定按钮始终显示。
 */
@Composable
private fun FilterActionsRow(
    onReset: (() -> Unit)?,
    onConfirm: () -> Unit,
    resetText: String,
    confirmText: String,
    confirmEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (onReset != null) {
            FilterButton(
                text = resetText,
                onClick = onReset,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
            )
        }
        FilterButton(
            text = confirmText,
            onClick = onConfirm,
            enabled = confirmEnabled,
            modifier = Modifier
                .weight(1f)
                .height(44.dp),
            prominent = true
        )
    }
}
