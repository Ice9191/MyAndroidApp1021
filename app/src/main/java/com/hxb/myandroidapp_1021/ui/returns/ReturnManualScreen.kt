package com.hxb.myandroidapp_1021.ui.returns

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hxb.myandroidapp_1021.components.BottomButton
import com.hxb.myandroidapp_1021.components.ConfirmDialog
import com.hxb.myandroidapp_1021.components.ManualScanCard
import com.hxb.myandroidapp_1021.components.OrderList
import com.hxb.myandroidapp_1021.components.ScanTabs
import com.hxb.myandroidapp_1021.ui.common.DisplayField

@Composable
fun ReturnManualScreen(
    viewModel: ReturnScanViewModel,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ReturnManualContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onHomeClick = onHomeClick,
        onConfirmToggleRead = { ids -> viewModel.toggleRead(ids) }
    )
}

@SuppressLint("UseOfNonLambdaOffsetOverload")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReturnManualContent(
    uiState: ReturnScanUiState,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onConfirmToggleRead: (Set<Int>) -> Unit
) {
    val themeBlue = Color(0xFF1E71C8)
    val bgColor = Color(0xFFF5F6F8)
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
                modifier = Modifier.height(75.dp),
                title = {
                    Text(
                        text = "归还·人工读取",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.offset(x = (-16).dp)
                    )
                },
                navigationIcon = {
                    IconButton(shape = RoundedCornerShape(4.dp), onClick = onBackClick, modifier = Modifier.offset(x = (-7).dp)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(shape = RoundedCornerShape(4.dp), onClick = onHomeClick, modifier = Modifier.offset(x = (-4).dp)) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = themeBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
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
                        DisplayField("存放货位", tool.location),
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
