package com.hxb.myandroidapp_1021.feature.otherpicking.presentation

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hxb.myandroidapp_1021.core.ui.components.InputFieldOne
import com.hxb.myandroidapp_1021.core.ui.components.OrderList
import com.hxb.myandroidapp_1021.core.ui.components.PullToRefresh
import com.hxb.myandroidapp_1021.core.ui.components.ToastNotification
import com.hxb.myandroidapp_1021.core.ui.components.TopAppBar
import com.hxb.myandroidapp_1021.core.ui.theme.AppColors
import com.hxb.myandroidapp_1021.feature.otherpicking.domain.model.OtherPickingOrder
import com.hxb.myandroidapp_1021.feature.otherpicking.domain.model.PickingStatus
import com.hxb.myandroidapp_1021.commonmodel.DisplayField

/**
 * 其他拣选列表页；进入页面后由下拉刷新容器自动加载，点击“开始拣选”把选中的单据交给扫描页。
 */
@Composable
fun OtherPickingListScreen(
    viewModel: OtherPickingListViewModel,
    scanViewModel: OtherPickingScanViewModel,
    onBackClick: () -> Unit = {},
    onStartClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    OtherPickingListContent(
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onRefresh = viewModel::refresh,
        onShowFilters = viewModel::showFilters,
        onDismissToast = viewModel::dismissToast,
        onBackClick = onBackClick,
        onStartClick = { order ->
            scanViewModel.selectOrder(order)
            onStartClick()
        }
    )

    if (uiState.showFilters) {
        OtherPickingFormFilter(
            filter = uiState.draftFilter,
            onChange = viewModel::onFilterChange,
            onReset = viewModel::resetFilters,
            onConfirm = viewModel::applyFilters,
            onDismiss = viewModel::dismissFilters
        )
    }
}

/**
 * 列表内容：搜索框与筛选行固定在顶部，列表区域支持下拉刷新。
 */
@Composable
fun OtherPickingListContent(
    uiState: OtherPickingListUiState,
    onSearchQueryChange: (String) -> Unit,
    onRefresh: () -> Unit,
    onShowFilters: () -> Unit,
    onDismissToast: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onStartClick: (OtherPickingOrder) -> Unit = {}
) {
    val pageBg = MaterialTheme.colorScheme.background
    val listState = rememberLazyListState()
    val focusManager = LocalFocusManager.current
    val clearFocus = { focusManager.clearFocus() }

    Scaffold(
        modifier = Modifier.imePadding(),
        containerColor = pageBg,
        topBar = {
            TopAppBar(
                title = "其他拣选",
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures(onTap = { clearFocus() })
                },
                onBackClick = {
                    clearFocus()
                    onBackClick()
                }
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
                InputFieldOne(
                    value = uiState.searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = "请输入拣选单号或上游单号"
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            clearFocus()
                            onShowFilters()
                        },
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("筛选")
                    }
                    Text(
                        text = "已显示 ${if (uiState.hasLoaded) uiState.filteredOrders.size else 0} 条",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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
                        key = { it.pickingOrderNo },
                        header = refreshHeader
                    ) { order ->
                        OtherPickingCard(
                            order = order,
                            onStartClick = {
                                clearFocus()
                                onStartClick(order)
                            }
                        )
                    }
                }
            }

            // 顶部提示条：提交后显示拣选结果。
            ToastNotification(
                message = uiState.toastMessage,
                notificationId = uiState.toastId,
                onDismiss = onDismissToast,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

/**
 * 其他拣选单卡片：首行为拣选单号与操作按钮，其后为来源与计划信息，最后为拣选进度。
 */
@Composable
private fun OtherPickingCard(
    order: OtherPickingOrder,
    onStartClick: () -> Unit
) {
    val pageBg = MaterialTheme.colorScheme.background
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(pageBg)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RectangleShape,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 1.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = order.pickingOrderNo,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = order.status.label,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1
                        )
                        Button(
                            onClick = onStartClick,
                            enabled = order.status != PickingStatus.CLOSED && order.pendingCount > 0,
                            modifier = Modifier
                                .height(30.dp)
                                .wrapContentWidth(),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Text("开始拣选", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 1.dp),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    listOf(
                        DisplayField("上游类型", order.upstreamType.label),
                        DisplayField("上游单号", order.upstreamNo),
                        DisplayField(
                            "车间工班",
                            listOf(order.workshop, order.workGroup)
                                .filter { it.isNotBlank() }
                                .joinToString(" / ")
                        ),
                        DisplayField("计划日期", order.plannedDate.ifBlank { "-" })
                    ).forEach { field ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 0.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "拣选进度 ${order.completedCount}/${order.totalCount}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        LinearProgressIndicator(
                            progress = {
                                if (order.totalCount > 0) {
                                    order.completedCount.toFloat() / order.totalCount
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
