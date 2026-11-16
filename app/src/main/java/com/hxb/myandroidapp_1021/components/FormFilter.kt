package com.hxb.myandroidapp_1021.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * 筛选面板字段：页面描述字段与当前值，组件只渲染并上报选择，不持有业务状态。
 * 单选标签组约定 options[0] 为“全部”。
 */
sealed interface FilterField {
    data class Chips(
        val label: String,
        val options: List<String>,
        val selectedIndex: Int,
        val onSelect: (Int) -> Unit
    ) : FilterField

    data class Text(
        val label: String,
        val value: String,
        val placeholder: String,
        val onChange: (String) -> Unit
    ) : FilterField

    data class DateRange(
        val label: String,
        val start: String,
        val end: String,
        val startPlaceholder: String,
        val endPlaceholder: String,
        val onStartChange: (String) -> Unit,
        val onEndChange: (String) -> Unit
    ) : FilterField
}

/**
 * 校验日期区间输入：格式为 yyyy-MM-dd 且开始不晚于结束；无错误返回 null，各页面确认筛选时共用。
 */
fun filterDateError(start: String, end: String): String? {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).apply { isLenient = false }
    val allValid = listOf(start, end).all { value ->
        value.isBlank() || (value.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) &&
                runCatching { format.parse(value) }.isSuccess)
    }
    return when {
        !allValid -> "日期请按 yyyy-MM-dd 输入有效日期"
        start.isNotBlank() && end.isNotBlank() && start > end -> "开始日期不能晚于结束日期"
        else -> null
    }
}

/**
 * 筛选抽屉：字段由页面描述，选项自动换行、内容可滚动，底部操作固定可见。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormFilter(
    fields: List<FilterField>,
    error: String?,
    onReset: () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    title: String = "筛选"
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        dragHandle = null
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
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
                        contentDescription = "Close filter",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                fields.forEach { field ->
                    when (field) {
                        is FilterField.Chips -> {
                            Text(field.label, fontSize = 13.sp)
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                field.options.forEachIndexed { index, option ->
                                    FormFilterButton(
                                        text = option,
                                        onClick = { field.onSelect(index) },
                                        selected = index == field.selectedIndex
                                    )
                                }
                            }
                        }

                        is FilterField.Text -> {
                            Text(field.label, fontSize = 13.sp)
                            InputFieldOne(
                                value = field.value,
                                onValueChange = field.onChange,
                                placeholder = field.placeholder,
                                modifier = Modifier.fillMaxWidth(),
                                showSearchIcon = false,
                                horizontalPadding = 0.dp
                            )
                        }

                        is FilterField.DateRange -> {
                            Text(field.label, fontSize = 13.sp)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                InputFieldOne(
                                    value = field.start,
                                    onValueChange = field.onStartChange,
                                    placeholder = field.startPlaceholder,
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
                                    value = field.end,
                                    onValueChange = field.onEndChange,
                                    placeholder = field.endPlaceholder,
                                    modifier = Modifier.weight(1f),
                                    showSearchIcon = false,
                                    horizontalPadding = 0.dp
                                )
                            }
                        }
                    }
                }
                error?.let { Text(it, fontSize = 13.sp, color = MaterialTheme.colorScheme.error) }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FormFilterButton(
                    text = "重置",
                    onClick = onReset,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                )
                FormFilterButton(
                    text = "确定",
                    onClick = onConfirm,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    prominent = true
                )
            }
        }
    }
}

@Composable
private fun FormFilterButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    prominent: Boolean = false
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
        colors = ButtonDefaults.textButtonColors(
            containerColor = when {
                prominent -> MaterialTheme.colorScheme.primary
                selected -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.background
            },
            contentColor = when {
                prominent -> MaterialTheme.colorScheme.onPrimary
                selected -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    ) { Text(text, fontSize = 13.sp) }
}
