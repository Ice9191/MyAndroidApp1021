package com.hxb.myandroidapp_1021.feature.tempreturn.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hxb.myandroidapp_1021.commonmodel.DisplayField
import com.hxb.myandroidapp_1021.core.ui.components.InputFieldOne
import com.hxb.myandroidapp_1021.core.ui.components.OrderList
import com.hxb.myandroidapp_1021.core.ui.components.OrderProgress
import com.hxb.myandroidapp_1021.core.ui.components.ScanTabs
import com.hxb.myandroidapp_1021.core.ui.components.TempListCard
import com.hxb.myandroidapp_1021.core.ui.components.TopAppBar

/**
 * 临时归还列表页：只展示由临时借出自动生成的归还单，不提供新建入口。
 */
@Composable
fun TempReturnListScreen(
    viewModel: TempReturnListViewModel,
    onBackClick: () -> Unit,
    onSelectOrder: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    // 每次进入列表都重新读取共享数据，避免漏掉借出确认后新生成的归还单。
    LaunchedEffect(Unit) {
        viewModel.refresh()
    }
    TempReturnListContent(
        state = state,
        onBackClick = onBackClick,
        onSelectOrder = onSelectOrder,
        onQueryChange = viewModel::onQueryChange,
        onTabSelected = viewModel::onTabSelected
    )
}

@Composable
fun TempReturnListContent(
    state: TempReturnListUiState,
    onBackClick: () -> Unit,
    onSelectOrder: (String) -> Unit,
    onQueryChange: (String) -> Unit,
    onTabSelected: (Int) -> Unit
) {
    val listState = rememberLazyListState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = "临时归还",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .clipToBounds()
        ) {
            InputFieldOne(
                state.query,
                onQueryChange,
                "借出单号 / 工单号 / 借用人"
            )
            ScanTabs(
                listOf("全部", "待确认", "已归还"),
                state.selectedTab,
                onTabSelected
            )
            OrderList(
                orders = state.visibleOrders,
                listState = listState,
                emptyText = "",
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                key = { it.orderNo }
            ) { current ->
                TempListCard(
                    title = current.orderNo,
                    status = current.status.label,
                    fields = listOf(
                        DisplayField("来源借出单号", current.borrowOrderNo.ifBlank { "-" }),
                        DisplayField("工单号", current.workOrderNo.ifBlank { "-" }),
                        DisplayField("车间", current.workshop.ifBlank { "-" }),
                        DisplayField("工班", current.workGroup.ifBlank { "-" }),
                        DisplayField("借用人", current.borrower.ifBlank { "-" }),
                        DisplayField("归还人", current.returner.ifBlank { "-" }),
                        DisplayField("归还时间", current.returnTime.ifBlank { "-" })
                    ),
                    progress = OrderProgress(
                        "归还进度",
                        current.returnedCount,
                        current.totalCount
                    ),
                    actionText = "查看单据",
                    onAction = { onSelectOrder(current.orderNo) }
                )
            }
        }
    }
}
