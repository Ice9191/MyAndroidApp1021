package com.hxb.myandroidapp_1021.feature.stockdetail.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxb.myandroidapp_1021.core.ui.components.DropdownSelection
import com.hxb.myandroidapp_1021.core.ui.components.FilterChip
import com.hxb.myandroidapp_1021.core.ui.components.FormFilter
import com.hxb.myandroidapp_1021.core.ui.components.InputFieldOne
import com.hxb.myandroidapp_1021.feature.stockdetail.domain.model.InventoryStatus
import com.hxb.myandroidapp_1021.feature.stockdetail.domain.model.StockDetailFilterState
import com.hxb.myandroidapp_1021.feature.stockdetail.domain.model.ToolCondition

/**
 * 库存明细筛选面板内容：库存状态、好坏件、仓库、货位、物资分类、规格型号和下次送检日期；骨架与按钮由 FormFilter 提供。
 * 物资分类选项由调用页面提供，null 表示“全部”。
 * 抽屉高度固定为屏幕的四分之三，避免筛选内容较少时抽屉高度跳动。
 */
@Composable
fun StockDetailFormFilter(
    filter: StockDetailFilterState,
    categoryOptions: List<String>,
    error: String?,
    onChange: (StockDetailFilterState) -> Unit,
    onReset: () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    FormFilter(
        error = error,
        onReset = onReset,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        heightFraction = 0.75f
    ) {
        Text("库存状态", fontSize = 13.sp)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            FilterChip(
                text = "全部",
                selected = filter.status == null,
                onClick = { onChange(filter.copy(status = null)) }
            )
            InventoryStatus.entries.forEach { status ->
                FilterChip(
                    text = status.label,
                    selected = filter.status == status,
                    onClick = { onChange(filter.copy(status = status)) }
                )
            }
        }
        Text("好坏件状态", fontSize = 13.sp)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                text = "全部",
                selected = filter.condition == null,
                onClick = { onChange(filter.copy(condition = null)) }
            )
            ToolCondition.entries.forEach { condition ->
                FilterChip(
                    text = condition.label,
                    selected = filter.condition == condition,
                    onClick = { onChange(filter.copy(condition = condition)) }
                )
            }
        }
        Text("仓库", fontSize = 13.sp)
        InputFieldOne(
            value = filter.warehouse,
            onValueChange = { onChange(filter.copy(warehouse = it)) },
            placeholder = "请输入仓库名称",
            modifier = Modifier.fillMaxWidth(),
            showSearchIcon = false,
            horizontalPadding = 0.dp
        )
        Text("货位", fontSize = 13.sp)
        InputFieldOne(
            value = filter.location,
            onValueChange = { onChange(filter.copy(location = it)) },
            placeholder = "请输入货位码",
            modifier = Modifier.fillMaxWidth(),
            showSearchIcon = false,
            horizontalPadding = 0.dp
        )
        Text("物资分类", fontSize = 13.sp)
        DropdownSelection(
            options = categoryOptions,
            selected = filter.category,
            onSelect = { onChange(filter.copy(category = it)) },
            contentDescription = "Show categories"
        )
        Text("规格型号", fontSize = 13.sp)
        InputFieldOne(
            value = filter.specification,
            onValueChange = { onChange(filter.copy(specification = it)) },
            placeholder = "请输入规格型号",
            modifier = Modifier.fillMaxWidth(),
            showSearchIcon = false,
            horizontalPadding = 0.dp
        )
        Text("下次送检日期", fontSize = 13.sp)
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
