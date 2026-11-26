package com.hxb.myandroidapp_1021.feature.putaway.presentation

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hxb.myandroidapp_1021.core.ui.components.InputFieldOne
import com.hxb.myandroidapp_1021.core.ui.components.OrderFilterRow
import com.hxb.myandroidapp_1021.core.ui.components.OrderList
import com.hxb.myandroidapp_1021.core.ui.components.OrderProgress
import com.hxb.myandroidapp_1021.core.ui.components.PullToRefresh
import com.hxb.myandroidapp_1021.core.ui.components.ScrollToTopButton
import com.hxb.myandroidapp_1021.core.ui.components.TopAppBar
import com.hxb.myandroidapp_1021.feature.putaway.domain.model.PutawayOrder
import com.hxb.myandroidapp_1021.core.ui.common.DisplayField
import kotlinx.coroutines.launch

@Composable
fun PutawayListScreen(
    flowViewModel: PutawayListViewModel,
    onBackClick: () -> Unit = {},
    onStartPutawayClick: () -> Unit = {}
) {
    val uiState by flowViewModel.uiState.collectAsStateWithLifecycle()

    PutawayListContent(
        uiState = uiState,
        onSearchQueryChange = flowViewModel::onSearchQueryChange,
        onFilterChange = flowViewModel::onFilterChange,
        onRefresh = flowViewModel::refresh,
        onBackClick = onBackClick,
        onStartPutawayClick = { order ->
            if (flowViewModel.selectOrder(order)) onStartPutawayClick()
        }
    )
}

@Composable
fun PutawayListContent(
    uiState: PutawayListUiState,
    onSearchQueryChange: (String) -> Unit,
    onRefresh: () -> Unit,
    onBackClick: () -> Unit = {},
    onStartPutawayClick: (PutawayOrder) -> Unit = {},
    onFilterChange: (PutawayFilter) -> Unit = {}
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
                title = "工器具上架",
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
                    placeholder = "请输入上架单号或入库单号"
                )

                if (uiState.errorMessage != null) {
                    Text(
                        uiState.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                // === 2. 状态筛选和数量，列表未加载时显示共0张 ===
                OrderFilterRow(
                    filterOptions = PutawayFilter.entries.map { it.label },
                    selectedFilterIndex = uiState.selectedFilter.ordinal,
                    onFilterChange = { onFilterChange(PutawayFilter.entries[it]) },
                    countText = "共${uiState.filteredOrders.size}张"
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
                        title = { it.orderNo },
                        statusText = { it.status.label },
                        fields = { order ->
                            listOf(
                                DisplayField("入库单号", order.inboundOrderNo),
                                DisplayField(
                                    "仓库",
                                    order.details.map { it.warehouse }.distinct().joinToString("、")
                                ),
                                DisplayField("上架人", order.operator.ifBlank { "-" }),
                                DisplayField("上架时间", order.putawayTime.ifBlank { "-" })
                            )
                        },
                        header = refreshHeader,
                        progress = { OrderProgress("上架进度", it.completedCount, it.details.size) },
                        actionText = "开始上架",
                        onActionClick = onStartPutawayClick,
                        modifier = Modifier.fillMaxSize()
                    )
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
