package com.hxb.myandroidapp_1021.pages.temp

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hxb.myandroidapp_1021.core.ui.components.BottomButton
import com.hxb.myandroidapp_1021.core.ui.components.ConfirmDialog
import com.hxb.myandroidapp_1021.core.ui.components.InputFieldOne
import com.hxb.myandroidapp_1021.core.ui.components.OrderList
import com.hxb.myandroidapp_1021.core.ui.components.OrderProgress
import com.hxb.myandroidapp_1021.core.ui.components.ScanTabs
import com.hxb.myandroidapp_1021.core.ui.components.TempListCard
import com.hxb.myandroidapp_1021.core.ui.components.TopAppBar
import com.hxb.myandroidapp_1021.core.ui.components.TopNotification
import com.hxb.myandroidapp_1021.pages.commonmodel.DisplayField

@Composable
fun TempReturnScreen(
    viewModel: TempReturnViewModel,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onPutawayClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val back = { if (state.selectedOrder != null) viewModel.showList() else onBackClick() }
    BackHandler(state.selectedOrder != null, back)
    TempReturnContent(
        state = state,
        onBackClick = back,
        onHomeClick = onHomeClick,
        onSelectOrder = viewModel::selectOrder,
        onQueryChange = viewModel::onQueryChange,
        onTabSelected = viewModel::onTabSelected,
        onReturnerChange = viewModel::onReturnerChange,
        onWorkOrderChange = viewModel::onWorkOrderChange,
        onLocationChange = viewModel::onLocationChange,
        onRemarkChange = viewModel::onRemarkChange,
        onPutawayClick = onPutawayClick,
        onRequestConfirmation = viewModel::requestConfirmation,
        onConfirm = viewModel::confirmLine,
        onNotificationDismiss = viewModel::dismissNotification
    )
}

@Composable
fun TempReturnContent(
    state: TempReturnUiState,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onSelectOrder: (String) -> Unit,
    onQueryChange: (String) -> Unit,
    onTabSelected: (Int) -> Unit,
    onReturnerChange: (String) -> Unit,
    onWorkOrderChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onRemarkChange: (String) -> Unit,
    onPutawayClick: () -> Unit,
    onRequestConfirmation: (String?) -> Unit,
    onConfirm: () -> Unit,
    onNotificationDismiss: () -> Unit
) {
    val order = state.selectedOrder
    val listState = rememberLazyListState()

    Scaffold(
        modifier = Modifier.imePadding(), containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = "临时归还",
                onBackClick = onBackClick,
                onHomeClick = onHomeClick
            )
        },
        bottomBar = {
            if (order != null) BottomButton("前往 PDA 上架", onPutawayClick, state.hasReturned)
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
                    Column(Modifier.weight(1f)) {
                        Column(Modifier.fillMaxWidth()) {
                            InputFieldOne(
                                state.query,
                                onQueryChange,
                                "借用人 / 工单号 / 借用单号 / 归还单号"
                            )
                            ScanTabs(
                                listOf("全部", "待确认", "已归还"),
                                state.selectedTab,
                                onTabSelected
                            )
                        }
                        OrderList(
                            orders = state.visibleOrders,
                            listState = listState,
                            emptyText = "暂无匹配的归还记录",
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            key = { it.orderNo }
                        ) { current ->
                            TempListCard(
                                title = current.orderNo,
                                status = current.status,
                                fields = listOf(
                                    DisplayField("工单号", current.workOrderNo),
                                    DisplayField(
                                        "借用人",
                                        current.lines.map { it.borrower }.distinct()
                                            .joinToString("、")
                                    ),
                                    DisplayField(
                                        "原借用单",
                                        current.lines.map { it.borrowOrderNo }.distinct()
                                            .joinToString("、")
                                    )
                                ),
                                progress = OrderProgress(
                                    "归还进度",
                                    current.lines.count { it.returned },
                                    current.lines.size
                                ),
                                actionText = "查看待还器具",
                                onAction = { onSelectOrder(current.orderNo) }
                            )
                        }
                    }
                } else {
                    OrderList(
                        orders = order.lines,
                        listState = listState,
                        emptyText = "暂无归还明细",
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        key = { it.lineNo },
                        header = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                TempListCard(
                                    title = order.orderNo,
                                    status = order.status,
                                    fields = listOf(
                                        DisplayField("经办人", order.createdBy)
                                    ),
                                    progress = OrderProgress(
                                        "归还进度",
                                        order.lines.count { it.returned },
                                        order.lines.size
                                    )
                                )
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.surface
                                ) {
                                    Column {
                                        Text(
                                            "归还信息",
                                            Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        InputFieldOne(
                                            state.returner,
                                            onReturnerChange,
                                            "归还人（必填）",
                                            showSearchIcon = false,
                                            enabled = !state.hasReturned
                                        )
                                        InputFieldOne(
                                            state.workOrderNo,
                                            onWorkOrderChange,
                                            "工单号（必填）",
                                            showSearchIcon = false,
                                            enabled = !state.hasReturned
                                        )
                                        InputFieldOne(
                                            state.location,
                                            onLocationChange,
                                            "归还库位（必填）",
                                            showSearchIcon = false,
                                            enabled = !state.hasReturned
                                        )
                                        InputFieldOne(
                                            state.remark,
                                            onRemarkChange,
                                            "备注",
                                            showSearchIcon = false,
                                            enabled = !state.hasReturned
                                        )
                                        Text(
                                            "逐条核对实物后确认归还；归还后仍需 PDA 上架",
                                            Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    ) { line ->
                        TempListCard(
                            title = line.tool.name,
                            status = if (line.returned) "已归还" else "待确认归还",
                            fields = listOf(DisplayField("行号", line.lineNo.toString())) + listOf(
                                DisplayField("物资编码", line.tool.itemCode),
                                DisplayField("RFID 编码", line.tool.rfid),
                                DisplayField("存放货位", line.tool.location),
                                DisplayField("单位", line.tool.unit),
                                DisplayField("原借用单", line.borrowOrderNo),
                                DisplayField("借用人", line.borrower),
                                DisplayField("借出时间", line.borrowedAt)
                            ) + if (line.returned) listOf(
                                DisplayField("归还时间", line.returnedAt)
                            ) else emptyList(),
                            actionText = if (line.returned) null else "确认归还",
                            onAction = { onRequestConfirmation(line.tool.rfid) }
                        )
                    }
                }
            }
            TopNotification(
                message = state.notificationMessage,
                notificationId = state.notificationId,
                onDismiss = onNotificationDismiss
            )
        }
    }
    val line = order?.lines?.firstOrNull { it.tool.rfid == state.confirmationRfid && !it.returned }
    if (line != null) {
        ConfirmDialog("确认归还", onConfirm, { onRequestConfirmation(null) }) {
            Text("${line.tool.name}\nRFID：${line.tool.rfid}\n归还人：${state.returner}\n归还库位：${state.location}\n确认已收到实物？归还后还需上架。")
        }
    }
}
