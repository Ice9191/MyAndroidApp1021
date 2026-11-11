package com.hxb.myandroidapp_1021.ui.putaway

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hxb.myandroidapp_1021.components.InputFieldOne
import com.hxb.myandroidapp_1021.components.OrderList
import com.hxb.myandroidapp_1021.components.OrderProgress
import com.hxb.myandroidapp_1021.components.ScrollToTopButton
import com.hxb.myandroidapp_1021.ui.common.DisplayField
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
        onBackClick = onBackClick,
        onStartPutawayClick = { order ->
            if (flowViewModel.selectOrder(order)) onStartPutawayClick()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PutawayListContent(
    uiState: PutawayListUiState,
    onSearchQueryChange: (String) -> Unit,
    onBackClick: () -> Unit = {},
    onStartPutawayClick: (PutawayOrder) -> Unit = {},
    onFilterChange: (PutawayFilter) -> Unit = {}
) {
    val themeBlue = Color(0xFF1E71C8)
    val pageBg = Color(0xFFF5F6F8)
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
                modifier = Modifier
                    .height(75.dp)
                    .pointerInput(Unit) { detectTapGestures(onTap = { clearFocus() }) },
                title = {
                    Text(
                        text = "工器具上架",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.offset(x = (-16).dp)
                    )
                },
                navigationIcon = {
                    IconButton(
                        shape = RoundedCornerShape(4.dp),
                        onClick = {
                            clearFocus()
                            onBackClick()
                        },
                        modifier = Modifier.offset(x = (-7).dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = themeBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
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
                        color = Color(0xFFB3261E),
                        modifier = Modifier.padding(16.dp)
                    )
                }
                // === 2. 列表 ===
                if (uiState.isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = themeBlue)
                    }
                } else OrderList(
                    orders = uiState.filteredOrders,
                    listState = listState,
                    emptyText = if (uiState.searchQuery.isEmpty()) "暂无数据" else "暂无匹配数据",
                    title = { it.orderNo },
                    statusText = { it.status },
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
                    progress = { OrderProgress("上架进度", it.completedCount, it.details.size) },
                    actionText = "开始上架",
                    onActionClick = onStartPutawayClick,
                    modifier = Modifier.fillMaxSize(),
                    filterOptions = PutawayFilter.entries.map { it.label },
                    selectedFilterIndex = uiState.selectedFilter.ordinal,
                    onFilterChange = { onFilterChange(PutawayFilter.entries[it]) }
                )
            }

            // === 3. 右下角悬浮按钮：回到顶部 ===
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
