package com.hxb.myandroidapp_1021.feature.stockdetail.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            placeholder = "请输入货位编码，如 A-03-02-01",
            modifier = Modifier.fillMaxWidth(),
            showSearchIcon = false,
            horizontalPadding = 0.dp
        )
        Text("物资分类", fontSize = 13.sp)
        CategoryFilterField(
            options = categoryOptions,
            selected = filter.category,
            onSelect = { onChange(filter.copy(category = it)) }
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

/**
 * 物资分类单选下拉框：外观与筛选面板的输入框一致；“全部”以 null 上报，其余为分类名称。
 */
@Composable
private fun CategoryFilterField(
    options: List<String>,
    selected: String?,
    onSelect: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.background)
                .clickable { expanded = true }
                .heightIn(min = 48.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                selected ?: "全部",
                Modifier.weight(1f),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = "Show categories",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            (listOf<String?>(null) + options).forEach { category ->
                DropdownMenuItem(
                    text = {
                        Text(
                            category ?: "全部",
                            fontSize = 14.sp,
                            color = if (category == selected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    },
                    onClick = {
                        onSelect(category)
                        expanded = false
                    }
                )
            }
        }
    }
}
