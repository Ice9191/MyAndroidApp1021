package com.hxb.myandroidapp_1021.ui.picking

import android.annotation.SuppressLint
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hxb.myandroidapp_1021.components.TopAppBar
import com.hxb.myandroidapp_1021.components.BottomButton
import com.hxb.myandroidapp_1021.components.ConfirmDialog
import com.hxb.myandroidapp_1021.components.ManualScanCard
import com.hxb.myandroidapp_1021.components.OrderList
import com.hxb.myandroidapp_1021.components.ScanTabs
import com.hxb.myandroidapp_1021.ui.commonmodel.DisplayField

@Composable
fun PickingManualScreen(
    viewModel: PickingScanViewModel,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PickingManualContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onHomeClick = onHomeClick,
        onConfirmToggleRead = { ids -> viewModel.toggleRead(ids) }
    )
}

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun PickingManualContent(
    uiState: PickingScanUiState,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onConfirmToggleRead: (Set<Int>) -> Unit
) {
    val bgColor = MaterialTheme.colorScheme.background
    var selectedTab by remember { mutableStateOf(0) }
    var selectedIds by remember(selectedTab) { mutableStateOf(emptySet<Int>()) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    val readList = uiState.readList
    val unreadList = uiState.unreadList
    val listState = rememberLazyListState()

    Scaffold(
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = "工单拣选",
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
                key = { it.id }
            ) { tool ->
                ManualScanCard(
                    title = tool.name,
                    lineLabel = "行号 ${tool.id.toString().padStart(2, '0')}",
                    statusText = if (tool.isRead) "已读取" else "未读取",
                    isRead = tool.isRead,
                    fields = listOf(
                        DisplayField("物资编码", tool.itemCode),
                        DisplayField("RFID编码", tool.rfid),
                        DisplayField("推荐货位", tool.location),
                        DisplayField("单位", tool.unit)
                    ),
                    selected = tool.id in selectedIds,
                    onSelectedChange = { selected ->
                        selectedIds = if (selected) selectedIds + tool.id else selectedIds - tool.id
                    }
                )
            }
        }
    }

    // 确认弹窗
    if (showConfirmDialog) {
        val toReadCount = unreadList.count { it.id in selectedIds }
        val toUnreadCount = readList.count { it.id in selectedIds }
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
