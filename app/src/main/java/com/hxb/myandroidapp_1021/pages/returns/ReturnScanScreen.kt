package com.hxb.myandroidapp_1021.pages.returns

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hxb.myandroidapp_1021.components.BottomButton
import com.hxb.myandroidapp_1021.components.ConfirmDialog
import com.hxb.myandroidapp_1021.components.ManualScanCard
import com.hxb.myandroidapp_1021.components.OrderList
import com.hxb.myandroidapp_1021.components.ScanControlButton
import com.hxb.myandroidapp_1021.components.ScanControlButtons
import com.hxb.myandroidapp_1021.components.ScanTabs
import com.hxb.myandroidapp_1021.components.ToolsScanList
import com.hxb.myandroidapp_1021.components.TopAppBar
import com.hxb.myandroidapp_1021.components.TopInfo
import com.hxb.myandroidapp_1021.pages.commonmodel.DisplayField
import com.hxb.myandroidapp_1021.pages.commonmodel.ScanTab

@Composable
fun ReturnScanScreen(
    viewModel: ReturnScanViewModel,
    onBackClick: () -> Unit = {},
    onNextClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // 人工读取作为本页子页面展示，不单独跳转；子页面内返回键回到扫描页
    var showManual by rememberSaveable { mutableStateOf(false) }
    BackHandler(enabled = showManual) { showManual = false }

    if (showManual) {
        ReturnManualContent(
            uiState = uiState,
            onBackClick = { showManual = false },
            onHomeClick = onHomeClick,
            onConfirmToggleRead = viewModel::toggleRead
        )
    } else {
        ReturnScanContent(
            uiState = uiState,
            onManualClick = { showManual = true },
            onBackClick = onBackClick,
            onNextClick = onNextClick,
            onSettingsClick = onSettingsClick,
            onHomeClick = onHomeClick
        )
    }
}

@Composable
fun ReturnScanContent(
    uiState: ReturnScanUiState,
    onManualClick: () -> Unit,
    onBackClick: () -> Unit = {},
    onNextClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    val bgColor = MaterialTheme.colorScheme.background
    val order = uiState.order
    val infoFields = listOf(
        DisplayField("归还单号", order?.orderNo.orEmpty()),
        DisplayField("单据状态", order?.status?.label.orEmpty()),
        DisplayField("工单号", order?.workOrderNo.orEmpty()),
        DisplayField("归属车间", order?.workshop.orEmpty()),
        DisplayField("归还人", order?.returner.orEmpty()),
        DisplayField("工具数量", "${uiState.tools.size}件"),
        DisplayField("归还时间", order?.returnedDate.orEmpty()),
        DisplayField("创建人", order?.createdBy.orEmpty()),
        DisplayField("创建时间", order?.createdAt.orEmpty())
    )
    var selectedTab by rememberSaveable { mutableStateOf(ScanTab.All) }
    var viewportHeight by remember { mutableIntStateOf(0) }
    var controlsAndTabsHeight by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current

    // 防止内外两层 LazyColumn 滚动冲突
    val listScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset = available

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity =
                available
        }
    }
    val isNextEnabled =
        !uiState.isLoading && uiState.readList.isNotEmpty() && uiState.unreadList.isEmpty()
    val visibleTools = when (selectedTab) {
        ScanTab.All -> uiState.tools
        ScanTab.Read -> uiState.readList
        ScanTab.Unread -> uiState.unreadList
    }
    val tabLabels = listOf(
        "全部 ${uiState.tools.size}",
        "已读取 ${uiState.readList.size}",
        "未读取 ${uiState.unreadList.size}"
    )

    Scaffold(
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = "工单归还上架·读取",
                onBackClick = onBackClick,
                onHomeClick = onHomeClick,
                onConfigClick = onSettingsClick
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .onSizeChanged { viewportHeight = it.height }
        ) {
            item {
                Column {
                    // === 1. 顶部信息卡片 ===
                    TopInfo(fields = infoFields)
                    HorizontalDivider(thickness = 8.dp, color = bgColor)

                    Column(
                        modifier = Modifier.onSizeChanged { controlsAndTabsHeight = it.height }
                    ) {
                        // === 2. 读取控制区 ===
                        ScanControlButtons(
                            firstButton = ScanControlButton(
                                "RFID 待开放",
                                onClick = {},
                                enabled = false
                            ),
                            secondButton = ScanControlButton(
                                "人工读取",
                                onClick = onManualClick,
                                enabled = !uiState.isLoading
                            ),
                            thirdButton = ScanControlButton(
                                "下一步",
                                onClick = onNextClick,
                                enabled = isNextEnabled
                            )
                        )
                        HorizontalDivider(thickness = 8.dp, color = bgColor)

                        // === 3. 三个 Tab ===
                        ScanTabs(
                            labels = tabLabels,
                            selectedIndex = selectedTab.ordinal,
                            onTabSelected = { selectedTab = ScanTab.entries[it] }
                        )
                    }
                }
            }

            // === 4. Tab 内容 ===
            item {
                ToolsScanList(
                    tools = visibleTools,
                    key = { it.id },
                    title = { it.name },
                    lineLabel = { "行号 ${it.id.toString().padStart(2, '0')}" },
                    statusText = { if (it.isRead) "已读取" else "未读取" },
                    isRead = { it.isRead },
                    fields = { tool ->
                        listOf(
                            DisplayField("物资编码", tool.itemCode),
                            DisplayField("RFID编码", tool.rfid),
                            DisplayField("存放货位", tool.location),
                            DisplayField("单位", tool.unit)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(with(density) {
                            (viewportHeight - controlsAndTabsHeight).coerceAtLeast(0).toDp()
                        })
                        .nestedScroll(listScrollConnection)
                )
            }
        }
    }
}

@SuppressLint("UseOfNonLambdaOffsetOverload")
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
    val readList = uiState.readList
    val unreadList = uiState.unreadList
    val listState = rememberLazyListState()

    Scaffold(
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = "工单归还上架·人工读取",
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