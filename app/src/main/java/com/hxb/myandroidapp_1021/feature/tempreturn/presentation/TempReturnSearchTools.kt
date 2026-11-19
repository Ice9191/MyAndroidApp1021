package com.hxb.myandroidapp_1021.feature.tempreturn.presentation

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
import com.hxb.myandroidapp_1021.core.ui.components.InputFieldOne
import com.hxb.myandroidapp_1021.core.ui.components.OrderList
import com.hxb.myandroidapp_1021.core.ui.components.TempListCard
import com.hxb.myandroidapp_1021.core.ui.components.TopAppBar
import com.hxb.myandroidapp_1021.commonmodel.DisplayField

/**
 * 临时归还的器具检索页：输入借用人、工单号或借用单号后才展示待还器具。
 */
@Composable
fun TempReturnSearchToolsScreen(
    viewModel: TempReturnViewModel,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    TempReturnSearchToolsContent(
        state = state,
        onBackClick = onBackClick,
        onHomeClick = onHomeClick,
        onToolQueryChange = viewModel::onToolQueryChange,
        onToggleTool = viewModel::toggleTool
    )
}

@Composable
fun TempReturnSearchToolsContent(
    state: TempReturnUiState,
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
                title = "检索待还器具",
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
                "借用人 / 工单号 / 借用单号"
            )
            OrderList(
                orders = state.visiblePendingTools,
                listState = listState,
                emptyText = if (state.toolQuery.isBlank()) "" else "暂无匹配的待还器具",
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                key = { it.rfid }
            ) { tool ->
                val selected = tool.rfid in state.selectedRfids
                TempListCard(
                    title = tool.description,
                    status = if (selected) "已添加" else "待还",
                    fields = listOf(
                        DisplayField("借用单号", tool.borrowOrderNo),
                        DisplayField("工单号", tool.workOrderNo.ifBlank { "-" }),
                        DisplayField("借用人", tool.borrower.ifBlank { "-" }),
                        DisplayField("物资编码", tool.itemCode),
                        DisplayField("RFID 编码", tool.rfid),
                        DisplayField("单位", tool.unit)
                    ),
                    actionText = if (selected) "移除" else "添加器具",
                    onAction = { onToggleTool(tool.rfid) }
                )
            }
        }
    }
}
