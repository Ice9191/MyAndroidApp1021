package com.hxb.myandroidapp_1021.feature.tempborrow.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hxb.myandroidapp_1021.core.ui.components.OrderList
import com.hxb.myandroidapp_1021.core.ui.components.OrderProgress
import com.hxb.myandroidapp_1021.core.ui.components.TempListCard
import com.hxb.myandroidapp_1021.core.ui.components.ToastNotification
import com.hxb.myandroidapp_1021.core.ui.components.TopAppBar
import com.hxb.myandroidapp_1021.feature.tempborrow.domain.model.TempBorrowDetailStatus
import com.hxb.myandroidapp_1021.commonmodel.DisplayField

/**
 * 临时借出的单据确认页：展示单据与全部明细，逐条确认借出后更新明细和单据状态。
 */
@Composable
fun TempBorrowConfirmScreen(
    flowViewModel: TempBorrowListViewModel,
    viewModel: TempBorrowConfirmViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit
) {
    val listState by flowViewModel.uiState.collectAsStateWithLifecycle()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(listState.selectedOrder, listState.pickedRfids) {
        viewModel.bindOrder(listState.selectedOrder, listState.pickedRfids)
    }
    TempBorrowConfirmContent(
        state = state,
        onBackClick = onBackClick,
        onHomeClick = onHomeClick,
        onConfirmLine = { lineNo ->
            viewModel.confirmLine(lineNo, flowViewModel::updateOrder)
        },
        onNotificationDismiss = viewModel::dismissNotification
    )
}

@Composable
fun TempBorrowConfirmContent(
    state: TempBorrowConfirmUiState,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onConfirmLine: (Int) -> Unit,
    onNotificationDismiss: () -> Unit
) {
    val order = state.order
    val listState = rememberLazyListState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = "确认借出",
                onBackClick = onBackClick,
                onHomeClick = onHomeClick
            )
        }
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .clipToBounds()
        ) {
            Column(Modifier.fillMaxSize()) {
                if (order == null) {
                    Text(
                        "暂无临时借出单",
                        Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    OrderList(
                        orders = order.details,
                        listState = listState,
                        emptyText = "暂无器具明细",
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        key = { it.lineNo },
                        header = {
                            TempListCard(
                                title = order.orderNo,
                                status = order.status.label,
                                fields = listOf(
                                    DisplayField("工单号", order.workOrderNo),
                                    DisplayField("借用人", order.borrower),
                                    DisplayField("车间", order.workshop),
                                    DisplayField("工班", order.workGroup),
                                    DisplayField("借出时间", order.borrowTime.ifBlank { "-" }),
                                    DisplayField("备注", order.remark.ifBlank { "-" }),
                                    DisplayField("创建人", order.createdBy),
                                    DisplayField("创建时间", order.createdAt)
                                ),
                                progress = OrderProgress(
                                    "借出进度",
                                    order.borrowedCount,
                                    order.totalCount
                                )
                            )
                        }
                    ) { line ->
                        TempListCard(
                            title = line.description,
                            status = when {
                                line.status == TempBorrowDetailStatus.BORROWED -> line.status.label
                                line.rfid in state.pickedRfids -> "已拣选 · 待确认借出"
                                else -> line.status.label
                            },
                            fields = listOf(
                                DisplayField("行号", line.lineNo.toString()),
                                DisplayField("物资编码", line.itemCode),
                                DisplayField("RFID 编码", line.rfid),
                                DisplayField("单位", line.unit)
                            ),
                            actionText = if (line.status == TempBorrowDetailStatus.BORROWED) {
                                null
                            } else {
                                "确认借出"
                            },
                            enabled = !state.isSubmitting,
                            onAction = { onConfirmLine(line.lineNo) }
                        )
                    }
                }
            }
            ToastNotification(
                message = state.notificationMessage,
                notificationId = state.notificationId,
                onDismiss = onNotificationDismiss
            )
        }
    }
}
