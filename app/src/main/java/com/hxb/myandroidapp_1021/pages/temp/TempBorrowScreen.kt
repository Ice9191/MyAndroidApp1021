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
import com.hxb.myandroidapp_1021.components.BottomButton
import com.hxb.myandroidapp_1021.components.ConfirmDialog
import com.hxb.myandroidapp_1021.components.InputFieldOne
import com.hxb.myandroidapp_1021.components.OrderList
import com.hxb.myandroidapp_1021.components.OrderProgress
import com.hxb.myandroidapp_1021.components.ScanTabs
import com.hxb.myandroidapp_1021.components.TempListCard
import com.hxb.myandroidapp_1021.components.TopAppBar
import com.hxb.myandroidapp_1021.components.TopNotification
import com.hxb.myandroidapp_1021.pages.commonmodel.DisplayField

@Composable
fun TempBorrowScreen(
    viewModel: TempBorrowViewModel,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onPickingClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val back =
        { if (state.editing || state.selectedOrder != null) viewModel.showList() else onBackClick() }
    BackHandler(state.editing || state.selectedOrder != null, back)
    TempBorrowContent(
        state = state,
        onBackClick = back,
        onHomeClick = onHomeClick,
        onNewOrder = viewModel::startNewOrder,
        onSelectOrder = viewModel::selectOrder,
        onQueryChange = viewModel::onQueryChange,
        onTabSelected = viewModel::onTabSelected,
        onBorrowerChange = viewModel::onBorrowerChange,
        onWorkOrderChange = viewModel::onWorkOrderChange,
        onWorkshopChange = viewModel::onWorkshopChange,
        onTeamChange = viewModel::onTeamChange,
        onRemarkChange = viewModel::onRemarkChange,
        onToggleTool = viewModel::toggleTool,
        onSaveOrder = viewModel::saveOrder,
        onPickingClick = onPickingClick,
        onRequestConfirmation = viewModel::requestConfirmation,
        onConfirm = viewModel::confirmLine,
        onNotificationDismiss = viewModel::dismissNotification
    )
}

