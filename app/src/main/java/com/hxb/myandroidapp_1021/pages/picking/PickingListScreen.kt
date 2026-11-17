package com.hxb.myandroidapp_1021.pages.picking

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hxb.myandroidapp_1021.core.ui.components.InputFieldOne
import com.hxb.myandroidapp_1021.core.ui.components.OrderList
import com.hxb.myandroidapp_1021.core.ui.components.ScrollToTopButton
import com.hxb.myandroidapp_1021.core.ui.components.TopAppBar
import com.hxb.myandroidapp_1021.pages.commonmodel.DisplayField
import com.hxb.myandroidapp_1021.pages.picking.components.CloseDialog
import com.hxb.myandroidapp_1021.pages.picking.components.ClosePickingOrder
import com.hxb.myandroidapp_1021.pages.picking.components.PickingFormFilter
import com.hxb.myandroidapp_1021.core.ui.theme.AppColors
import kotlinx.coroutines.launch

@Composable
fun PickingListScreen(
    scanViewModel: PickingScanViewModel,
    viewModel: PickingListViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onStartPickClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PickingListContent(
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onShowFilters = viewModel::showFilters,
        onSelectOrder = viewModel::selectOrder,
        onRequestCloseOrder = viewModel::requestCloseOrder,
        onBackClick = onBackClick,
        onStartPickClick = { order ->
            scanViewModel.selectOrder(order)
            onStartPickClick()
        }
    )
    if (uiState.showFilters) {
        PickingFormFilter(
            filter = uiState.draftFilter,
            workshopOptions = uiState.orders.map { it.workshop }
                .filter { it.isNotBlank() }.distinct().sorted(),
            error = uiState.filterError,
            onChange = viewModel::onFilterChange,
            onReset = viewModel::resetFilters,
            onConfirm = viewModel::applyFilters,
            onDismiss = viewModel::dismissFilters
        )
    }
    val closingOrder = uiState.orders.firstOrNull { it.orderNo == uiState.closingOrderNo }
    if (closingOrder != null) {
        CloseDialog(
            remaining = closingOrder.pendingCount,
            onConfirm = viewModel::confirmCloseOrder,
            onDismiss = viewModel::dismissCloseOrder
        )
    }
}

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun PickingListContent(
    uiState: PickingListUiState,
    onSearchQueryChange: (String) -> Unit,
    onShowFilters: () -> Unit,
    onSelectOrder: (String) -> Unit,
    onRequestCloseOrder: () -> Unit,
    onBackClick: () -> Unit = {},
    onStartPickClick: (PickingOrder) -> Unit = {}
) {
    val pageBg = MaterialTheme.colorScheme.background
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val showScrollToTopButton by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 2 } // 向下滑过 2 个条目再显示回到顶部按钮
    }
    val focusManager = LocalFocusManager.current
    val clearFocus = { focusManager.clearFocus() }
    val hasFilter = uiState.filter != PickingListFilter()
    val selectedOrder = uiState.orders.firstOrNull { it.orderNo == uiState.selectedOrderNo }
    val canCloseSelected = selectedOrder != null &&
            selectedOrder.status != PickingStatus.CLOSED && selectedOrder.pendingCount > 0

    Scaffold(
        modifier = Modifier.imePadding(),
        containerColor = pageBg,
        topBar = {
            TopAppBar(
                title = "工单拣选",
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
                    placeholder = "请输入拣选单号"
                )

                // === 2. 列表 ===
                OrderList(
                    orders = uiState.filteredOrders,
                    listState = listState,
                    emptyText = if (uiState.searchQuery.isEmpty() && !hasFilter) "暂无数据" else "暂无匹配数据",
                    modifier = Modifier.fillMaxSize(),
                    key = { it.orderNo },
                    header = {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 4.dp, end = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { clearFocus(); onShowFilters() },
                                shape = RoundedCornerShape(4.dp)
                            ) { Text("筛选") }
                            Text(
                                "已显示 ${uiState.filteredOrders.size} 条",
                                Modifier.weight(1f),
                                textAlign = TextAlign.End,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                ) { order ->
                    val selected = uiState.selectedOrderNo == order.orderNo
                    val lift by animateDpAsState(
                        targetValue = if (selected) 2.dp else 0.dp,
                        label = "pickingCardLift"
                    )
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .background(pageBg)
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = -lift)
                                .toggleable(
                                    value = selected,
                                    role = Role.Checkbox,
                                    onValueChange = { onSelectOrder(order.orderNo) }
                                ),
                            shape = RectangleShape,
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(
                                width = if (selected) 2.dp else 1.dp,
                                color = if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outlineVariant
                            ),
                            shadowElevation = lift * 2
                        ) {
                            Column(
                                Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    Modifier.fillMaxWidth(),
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
                                            onClick = { clearFocus(); onStartPickClick(order) },
                                            enabled = order.status != PickingStatus.CLOSED,
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
                                                text = "开始拣选",
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
                                listOf(
                                    DisplayField("单据状态", order.status.label),
                                    DisplayField("车间/工班", order.workshop),
                                    DisplayField("借用人", order.borrower),
                                    DisplayField("计划日期", order.plannedDate.ifBlank { "-" })
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
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "拣选进度 ${order.pickedCount}/${order.totalCount}",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    LinearProgressIndicator(
                                        progress = {
                                            if (order.totalCount > 0) {
                                                (order.pickedCount.toFloat() / order.totalCount).coerceIn(
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
                        HorizontalDivider(thickness = 8.dp, color = pageBg)
                    }
                }
            }

            // === 3. 悬浮按钮：关闭选中的拣选单 ===
            if (canCloseSelected) {
                ClosePickingOrder(
                    onClick = { clearFocus(); onRequestCloseOrder() },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, bottom = 32.dp)
                )
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
