package com.hxb.myandroidapp_1021.feature.returns.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hxb.myandroidapp_1021.core.ui.components.InputFieldOne
import com.hxb.myandroidapp_1021.core.ui.components.OrderFilterRow
import com.hxb.myandroidapp_1021.core.ui.components.OrderList
import com.hxb.myandroidapp_1021.core.ui.components.PullToRefresh
import com.hxb.myandroidapp_1021.core.ui.components.ScrollToTopButton
import com.hxb.myandroidapp_1021.core.ui.components.TopAppBar
import com.hxb.myandroidapp_1021.core.ui.theme.AppColors
import com.hxb.myandroidapp_1021.feature.returns.domain.model.ReturnOrder
import com.hxb.myandroidapp_1021.feature.returns.domain.model.ReturnOrderStatus
import com.hxb.myandroidapp_1021.commonmodel.DisplayField
import kotlinx.coroutines.launch

@Composable
fun ReturnListScreen(
    flowViewModel: ReturnListViewModel,
    scanViewModel: ReturnScanViewModel,
    onBackClick: () -> Unit = {},
    onStartReturnClick: () -> Unit = {}
) {
    val uiState by flowViewModel.uiState.collectAsStateWithLifecycle()

    ReturnListContent(
        uiState = uiState,
        onSearchQueryChange = flowViewModel::onSearchQueryChange,
        onRefresh = flowViewModel::refresh,
        onFilterChange = flowViewModel::onFilterChange,
        onBackClick = onBackClick,
        onStartReturnClick = { order ->
            scanViewModel.selectOrder(order)
            onStartReturnClick()
        }
    )
}

/**
 * 归还上架列表：与拣选列表共用搜索、筛选、下拉刷新和卡片结构，加载圆圈由刷新头部替代。
 */
@Composable
fun ReturnListContent(
    uiState: ReturnListUiState,
    onSearchQueryChange: (String) -> Unit,
    onRefresh: () -> Unit,
    onFilterChange: (ReturnFilter) -> Unit,
    onBackClick: () -> Unit = {},
    onStartReturnClick: (ReturnOrder) -> Unit = {}
) {
    val pageBg = MaterialTheme.colorScheme.background
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val showScrollToTopButton by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 2 } // 向下滑过 2 个条目再显示回到顶部按钮
    }
    val focusManager = LocalFocusManager.current
    val clearFocus = { focusManager.clearFocus() }

    Scaffold(
        modifier = Modifier.imePadding(),
        containerColor = pageBg,
        topBar = {
            TopAppBar(
                title = "工单归还上架",
                modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { clearFocus() }) },
                onBackClick = { clearFocus(); onBackClick() }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pointerInput(Unit) { detectTapGestures(onTap = { clearFocus() }) }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // === 1. 顶部搜索框 ===
                InputFieldOne(
                    value = uiState.searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = "请输入上架单号或归还单号"
                )

                // === 2. 状态筛选和数量 ===
                OrderFilterRow(
                    filterOptions = ReturnFilter.entries.map { it.label },
                    selectedFilterIndex = uiState.selectedFilter.ordinal,
                    onFilterChange = { onFilterChange(ReturnFilter.entries[it]) },
                    countText = "已显示 ${uiState.filteredOrders.size} 条"
                )

                // === 3. 下拉刷新 + 列表：刷新头部作为列表 header，列表数据跟随头部移动 ===
                PullToRefresh(
                    isRefreshing = uiState.isRefreshing,
                    hasLoaded = uiState.hasLoaded,
                    onRefresh = onRefresh,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) { refreshHeader ->
                    OrderList(
                        orders = if (uiState.hasLoaded) uiState.filteredOrders else emptyList(),
                        listState = listState,
                        emptyText = "",
                        modifier = Modifier.fillMaxSize(),
                        key = { it.orderNo },
                        header = refreshHeader
                    ) { order ->
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .background(pageBg)
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RectangleShape,
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                            ) {
                                Column {
                                    Row(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 1.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = order.orderNo,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = order.status.label,
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.primary,
                                                maxLines = 1
                                            )
                                            Button(
                                                onClick = { clearFocus(); onStartReturnClick(order) },
                                                enabled = order.status != ReturnOrderStatus.COMPLETED &&
                                                        order.pendingCount > 0,
                                                modifier = Modifier
                                                    .height(30.dp)
                                                    .wrapContentWidth(),
                                                shape = RoundedCornerShape(4.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = MaterialTheme.colorScheme.primary
                                                )
                                            ) {
                                                Text(
                                                    text = "开始上架",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                    HorizontalDivider(
                                        thickness = 1.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant
                                    )
                                    Column(
                                        Modifier.padding(horizontal = 16.dp, vertical = 1.dp),
                                        verticalArrangement = Arrangement.spacedBy(1.dp)
                                    ) {
                                        listOf(
                                            DisplayField("归还单号", order.returnOrderNo),
                                            DisplayField("工单号", order.workOrderNo),
                                            DisplayField("单据状态", order.status.label),
                                            DisplayField("上架人", order.putawayBy.ifBlank { "-" }),
                                            DisplayField("上架时间", order.putawayAt.ifBlank { "-" }),
                                            DisplayField("货位", order.location.ifBlank { "-" })
                                        ).forEach { field ->
                                            Row(
                                                Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = field.label,
                                                    fontSize = 13.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    maxLines = 1,
                                                    modifier = Modifier.alignByBaseline()
                                                )
                                                Text(
                                                    text = field.value,
                                                    fontSize = 13.sp,
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    maxLines = 1,
                                                    modifier = Modifier
                                                        .padding(start = 16.dp)
                                                        .alignByBaseline()
                                                )
                                            }
                                        }
                                        Row(
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(top = 0.dp, bottom = 4.dp),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "上架进度 ${order.putawayCount}/${order.totalCount}",
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            LinearProgressIndicator(
                                                progress = {
                                                    if (order.totalCount > 0) {
                                                        (order.putawayCount.toFloat() / order.totalCount).coerceIn(
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
                                                color = AppColors.Progress,
                                                trackColor = AppColors.UnreadBackground,
                                                gapSize = 0.dp,
                                                drawStopIndicator = {}
                                            )
                                        }
                                    }
                                }
                            }
                            HorizontalDivider(thickness = 8.dp, color = pageBg)
                        }
                    }
                }
            }

            // === 4. 右下角悬浮按钮：回到顶部 ===
            ScrollToTopButton(
                visible = showScrollToTopButton,
                onClick = { coroutineScope.launch { listState.animateScrollToItem(0) } },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 32.dp)
            )
        }
    }
}