@Composable
fun TempBorrowContent(
    state: TempBorrowUiState,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onNewOrder: () -> Unit,
    onSelectOrder: (String) -> Unit,
    onQueryChange: (String) -> Unit,
    onTabSelected: (Int) -> Unit,
    onBorrowerChange: (String) -> Unit,
    onWorkOrderChange: (String) -> Unit,
    onWorkshopChange: (String) -> Unit,
    onTeamChange: (String) -> Unit,
    onRemarkChange: (String) -> Unit,
    onToggleTool: (String) -> Unit,
    onSaveOrder: () -> Unit,
    onPickingClick: () -> Unit,
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
                title = "临时借出",
                onBackClick = onBackClick, onHomeClick = onHomeClick
            )
        },
        bottomBar = {
            when {
                state.editing -> BottomButton(
                    "保存借出单 · 已选 ${state.selectedRfids.size} 件",
                    onSaveOrder
                )

                order == null -> BottomButton("新建临时借出", onNewOrder)
                !state.allPicked -> BottomButton("前往 PDA 拣选", onPickingClick)
            }
        }
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .clipToBounds()
        ) {
            Column(Modifier.fillMaxSize()) {
                when {
                    state.editing -> OrderList(
                        orders = state.visibleTools,
                        listState = listState,
                        emptyText = "暂无匹配的在库器具",
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        key = { it.rfid },
                        header = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.surface
                                ) {
                                    Column {
                                        Text(
                                            "借用信息",
                                            Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        InputFieldOne(
                                            state.borrower,
                                            onBorrowerChange,
                                            "借用人（必填）",
                                            showSearchIcon = false
                                        )
                                        InputFieldOne(
                                            state.workOrderNo,
                                            onWorkOrderChange,
                                            "工单号（必填）",
                                            showSearchIcon = false
                                        )
                                        InputFieldOne(
                                            state.workshop,
                                            onWorkshopChange,
                                            "车间",
                                            showSearchIcon = false
                                        )
                                        InputFieldOne(
                                            state.team,
                                            onTeamChange,
                                            "工班",
                                            showSearchIcon = false
                                        )
                                        InputFieldOne(
                                            state.remark,
                                            onRemarkChange,
                                            "备注",
                                            showSearchIcon = false
                                        )
                                    }
                                }
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.surface
                                ) {
                                    Column {
                                        Text(
                                            "选择在库器具 · 已选 ${state.selectedRfids.size} 件",
                                            Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        InputFieldOne(
                                            state.query,
                                            onQueryChange,
                                            "工具编码 / 名称 / 标签 ID"
                                        )
                                    }
                                }
                            }
                        }
                    ) { tool ->
                        val selected = tool.rfid in state.selectedRfids
                        TempListCard(
                            title = tool.name,
                            status = if (selected) "已选择" else "在库",
                            fields = listOf(
                                DisplayField("物资编码", tool.itemCode),
                                DisplayField("RFID 编码", tool.rfid),
                                DisplayField("存放货位", tool.location),
                                DisplayField("单位", tool.unit)
                            ),
                            actionText = if (selected) "移除选择" else "添加器具",
                            onAction = { onToggleTool(tool.rfid) }
                        )
                    }

                    order != null -> OrderList(
                        orders = order.lines,
                        listState = listState,
                        emptyText = "暂无器具明细",
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        key = { it.lineNo },
                        header = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                TempListCard(
                                    title = order.orderNo,
                                    status = order.status,
                                    fields = listOf(
                                        DisplayField("工单号", order.workOrderNo),
                                        DisplayField("借用人", order.borrower),
                                        DisplayField(
                                            "车间 / 工班",
                                            "${order.workshop} / ${order.team}"
                                        ),
                                        DisplayField("经办人", order.createdBy),
                                        DisplayField("创建时间", order.createdAt),
                                        DisplayField("备注", order.remark),
                                        DisplayField(
                                            "拣选进度",
                                            "${order.lines.count { it.tool.rfid in state.pickedRfids }} / ${order.lines.size}"
                                        )
                                    ),
                                    progress = OrderProgress(
                                        "借出进度",
                                        order.lines.count { it.borrowed },
                                        order.lines.size
                                    )
                                )
                                Text(
                                    if (state.allPicked) "拣选已完成，请核对实物后逐条确认借出" else "先完成本单全部器具的 PDA 拣选，再逐条确认借出",
                                    Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }
                    ) { line ->
                        TempListCard(
                            title = line.tool.name,
                            status = when {
                                line.borrowed -> "已借出"
                                line.tool.rfid in state.pickedRfids -> "已拣选 · 待确认借出"
                                else -> "待拣选"
                            },
                            fields = listOf(DisplayField("行号", line.lineNo.toString())) + listOf(
                                DisplayField("物资编码", line.tool.itemCode),
                                DisplayField("RFID 编码", line.tool.rfid),
                                DisplayField("存放货位", line.tool.location),
                                DisplayField("单位", line.tool.unit)
                            ) + if (line.borrowed) listOf(
                                DisplayField("借出时间", line.borrowedAt)
                            ) else emptyList(),
                            actionText = if (line.borrowed) null else "确认借出",
                            enabled = state.allPicked,
                            onAction = { onRequestConfirmation(line.tool.rfid) }
                        )
                    }

                    else -> Column(Modifier.weight(1f)) {
                        Column(Modifier.fillMaxWidth()) {
                            InputFieldOne(
                                state.query,
                                onQueryChange,
                                "借出单号 / 工单号 / 借用人"
                            )
                            ScanTabs(
                                listOf("全部", "草稿", "已借出"),
                                state.selectedTab,
                                onTabSelected
                            )
                        }
                        OrderList(
                            orders = state.visibleOrders,
                            listState = listState,
                            emptyText = "暂无临时借出单，点击下方按钮新建",
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            key = { it.orderNo }
                        ) { current ->
                            TempListCard(
                                title = current.orderNo,
                                status = current.status,
                                fields = listOf(
                                    DisplayField("工单号", current.workOrderNo),
                                    DisplayField("借用人", current.borrower),
                                    DisplayField("创建时间", current.createdAt)
                                ),
                                progress = OrderProgress(
                                    "借出进度",
                                    current.lines.count { it.borrowed },
                                    current.lines.size
                                ),
                                actionText = "查看单据",
                                onAction = { onSelectOrder(current.orderNo) }
                            )
                        }
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
    val line = order?.lines?.firstOrNull { it.tool.rfid == state.confirmationRfid && !it.borrowed }
    if (line != null) {
        ConfirmDialog("确认借出", onConfirm, { onRequestConfirmation(null) }) {
            Text("${line.tool.name}\nRFID：${line.tool.rfid}\n借用人：${order.borrower}\n确认实物已交给借用人？")
        }
    }
}
