package com.hxb.myandroidapp_1021.feature.tempreturn.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import com.hxb.myandroidapp_1021.core.ui.common.DisplayField
import com.hxb.myandroidapp_1021.core.ui.components.OrderList
import com.hxb.myandroidapp_1021.core.ui.components.OrderProgress
import com.hxb.myandroidapp_1021.core.ui.components.TempListCard
import com.hxb.myandroidapp_1021.core.ui.components.ToastNotification
import com.hxb.myandroidapp_1021.core.ui.components.TopAppBar
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnDetailStatus

/**
 * 临时归还的单据确认页：展示单据与全部明细，登记归还信息后逐条确认归还。
 */
@Composable
fun TempReturnConfirmScreen(
    flowViewModel: TempReturnListViewModel,
    viewModel: TempReturnConfirmViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit
) {
    val listState by flowViewModel.uiState.collectAsStateWithLifecycle()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(listState.selectedOrder) {
        viewModel.bindOrder(listState.selectedOrder)
    }
    TempReturnConfirmContent(
        state = state,
        onBackClick = onBackClick,
        onHomeClick = onHomeClick,
        onOpenInfoDialog = viewModel::openInfoDialog,
        onConfirmLine = { lineNo ->
            viewModel.confirmLine(lineNo, flowViewModel::updateOrder)
        },
        onNotificationDismiss = viewModel::dismissNotification
    )
    if (state.infoDialogVisible) {
        TempReturnInfoDialog(
            returner = state.draftReturner,
            workOrderNo = state.draftWorkOrderNo,
            returnLocation = state.draftReturnLocation,
            remark = state.draftRemark,
            error = state.infoError,
            onReturnerChange = viewModel::onDraftReturnerChange,
            onWorkOrderNoChange = viewModel::onDraftWorkOrderNoChange,
            onReturnLocationChange = viewModel::onDraftReturnLocationChange,
            onRemarkChange = viewModel::onDraftRemarkChange,
            onConfirm = viewModel::confirmInfo,
            onDismiss = viewModel::dismissInfoDialog
        )
    }
}

@Composable
fun TempReturnConfirmContent(
    state: TempReturnConfirmUiState,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onOpenInfoDialog: () -> Unit,
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
                title = "确认归还",
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
                        "暂无临时归还单",
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
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                TempListCard(
                                    title = order.orderNo,
                                    status = order.status.label,
                                    fields = listOf(
                                        DisplayField(
                                            "来源借出单号",
                                            order.borrowOrderNo.ifBlank { "-" }
                                        ),
                                        DisplayField("工单号", state.workOrderNo.ifBlank { "-" }),
                                        DisplayField("车间", order.workshop.ifBlank { "-" }),
                                        DisplayField("工班", order.workGroup.ifBlank { "-" }),
                                        DisplayField("借用人", order.borrower.ifBlank { "-" }),
                                        DisplayField("归还人", state.returner.ifBlank { "-" }),
                                        DisplayField(
                                            "归还库位",
                                            state.returnLocation.ifBlank { "-" }
                                        ),
                                        DisplayField("归还时间", order.returnTime.ifBlank { "-" }),
                                        DisplayField("备注", state.remark.ifBlank { "-" }),
                                        DisplayField("创建人", order.createdBy),
                                        DisplayField("创建时间", order.createdAt)
                                    ),
                                    progress = OrderProgress(
                                        "归还进度",
                                        order.returnedCount,
                                        order.totalCount
                                    )
                                )
                                Button(
                                    onClick = onOpenInfoDialog,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text("登记归还信息")
                                }
                            }
                        }
                    ) { line ->
                        TempListCard(
                            title = line.description,
                            status = line.status.label,
                            fields = listOf(
                                DisplayField("行号", line.lineNo.toString()),
                                DisplayField("物资编码", line.itemCode),
                                DisplayField("RFID 编码", line.rfid),
                                DisplayField("单位", line.unit)
                            ),
                            actionText = if (line.status == TempReturnDetailStatus.RETURNED) {
                                null
                            } else {
                                "确认归还"
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
