package com.hxb.myandroidapp_1021.feature.picking.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxb.myandroidapp_1021.core.ui.components.DropdownSelection
import com.hxb.myandroidapp_1021.core.ui.components.FormFilter
import com.hxb.myandroidapp_1021.core.ui.components.InputFieldOne
import com.hxb.myandroidapp_1021.feature.picking.domain.model.PickingStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private const val FILTER_DATE_PATTERN = "yyyy-MM-dd"

/**
 * 工单拣选筛选面板内容：单据状态、工班和计划日期；骨架与按钮由 FormFilter 提供。
 * 单据状态用下拉框选择；工班只读，点击由调用页面打开查询弹窗（候选较多，需要按关键字筛选）。
 * 计划日期用日历弹窗选择，只选一侧时按单边条件筛选，两侧都选时包含起止两天。
 */
@Composable
fun PickingFormFilter(
    filter: PickingListFilter,
    error: String?,
    onChange: (PickingListFilter) -> Unit,
    onWorkGroupClick: () -> Unit,
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
        DropdownSelection(
            options = PickingStatus.entries,
            selected = filter.status,
            onSelect = { onChange(filter.copy(status = it)) },
            label = { it.label },
            contentDescription = "Show picking statuses"
        )
        Text("工班", fontSize = 13.sp)
        InputFieldOne(
            value = filter.workGroup.orEmpty(),
            onValueChange = {},
            placeholder = "请选择工班",
            modifier = Modifier.fillMaxWidth(),
            showSearchIcon = false,
            horizontalPadding = 0.dp,
            onClick = onWorkGroupClick
        )
        Text("计划日期", fontSize = 13.sp)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DateFilterButton(
                value = filter.startDate,
                placeholder = "开始日期",
                onSelect = { onChange(filter.copy(startDate = it)) },
                modifier = Modifier.weight(1f)
            )
            Text(
                "至",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
            DateFilterButton(
                value = filter.endDate,
                placeholder = "结束日期",
                onSelect = { onChange(filter.copy(endDate = it)) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * 日期筛选按钮：外观与筛选面板输入框一致，点击弹出 Material 3 日历。
 * 日历确认只把选中的日期按 yyyy-MM-dd 回填给调用页面，是否应用筛选仍由面板的「确定」决定。
 */
@Composable
private fun DateFilterButton(
    value: String,
    placeholder: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPicker by remember { mutableStateOf(false) }
    Row(
        modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.background)
            .clickable { showPicker = true }
            .heightIn(min = 48.dp)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = value.ifBlank { placeholder },
            modifier = Modifier.weight(1f),
            fontSize = 14.sp,
            maxLines = 1,
            color = if (value.isBlank()) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = "Pick date",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
    if (showPicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = parseFilterDate(value))
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    HorizontalDivider(color = DatePickerDefaults.colors().dividerColor)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = placeholder,
                            modifier = Modifier.weight(1f),
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        TextButton(
                            shape = RoundedCornerShape(4.dp),
                            onClick = { showPicker = false }
                        ) {
                            Text("取消")
                        }
                        TextButton(
                            shape = RoundedCornerShape(4.dp),
                            enabled = state.selectedDateMillis != null,
                            onClick = {
                                state.selectedDateMillis?.let { onSelect(formatFilterDate(it)) }
                                showPicker = false
                            }
                        ) {
                            Text("确定")
                        }
                    }
                }
            }
        ) {
            DatePicker(
                state = state,
                colors = DatePickerDefaults.colors(dividerColor = Color.Transparent),
                title = null,
                headline = null,
                showModeToggle = false
            )
        }
    }
}

/**
 * 日历回传的是 UTC 零点毫秒，格式化固定用 UTC，避免本地时区把日期减一天。
 */
private fun formatFilterDate(millis: Long): String =
    SimpleDateFormat(FILTER_DATE_PATTERN, Locale.ROOT)
        .apply { timeZone = TimeZone.getTimeZone("UTC") }
        .format(Date(millis))

/**
 * 把已保存的 yyyy-MM-dd 还原成日历需要的 UTC 零点毫秒，空值表示未选择。
 */
private fun parseFilterDate(date: String): Long? {
    if (date.isBlank()) return null
    return runCatching {
        SimpleDateFormat(FILTER_DATE_PATTERN, Locale.ROOT)
            .apply {
                isLenient = false
                timeZone = TimeZone.getTimeZone("UTC")
            }
            .parse(date)
            ?.time
    }.getOrNull()
}
