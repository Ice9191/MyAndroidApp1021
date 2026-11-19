package com.hxb.myandroidapp_1021.feature.tempborrow.presentation

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
import androidx.compose.material3.Button
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
import com.hxb.myandroidapp_1021.core.ui.components.InputFieldOne
import com.hxb.myandroidapp_1021.core.ui.components.OrderList
import com.hxb.myandroidapp_1021.core.ui.components.OrderProgress
import com.hxb.myandroidapp_1021.core.ui.components.ScanTabs
import com.hxb.myandroidapp_1021.core.ui.components.TempListCard
import com.hxb.myandroidapp_1021.core.ui.components.ToastNotification
import com.hxb.myandroidapp_1021.core.ui.components.TopAppBar
import com.hxb.myandroidapp_1021.commonmodel.DisplayField

@Composable
fun TempBorrowScreen(
    viewModel: TempBorrowViewModel,
    onBackClick: () -> Unit,
    onSearchToolsClick: () -> Unit,
    onSelectOrder: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val back = {
        if (state.editing) viewModel.showList() else onBackClick()
    }
    BackHandler(state.editing, back)
    TempBorrowContent(
        state = state,
        onBackClick = back,
        onNewOrder = viewModel::startNewOrder,
        onSelectOrder = onSelectOrder,
        onQueryChange = viewModel::onQueryChange,
        onTabSelected = viewModel::onTabSelected,
        onBorrowerChange = viewModel::onBorrowerChange,
        onWorkOrderChange = viewModel::onWorkOrderChange,
        onWorkshopChange = viewModel::onWorkshopChange,
        onWorkGroupChange = viewModel::onWorkGroupChange,
        onRemarkChange = viewModel::onRemarkChange,
        onToggleTool = viewModel::toggleTool,
        onSaveOrder = viewModel::saveOrder,
        onSearchToolsClick = onSearchToolsClick,
        onNotificationDismiss = viewModel::dismissNotification
    )
}

@Composable
fun TempBorrowContent(
    state: TempBorrowUiState,
    onBackClick: () -> Unit,
    onNewOrder: () -> Unit,
    onSelectOrder: (String) -> Unit,
    onQueryChange: (String) -> Unit,
    onTabSelected: (Int) -> Unit,
    onBorrowerChange: (String) -> Unit,
    onWorkOrderChange: (String) -> Unit,
    onWorkshopChange: (String) -> Unit,
    onWorkGroupChange: (String) -> Unit,
    onRemarkChange: (String) -> Unit,
    onToggleTool: (String) -> Unit,
    onSaveOrder: () -> Unit,
    onSearchToolsClick: () -> Unit,
    onNotificationDismiss: () -> Unit
) {
    val listState = rememberLazyListState()
    val formListState = rememberLazyListState()

    Scaffold(
        modifier = Modifier.imePadding(), containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = "临时借出",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            if (state.editing) {
                BottomButton(
                    "保存借出单",
                    onSaveOrder,
                    !state.isSubmitting
                )
            } else {
                BottomButton("新建临时借出", onNewOrder)
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
                if (state.editing) {
                    OrderList(
                        orders = state.selectedTools,
                        listState = formListState,
                        emptyText = "暂无已添加的工器具",
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
                                            Modifier.padding(horizontal = 16.dp)
                                                .padding(top = 12.dp, bottom = 6.dp),
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
                                            state.workGroup,
                                            onWorkGroupChange,
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
                                Button(
                                    onClick = onSearchToolsClick,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text("前往检索工器具")
                                }
                            }
                        }
                    ) { tool ->
                        TempListCard(
                            title = tool.description,
                            status = "已添加",
                            fields = listOf(
                                DisplayField("物资编码", tool.itemCode),
                                DisplayField("RFID 编码", tool.rfid),
                                DisplayField("存放货位", tool.location),
                                DisplayField("单位", tool.unit)
                            ),
                            actionText = "移除",
                            onAction = { onToggleTool(tool.rfid) }
                        )
                    }
                } else {
                    Column(Modifier.fillMaxWidth()) {
                        InputFieldOne(
                            state.query,
                            onQueryChange,
                            "借出单号 / 工单号 / 借用人"
                        )
                        ScanTabs(
                            listOf("全部", "待确认", "已借出"),
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
                            status = current.status.label,
                            fields = listOf(
                                DisplayField("工单号", current.workOrderNo),
                                DisplayField("借用人", current.borrower),
                                DisplayField("借出时间", current.borrowTime.ifBlank { "-" })
                            ),
                            progress = OrderProgress(
                                "借出进度",
                                current.borrowedCount,
                                current.totalCount
                            ),
                            actionText = "查看单据",
                            onAction = { onSelectOrder(current.orderNo) }
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
