package com.hxb.myandroidapp_1021.ui.pages.picking.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxb.myandroidapp_1021.ui.components.FilterChip
import com.hxb.myandroidapp_1021.ui.components.FormFilter
import com.hxb.myandroidapp_1021.ui.components.InputFieldOne
import com.hxb.myandroidapp_1021.pages.picking.PickingListFilter
import com.hxb.myandroidapp_1021.pages.picking.PickingStatus

/**
 * 工单拣选筛选面板内容：单据状态、车间/工班和计划日期；骨架与按钮由 FormFilter 提供。
 */
@Composable
fun PickingFormFilter(
    filter: PickingListFilter,
    workshopOptions: List<String>,
    error: String?,
    onChange: (PickingListFilter) -> Unit,
    onReset: () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    FormFilter(
        error = error,
        onReset = onReset,
        onConfirm = onConfirm,
        onDismiss = onDismiss
    ) {
        Text("单据状态", fontSize = 13.sp)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            FilterChip(
                text = "全部",
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
        Text("车间/工班", fontSize = 13.sp)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            FilterChip(
                text = "全部",
                selected = filter.workshop == null,
                onClick = { onChange(filter.copy(workshop = null)) }
            )
            workshopOptions.forEach { workshop ->
                FilterChip(
                    text = workshop,
                    selected = filter.workshop == workshop,
                    onClick = { onChange(filter.copy(workshop = workshop)) }
                )
            }
        }
        Text("计划日期", fontSize = 13.sp)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            InputFieldOne(
                value = filter.startDate,
                onValueChange = { onChange(filter.copy(startDate = it)) },
                placeholder = "开始日期",
                modifier = Modifier.weight(1f),
                showSearchIcon = false,
                horizontalPadding = 0.dp
            )
            Text(
                "至",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
            InputFieldOne(
                value = filter.endDate,
                onValueChange = { onChange(filter.copy(endDate = it)) },
                placeholder = "结束日期",
                modifier = Modifier.weight(1f),
                showSearchIcon = false,
                horizontalPadding = 0.dp
            )
        }
    }
}
