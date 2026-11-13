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
        onConfirm = viewModel::confirmLine
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
    onConfirm: () -> Unit
) {
    val order = state.selectedOrder
    val allPicked = order != null && order.lines.all { it.tool.rfid in state.pickedRfids }
    Scaffold(
        modifier = Modifier.imePadding(), containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = if (state.editing) "临时借出 · 新建" else "临时借出",
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
                !allPicked -> BottomButton("前往 PDA 拣选", onPickingClick)
            }
        }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TempMessage(state.message)
            LazyColumn(
                modifier = Modifier.weight(1f), contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                when {
                    state.editing -> {
                        item {
                            Column {
                                Text(
                                    "借用信息",
                                    Modifier.padding(8.dp),
                                    style = MaterialTheme.typography.titleMedium
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
                                Text(
                                    "选择在库器具 · 已选 ${state.selectedRfids.size} 件",
                                    Modifier.padding(8.dp)
                                )
                                InputFieldOne(
                                    state.query,
                                    onQueryChange,
                                    "工具编码 / 名称 / 标签 ID"
                                )
                            }
                        }
                        if (state.visibleTools.isEmpty()) item {
                            Text(
                                "暂无匹配的在库器具",
                                Modifier.padding(8.dp)
                            )
                        }
                        items(state.visibleTools, key = { it.rfid }) { tool ->
                            val selected = tool.rfid in state.selectedRfids
                            TempCard(
                                title = tool.name,
                                status = if (selected) "已选择" else "在库",
                                fields = tool.fields(),
                                actionText = if (selected) "移除选择" else "添加器具",
                                onAction = { onToggleTool(tool.rfid) }
                            )
                        }
                    }

                    order != null -> {
                        item {
                            TempCard(
                                title = order.orderNo, status = order.status,
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
                                    ),
                                    DisplayField(
                                        "借出进度",
                                        "${order.lines.count { it.borrowed }} / ${order.lines.size}"
                                    )
                                )
                            )
                        }
                        item {
                            Text(
                                if (allPicked) "拣选已完成，请核对实物后逐条确认借出" else "先完成本单全部器具的 PDA 拣选，再逐条确认借出",
                                Modifier.padding(8.dp)
                            )
                        }
                        items(order.lines, key = { it.lineNo }) { line ->
                            TempCard(
                                title = line.tool.name,
                                status = if (line.borrowed) "已借出" else if (line.tool.rfid in state.pickedRfids) "已拣选 · 待确认借出" else "待拣选",
                                fields = listOf(
                                    DisplayField(
                                        "行号",
                                        line.lineNo.toString()
                                    )
                                ) + line.tool.fields() +
                                        if (line.borrowed) listOf(
                                            DisplayField(
                                                "借出时间",
                                                line.borrowedAt
                                            )
                                        ) else emptyList(),
                                actionText = if (line.borrowed) null else "确认借出",
                                enabled = allPicked,
                                onAction = { onRequestConfirmation(line.tool.rfid) }
                            )
                        }
                    }

                    else -> {
                        item {
                            Column {
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
                        }
                        if (state.visibleOrders.isEmpty()) item {
                            Text(
                                "暂无临时借出单，点击下方按钮新建",
                                Modifier.padding(8.dp)
                            )
                        }
                        items(state.visibleOrders, key = { it.orderNo }) { current ->
                            TempCard(
                                title = current.orderNo,
                                status = current.status,
                                fields = listOf(
                                    DisplayField("工单号", current.workOrderNo),
                                    DisplayField("借用人", current.borrower),
                                    DisplayField("创建时间", current.createdAt),
                                    DisplayField(
                                        "借出进度",
                                        "${current.lines.count { it.borrowed }} / ${current.lines.size}"
                                    )
                                ),
                                actionText = "查看单据",
                                onAction = { onSelectOrder(current.orderNo) }
                            )
                        }
                    }
                }
            }
        }
    }
    val line = order?.lines?.firstOrNull { it.tool.rfid == state.confirmationRfid && !it.borrowed }
    if (line != null) {
        ConfirmDialog("确认借出", onConfirm, { onRequestConfirmation(null) }) {
            Text("${line.tool.name}\nRFID：${line.tool.rfid}\n借用人：${order.borrower}\n确认实物已交给借用人？")
        }
    }
}

