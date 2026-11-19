package com.hxb.myandroidapp_1021.feature.otherputaway.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxb.myandroidapp_1021.core.ui.components.FilterChip
import com.hxb.myandroidapp_1021.core.ui.components.FormFilter
import com.hxb.myandroidapp_1021.feature.otherputaway.domain.model.PutawayStatus
import com.hxb.myandroidapp_1021.feature.otherputaway.domain.model.SourceType

/**
 * 其他上架筛选面板内容：上游类型与单据状态；骨架、标题和按钮由 FormFilter 提供，高度随内容变化。
 */
@Composable
fun OtherPutawayFormFilter(
    filter: OtherPutawayFilter,
    onChange: (OtherPutawayFilter) -> Unit,
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
                selected = filter.upstreamType == null,
                onClick = { onChange(filter.copy(upstreamType = null)) }
            )
            SourceType.entries.forEach { sourceType ->
                FilterChip(
                    text = sourceType.label,
                    selected = filter.upstreamType == sourceType,
                    onClick = { onChange(filter.copy(upstreamType = sourceType)) }
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
            PutawayStatus.entries.forEach { status ->
                FilterChip(
                    text = status.label,
                    selected = filter.status == status,
                    onClick = { onChange(filter.copy(status = status)) }
                )
            }
        }
    }
}
