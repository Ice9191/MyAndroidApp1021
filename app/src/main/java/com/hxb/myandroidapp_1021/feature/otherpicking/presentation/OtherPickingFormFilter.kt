package com.hxb.myandroidapp_1021.feature.otherpicking.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxb.myandroidapp_1021.core.ui.components.FilterChip
import com.hxb.myandroidapp_1021.core.ui.components.FormFilter
import com.hxb.myandroidapp_1021.feature.otherpicking.domain.model.PickingStatus
import com.hxb.myandroidapp_1021.feature.otherpicking.domain.model.SourceType

/**
 * 其他拣选筛选面板内容：上游类型与单据状态；骨架、标题和按钮由 FormFilter 提供。
 */
@Composable
fun OtherPickingFormFilter(
    filter: OtherPickingFilter,
    onChange: (OtherPickingFilter) -> Unit,
    onReset: () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    FormFilter(
        error = null,
        onReset = onReset,
        onConfirm = onConfirm,
        onDismiss = onDismiss
    ) {
        Text("上游类型", fontSize = 13.sp)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            FilterChip(
                text = "全部来源",
                selected = filter.sourceType == null,
                onClick = { onChange(filter.copy(sourceType = null)) }
            )
            SourceType.entries.forEach { sourceType ->
                FilterChip(
                    text = sourceType.label,
                    selected = filter.sourceType == sourceType,
                    onClick = { onChange(filter.copy(sourceType = sourceType)) }
                )
            }
        }
        Text("单据状态", fontSize = 13.sp)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            FilterChip(
                text = "全部状态",
                selected = filter.status == null,
                onClick = { onChange(filter.copy(status = null)) }
            )
            PickingStatus.entries.forEach { status ->
                FilterChip(
                    text = status.label,
                    selected = filter.status == status,
                    onClick = { onChange(filter.copy(status = status)) }
                )
            }
        }
    }
}
