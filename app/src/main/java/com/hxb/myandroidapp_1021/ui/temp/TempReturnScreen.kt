package com.hxb.myandroidapp_1021.ui.temp

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hxb.myandroidapp_1021.components.BottomButton
import com.hxb.myandroidapp_1021.components.ConfirmDialog
import com.hxb.myandroidapp_1021.components.InputFieldOne
import com.hxb.myandroidapp_1021.components.ScanTabs
import com.hxb.myandroidapp_1021.components.TempCard
import com.hxb.myandroidapp_1021.components.TempMessage
import com.hxb.myandroidapp_1021.components.TopAppBar
import com.hxb.myandroidapp_1021.components.fields
import com.hxb.myandroidapp_1021.ui.commonmodel.DisplayField

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
        onConfirm = viewModel::confirmLine
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
    onConfirm: () -> Unit
) {
    val order = state.selectedOrder
    val hasReturned = order?.lines?.any { it.returned } == true
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
            if (order != null) BottomButton("前往 PDA 上架", onPutawayClick, hasReturned)
        }
    ) { paddingValues ->
        Column(Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            TempMessage(state.message)
            LazyColumn(
                modifier = Modifier.weight(1f), contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (order == null) {
                    item {
                        Column {
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
                    }
                    if (state.visibleOrders.isEmpty()) item {
                        Text(
                            "暂无匹配的归还记录",
                            Modifier.padding(8.dp)
                        )
                    }
                    items(state.visibleOrders, key = { it.orderNo }) { current ->
                        TempCard(
                            title = current.orderNo,
                            status = current.status,
                            fields = listOf(
                                DisplayField("工单号", current.workOrderNo),
                                DisplayField(
                                    "借用人",
                                    current.lines.map { it.borrower }.distinct().joinToString("、")
                                ),
                                DisplayField(
                                    "原借用单",
                                    current.lines.map { it.borrowOrderNo }.distinct()
                                        .joinToString("、")
                                ),
                                DisplayField(
                                    "归还进度",
                                    "${current.lines.count { it.returned }} / ${current.lines.size}"
                                )
                            ),
                            actionText = "查看待还器具",
                            onAction = { onSelectOrder(current.orderNo) }
                        )
                    }
                } else {
                    item {
                        TempCard(
                            title = order.orderNo, status = order.status,
                            fields = listOf(
                                DisplayField("经办人", order.createdBy),
                                DisplayField(
                                    "归还进度",
                                    "${order.lines.count { it.returned }} / ${order.lines.size}"
                                )
                            )
                        )
                    }
                    item {
                        Column {
                            InputFieldOne(
                                state.returner,
                                onReturnerChange,
                                "归还人（必填）",
                                showSearchIcon = false,
                                enabled = !hasReturned
                            )
                            InputFieldOne(
                                state.workOrderNo,
                                onWorkOrderChange,
                                "工单号（必填）",
                                showSearchIcon = false,
                                enabled = !hasReturned
                            )
                            InputFieldOne(
                                state.location,
                                onLocationChange,
                                "归还库位（必填）",
                                showSearchIcon = false,
                                enabled = !hasReturned
                            )
                            InputFieldOne(
                                state.remark,
                                onRemarkChange,
                                "备注",
                                showSearchIcon = false,
                                enabled = !hasReturned
                            )
                            Text(
                                "逐条核对实物后确认归还；归还后仍需 PDA 上架",
                                Modifier.padding(8.dp)
                            )
                        }
                    }
                    items(order.lines, key = { it.lineNo }) { line ->
                        TempCard(
                            title = line.tool.name,
                            status = if (line.returned) "已归还" else "待确认归还",
                            fields = listOf(
                                DisplayField(
                                    "行号",
                                    line.lineNo.toString()
                                )
                            ) + line.tool.fields() + listOf(
                                DisplayField("原借用单", line.borrowOrderNo),
                                DisplayField("借用人", line.borrower),
                                DisplayField("借出时间", line.borrowedAt)
                            ) + if (line.returned) listOf(
                                DisplayField(
                                    "归还时间",
                                    line.returnedAt
                                )
                            ) else emptyList(),
                            actionText = if (line.returned) null else "确认归还",
                            onAction = { onRequestConfirmation(line.tool.rfid) }
                        )
                    }
                }
            }
        }
    }
    val line = order?.lines?.firstOrNull { it.tool.rfid == state.confirmationRfid && !it.returned }
    if (line != null) {
        ConfirmDialog("确认归还", onConfirm, { onRequestConfirmation(null) }) {
            Text("${line.tool.name}\nRFID：${line.tool.rfid}\n归还人：${state.returner}\n归还库位：${state.location}\n确认已收到实物？归还后还需上架。")
        }
    }
}

