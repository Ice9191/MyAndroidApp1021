package com.hxb.myandroidapp_1021.feature.tempborrow.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hxb.myandroidapp_1021.commonmodel.DisplayField
import com.hxb.myandroidapp_1021.core.ui.components.InputFieldOne
import com.hxb.myandroidapp_1021.core.ui.components.OrderList
import com.hxb.myandroidapp_1021.core.ui.components.TempListCard
import com.hxb.myandroidapp_1021.core.ui.components.TopAppBar

/**
 * 临时借出的器具检索页：只有输入关键字后才展示可添加的库存器具，添加结果保存在共享页面状态中。
 */
@Composable
fun TempBorrowSearchToolsScreen(
    viewModel: TempBorrowListViewModel,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    TempBorrowSearchToolsContent(
        state = state,
        onBackClick = onBackClick,
        onHomeClick = onHomeClick,
        onToolQueryChange = viewModel::onToolQueryChange,
        onToggleTool = viewModel::toggleTool
    )
}

@Composable
fun TempBorrowSearchToolsContent(
    state: TempBorrowListUiState,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onToolQueryChange: (String) -> Unit,
    onToggleTool: (String) -> Unit
) {
    val listState = rememberLazyListState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = "检索工器具",
                onBackClick = onBackClick,
                onHomeClick = onHomeClick
            )
        }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            InputFieldOne(
                state.toolQuery,
                onToolQueryChange,
                "工具编码 / 名称 / 标签 ID"
            )
            OrderList(
                orders = state.visibleInventory,
                listState = listState,
                emptyText = if (state.toolQuery.isBlank()) "" else "暂无匹配的在库器具",
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                key = { it.rfid }
            ) { tool ->
                val selected = tool.rfid in state.selectedRfids
                TempListCard(
                    title = tool.description,
                    status = if (selected) "已添加" else "在库",
                    fields = listOf(
                        DisplayField("物资编码", tool.itemCode),
                        DisplayField("RFID 编码", tool.rfid),
                        DisplayField("存放货位", tool.location),
                        DisplayField("单位", tool.unit)
                    ),
                    actionText = if (selected) "移除" else "添加器具",
                    onAction = { onToggleTool(tool.rfid) }
                )
            }
        }
    }
}
