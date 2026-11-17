package com.hxb.myandroidapp_1021.ui.pages.returns

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hxb.myandroidapp_1021.ui.components.ToastNotification
import com.hxb.myandroidapp_1021.ui.components.FormFilter
import com.hxb.myandroidapp_1021.ui.components.InputFieldOne
import com.hxb.myandroidapp_1021.ui.components.ScanControlButton
import com.hxb.myandroidapp_1021.ui.components.ScanControlButtons
import com.hxb.myandroidapp_1021.ui.components.ScanTabs
import com.hxb.myandroidapp_1021.ui.components.ScannerLifecycle
import com.hxb.myandroidapp_1021.ui.components.ToolsScanList
import com.hxb.myandroidapp_1021.ui.components.TopAppBar
import com.hxb.myandroidapp_1021.ui.components.TopInfo
import com.hxb.myandroidapp_1021.domain.scanner.ScannerState
import com.hxb.myandroidapp_1021.pages.common.DisplayField
import com.hxb.myandroidapp_1021.pages.common.ScanTab
import com.hxb.myandroidapp_1021.pages.returns.components.ConditionBadge
import com.hxb.myandroidapp_1021.pages.returns.components.DrawerFieldRow
import com.hxb.myandroidapp_1021.pages.returns.components.ReturnManualContent


@Composable
fun ReturnScanScreen(
    viewModel: ReturnScanViewModel,
    flowViewModel: ReturnListViewModel,
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scanner by viewModel.scannerState.collectAsStateWithLifecycle()
    ScannerLifecycle(enabled = !uiState.isSubmitting && !uiState.showConfirmDrawer, onSession = viewModel::onScannerSession)
    var showManual by rememberSaveable { mutableStateOf(false) }
    BackHandler(enabled = showManual) { showManual = false }

    ToastNotification(message = uiState.errorMessage ?: scanner.errorMessage)

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
            scanner = scanner,
            onScanClick = viewModel::startScanner,
            onManualClick = { showManual = true },
            onToolClick = viewModel::selectTool,
            onBackClick = onBackClick,
            onSettingsClick = onSettingsClick,
            onHomeClick = onHomeClick
        )
    }

    // 单条上架抽屉：只核对并提交当前选中的一条明细，成功后留在本页继续上架
    val selectedTool = uiState.selectedTool
    if (uiState.showConfirmDrawer && selectedTool != null) {
        FormFilter(
            error = uiState.submitError,
            title = "上架确认",
            resetText = "取消",
            onReset = viewModel::dismissConfirmDrawer,
            confirmText = if (uiState.isSubmitting) "提交中" else "确认上架",
            confirmEnabled = !uiState.isSubmitting && !selectedTool.isPutaway &&
                    !selectedTool.isBadCondition && selectedTool.canProcess && selectedTool.isRead &&
                    uiState.draftLocation.isNotBlank(),
            onConfirm = { viewModel.confirmPutaway(flowViewModel::updatePutawayProgress) },
            onDismiss = viewModel::dismissConfirmDrawer
        ) {
            DrawerFieldRow("工具编号", selectedTool.itemCode)
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "工具名称",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        selectedTool.name,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (selectedTool.condition.isNotBlank()) ConditionBadge(selectedTool.condition)
                }
            }
            DrawerFieldRow("RFID编码", selectedTool.rfid)
            val editable =
                selectedTool.canProcess && !selectedTool.isBadCondition && !selectedTool.isPutaway
            // 文档 1.2.2 锁定仓库；1.4.5 允许现场输入或扫描实际货位，不能使用固定候选值。
            DrawerFieldRow("仓库", uiState.draftWarehouse)
            InputFieldOne(
                uiState.draftLocation, viewModel::onDraftLocationChange, "扫描或输入实际货位码",
                showSearchIcon = false, enabled = editable && !uiState.isSubmitting
            )
            when {
                !selectedTool.canProcess || selectedTool.isBadCondition -> Text(
                    selectedTool.blockedReason.ifBlank { "该器具当前不能正常上架，请转送修或送检流程处理" },
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.error
                )

                selectedTool.isPutaway -> Text(
                    "该器具已上架，不能重复确认",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                !selectedTool.isRead -> Text(
                    "该器具尚未完成 RFID 核对，核对后才能确认上架",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun ReturnScanContent(
    uiState: ReturnScanUiState,
    scanner: ScannerState,
    onScanClick: () -> Unit,
    onManualClick: () -> Unit,
    onToolClick: (Int) -> Unit,
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    val bgColor = MaterialTheme.colorScheme.background
    val order = uiState.order
    val infoFields = listOf(
        DisplayField("上架单号", order?.orderNo.orEmpty()),
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
                title = "工单归还上架",
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
                        // === 2. 读取控制区：下一步已取消，逐条在抽屉确认 ===
                        ScanControlButtons(
                            firstButton = ScanControlButton(
                                if (scanner.isScanning) "停止扫描" else "RFID 扫描",
                                onClick = onScanClick,
                                enabled = !uiState.isLoading && !uiState.isSubmitting
                            ),
                            secondButton = ScanControlButton(
                                "人工读取",
                                onClick = onManualClick,
                                enabled = !uiState.isLoading
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

            // === 4. Tab 内容：点击卡片选中并打开单条上架抽屉 ===
            item {
                ToolsScanList(
                    tools = visibleTools,
                    key = { it.id },
                    title = { it.name },
                    lineLabel = { "行号 ${it.id.toString().padStart(2, '0')}" },
                    statusText = {
                        when {
                            it.isPutaway -> "已上架"
                            it.isRead -> "已读取"
                            else -> "未读取"
                        }
                    },
                    isRead = { it.isRead || it.isPutaway },
                    fields = { tool ->
                        listOf(
                            DisplayField("物资编码", tool.itemCode),
                            DisplayField("RFID编码", tool.rfid),
                            DisplayField("货位", tool.location),
                            DisplayField("单位", tool.unit)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(with(density) {
                            (viewportHeight - controlsAndTabsHeight).coerceAtLeast(0).toDp()
                        })
                        .nestedScroll(listScrollConnection),
                    selectedTool = uiState.selectedTool,
                    onItemClick = { tool -> onToolClick(tool.id) },
                    badge = { tool -> tool.condition.ifBlank { null } }
                )
            }
        }
    }
}
