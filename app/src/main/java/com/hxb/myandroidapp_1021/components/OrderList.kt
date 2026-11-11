package com.hxb.myandroidapp_1021.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxb.myandroidapp_1021.ui.common.DisplayField

data class OrderProgress(val label: String, val completed: Int, val total: Int)

/**
 * 接收任意订单类型，由调用页面提供展示字段和操作，不依赖具体业务或 ViewModel。
 */
@Composable
fun <T> OrderList(
    orders: List<T>,
    listState: LazyListState,
    emptyText: String,
    title: (T) -> String,
    statusText: (T) -> String,
    fields: (T) -> List<DisplayField>,
    actionText: String,
    onActionClick: (T) -> Unit,
    modifier: Modifier = Modifier,
    filterOptions: List<String> = emptyList(),
    selectedFilterIndex: Int = 0,
    onFilterChange: (Int) -> Unit = {},
    header: (@Composable () -> Unit)? = null,
    progress: ((T) -> OrderProgress)? = null
) {
    val themeBlue = Color(0xFF1E71C8)
    val textDark = Color(0xFF222222)
    val textGray = Color(0xFF888888)
    val pageBg = Color(0xFFF5F6F8)

    OrderList(
        orders = orders,
        listState = listState,
        emptyText = emptyText,
        modifier = modifier,
        header = header,
        filterOptions = filterOptions,
        selectedFilterIndex = selectedFilterIndex,
        onFilterChange = onFilterChange
    ) { order ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 1.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title(order),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = textDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = statusText(order),
                        fontSize = 13.sp,
                        color = themeBlue,
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    Button(
                        onClick = { onActionClick(order) },
                        modifier = Modifier
                            .height(30.dp)
                            .wrapContentWidth(),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = themeBlue)
                    ) {
                        Text(
                            text = actionText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F0))

            Column(
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 1.dp
                ),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                fields(order).forEach { field ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = field.label,
                            fontSize = 13.sp,
                            color = textGray,
                            maxLines = 1,
                            modifier = Modifier.alignByBaseline()
                        )
                        Text(
                            text = field.value,
                            fontSize = 13.sp,
                            color = textDark,
                            maxLines = 1,
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .alignByBaseline()
                        )
                    }
                }
                progress?.invoke(order)?.let { itemProgress ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 0.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${itemProgress.label} ${itemProgress.completed}/${itemProgress.total}",
                            fontSize = 13.sp,
                            color = textGray
                        )
                        LinearProgressIndicator(
                            progress = {
                                if (itemProgress.total > 0) {
                                    (itemProgress.completed.toFloat() / itemProgress.total).coerceIn(
                                        0f,
                                        1f
                                    )
                                } else {
                                    0f
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp),
                            color = Color(0xFF4B95F5),
                            trackColor = Color(0xFFF0F2F5),
                            gapSize = 0.dp,
                            drawStopIndicator = {}
                        )
                    }
                }
            }

            HorizontalDivider(thickness = 8.dp, color = pageBg)
        }
    }
}

/**
 * 自定义条目内容的列表入口，滚动状态和条目事件由调用页面管理。
 */
@Composable
fun <T> OrderList(
    orders: List<T>,
    listState: LazyListState,
    emptyText: String,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    key: ((T) -> Any)? = null,
    filterOptions: List<String> = emptyList(),
    selectedFilterIndex: Int = 0,
    onFilterChange: (Int) -> Unit = {},
    header: (@Composable () -> Unit)? = null,
    itemContent: @Composable LazyItemScope.(T) -> Unit
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement
    ) {
        if (filterOptions.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F6F8))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        Row(
                            modifier = Modifier
                                .clickable { isDropdownExpanded = true }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = filterOptions[selectedFilterIndex],
                                fontSize = 14.sp,
                                color = Color(0xFF222222),
                                fontWeight = FontWeight.Medium
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "展开筛选",
                                tint = Color(0xFF222222)
                            )
                        }
                        DropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false },
                            containerColor = Color.White
                        ) {
                            filterOptions.forEachIndexed { index, label ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = label,
                                            fontSize = 14.sp,
                                            color = if (index == selectedFilterIndex) Color(
                                                0xFF1E71C8
                                            ) else Color(0xFF222222)
                                        )
                                    },
                                    onClick = {
                                        onFilterChange(index)
                                        isDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    Text(
                        text = "共${orders.size}张",
                        fontSize = 14.sp,
                        color = Color(0xFF888888)
                    )
                }
            }
        }
        if (header != null) {
            item { header() }
        }
        if (orders.isEmpty()) {
            item {
                Text(
                    text = emptyText,
                    fontSize = 14.sp,
                    color = Color(0xFF888888),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                )
            }
        } else {
            items(orders, key = key, itemContent = itemContent)
        }
    }
}
