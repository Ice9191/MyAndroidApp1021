package com.hxb.myandroidapp_1021.feature.returns.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.hxb.myandroidapp_1021.core.ui.components.BottomButton
import com.hxb.myandroidapp_1021.core.ui.components.ConfirmDialog
import com.hxb.myandroidapp_1021.core.ui.components.ManualScanCard
import com.hxb.myandroidapp_1021.core.ui.components.OrderList
import com.hxb.myandroidapp_1021.core.ui.components.ScanTabs
import com.hxb.myandroidapp_1021.core.ui.components.TopAppBar

/**
 * 人工读取页：勾选后整批切换读取状态；卡片与拣选人工页同为直角白底样式。
 */
@Composable
fun ReturnManualContent(
    uiState: ReturnScanUiState,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onConfirmToggleRead: (Set<Int>) -> Unit
) {
    val bgColor = MaterialTheme.colorScheme.background
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedIds by remember(selectedTab) { mutableStateOf(emptySet<Int>()) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    // 仅待上架明细参与人工读取，损坏、待送检、已上架明细不进入上架流程
    val readList = uiState.readList.filter { it.canPutaway }
    val unreadList = uiState.unreadList.filter { it.canPutaway }
    val listState = rememberLazyListState()

    Scaffold(
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = "工单归还上架",
                onBackClick = onBackClick,
                onHomeClick = onHomeClick
            )
        },
        bottomBar = {
            BottomButton(
                text = if (selectedTab == 0) "取消读取" else "人工读取",
                onClick = { showConfirmDialog = true },
                enabled = selectedIds.isNotEmpty()
            )
        }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val tabLabels = listOf("已读取 ${readList.size}", "未读取 ${unreadList.size}")
            ScanTabs(
                labels = tabLabels,
                selectedIndex = selectedTab,
                onTabSelected = { selectedTab = it }
            )
            val visibleTools = if (selectedTab == 0) readList else unreadList

            OrderList(
                orders = visibleTools,
                listState = listState,
                emptyText = if (selectedTab == 0) "暂无已读取工具" else "暂无未读取工具",
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                key = { it.lineNo }
            ) { tool ->
                ManualScanCard(
                    title = tool.description,
                    lineLabel = "行号 ${tool.lineNo.toString().padStart(2, '0')}",
                    statusText = if (tool.isRead) "已读取" else "未读取",
                    isRead = tool.isRead,
                    fields = tool.displayFields(),
                    selected = tool.lineNo in selectedIds,
                    onSelectedChange = { selected ->
                        selectedIds = if (selected) selectedIds + tool.lineNo else selectedIds - tool.lineNo
                    },
                    shape = RectangleShape,
                    containerColor = Color.White
                )
            }
        }
    }

    // 确认弹窗：按当前 Tab 统计本次要读取/取消读取的条数
    if (showConfirmDialog) {
        val toReadCount = unreadList.count { it.lineNo in selectedIds }
        val toUnreadCount = readList.count { it.lineNo in selectedIds }
        ConfirmDialog(
            title = "确认操作",
            onConfirm = {
                onConfirmToggleRead(selectedIds)
                selectedIds = emptySet()
                showConfirmDialog = false
            },
            onDismiss = { showConfirmDialog = false }
        ) {
            Column {
                if (toReadCount > 0) Text("人工读取 $toReadCount 件工具")
                if (toUnreadCount > 0) Text("取消读取 $toUnreadCount 件工具")
            }
        }
    }
}
