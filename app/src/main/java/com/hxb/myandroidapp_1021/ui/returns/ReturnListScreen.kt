package com.hxb.myandroidapp_1021.ui.returns

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hxb.myandroidapp_1021.components.InputFieldOne
import com.hxb.myandroidapp_1021.components.OrderList
import com.hxb.myandroidapp_1021.components.OrderProgress
import com.hxb.myandroidapp_1021.components.ScrollToTopButton
import com.hxb.myandroidapp_1021.ui.common.DisplayField
import kotlinx.coroutines.launch

@Composable
fun ReturnListScreen(
    scanViewModel: ReturnScanViewModel,
    viewModel: ReturnListViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onStartReturnClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ReturnListContent(
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onStatusChange = viewModel::onStatusChange,
        onBackClick = onBackClick,
        onStartReturnClick = { order ->
            scanViewModel.selectOrder(order)
            onStartReturnClick()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReturnListContent(
    uiState: ReturnListUiState,
    onSearchQueryChange: (String) -> Unit,
    onBackClick: () -> Unit = {},
    onStartReturnClick: (ReturnOrder) -> Unit = {},
    onStatusChange: (ReturnStatus?) -> Unit = {}
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
                        text = "归还",
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
                    placeholder = "请输入归还单号"
                )

                // === 2. 列表 ===
                OrderList(
                    orders = uiState.filteredOrders,
                    listState = listState,
                    emptyText = if (uiState.searchQuery.isEmpty()) "暂无数据" else "暂无匹配数据",
                    title = { it.orderNo },
                    statusText = { it.status.label },
                    fields = { order ->
                        listOf(
                            DisplayField("归还单号", order.orderNo),
                            DisplayField("单据状态", order.status.label),
                            DisplayField("车间/工班", order.workshop),
                            DisplayField("归还人", order.returner),
                            DisplayField("归还时间", order.returnedDate.ifBlank { "-" })
                        )
                    },
                    progress = { OrderProgress("归还进度", it.returnedCount, it.totalCount) },
                    actionText = "开始上架",
                    onActionClick = onStartReturnClick,
                    modifier = Modifier.fillMaxSize(),
                    filterOptions = listOf("全部") + ReturnStatus.entries.map { it.label },
                    selectedFilterIndex = uiState.selectedStatus?.let { it.ordinal + 1 } ?: 0,
                    onFilterChange = { onStatusChange(ReturnStatus.entries.getOrNull(it - 1)) }
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
