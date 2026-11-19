package com.hxb.myandroidapp_1021.feature.returns.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hxb.myandroidapp_1021.core.ui.components.FormFilter
import com.hxb.myandroidapp_1021.core.ui.components.InputFieldOne
import com.hxb.myandroidapp_1021.core.ui.components.ScanControlButton
import com.hxb.myandroidapp_1021.core.ui.components.ScanControlButtons
import com.hxb.myandroidapp_1021.core.ui.components.ScanTabs
import com.hxb.myandroidapp_1021.core.ui.components.ToolsScanList
import com.hxb.myandroidapp_1021.core.ui.components.TopAppBar
import com.hxb.myandroidapp_1021.core.ui.components.TopInfo
import com.hxb.myandroidapp_1021.core.ui.components.ToastNotification
import com.hxb.myandroidapp_1021.core.ui.theme.AppColors
import com.hxb.myandroidapp_1021.feature.returns.domain.model.ReturnDetailStatus
import com.hxb.myandroidapp_1021.commonmodel.DisplayField
import com.hxb.myandroidapp_1021.commonmodel.ScanTab

@Composable
fun ReturnScanScreen(
    viewModel: ReturnScanViewModel,
    flowViewModel: ReturnListViewModel,
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
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
            onToolClick = viewModel::selectTool,
            onNotificationDismiss = viewModel::onNotificationDismiss,
            onBackClick = onBackClick,
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
            confirmEnabled = !uiState.isSubmitting && selectedTool.canPutaway &&
                    selectedTool.isRead && uiState.draftLocation.isNotBlank(),
            onConfirm = { viewModel.confirmPutaway(flowViewModel::updateOrder) },
            onDismiss = viewModel::dismissConfirmDrawer
        ) {
            DrawerFieldRow("物资编码", selectedTool.itemCode)
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "物资描述",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    selectedTool.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            DrawerFieldRow("RFID编码", selectedTool.rfid)
            // 实际货位是明细字段，默认带出单据货位，允许按实际存放位置修改
            Text(
                "实际货位",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            InputFieldOne(
                value = uiState.draftLocation,
                onValueChange = viewModel::onDraftLocationChange,
                placeholder = "请扫描货位码",
                showSearchIcon = false,
                horizontalPadding = 0.dp
            )
        }
    }
}

@Composable
fun ReturnScanContent(
    uiState: ReturnScanUiState,
    onManualClick: () -> Unit,
    onToolClick: (Int) -> Unit,
    onNotificationDismiss: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    val bgColor = MaterialTheme.colorScheme.background
    val order = uiState.order
    val infoFields = listOf(
        DisplayField("上架单号", order?.orderNo.orEmpty()),
        DisplayField("归还单号", order?.returnOrderNo.orEmpty()),
        DisplayField("工单号", order?.workOrderNo.orEmpty()),
        DisplayField("单据状态", order?.status?.label.orEmpty()),
        DisplayField("上架人", order?.putawayBy.orEmpty().ifBlank { "-" }),
        DisplayField("上架时间", order?.putawayAt.orEmpty().ifBlank { "-" }),
        DisplayField("货位", order?.location.orEmpty().ifBlank { "-" }),
        DisplayField("工具数量", "${uiState.tools.size}件"),
        DisplayField("创建人", order?.createdBy.orEmpty()),
        DisplayField("创建时间", order?.createdAt.orEmpty()),
        DisplayField("最后更新人", order?.updatedBy.orEmpty()),
        DisplayField("最后更新时间", order?.updatedAt.orEmpty())
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
                onHomeClick = onHomeClick
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
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
                                    "RFID 扫描",
                                    onClick = {},
                                    enabled = false
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

                // === 4. Tab 内容：点击卡片选中并打开单条上架抽屉；卡片与拣选扫描页同款直角白底 ===
                item {
                    ToolsScanList(
                        tools = visibleTools,
                        key = { it.lineNo },
                        title = { it.description },
                        lineLabel = { "行号 ${it.lineNo.toString().padStart(2, '0')}" },
                        statusText = { it.status.label },
                        isRead = { it.isRead },
                        fields = { tool -> tool.displayFields() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(with(density) {
                                (viewportHeight - controlsAndTabsHeight).coerceAtLeast(0).toDp()
                            })
                            .nestedScroll(listScrollConnection),
                        shape = RectangleShape,
                        containerColor = Color.White,
                        selectedTool = uiState.selectedTool,
                        onItemClick = { tool -> onToolClick(tool.lineNo) },
                        statusContentColor = { tool ->
                            when (tool.status) {
                                ReturnDetailStatus.DAMAGED -> AppColors.DamagedText
                                ReturnDetailStatus.PENDING_INSPECTION -> AppColors.InspectionText
                                else -> null
                            }
                        },
                        statusContainerColor = { tool ->
                            when (tool.status) {
                                ReturnDetailStatus.DAMAGED -> AppColors.DamagedBackground
                                ReturnDetailStatus.PENDING_INSPECTION -> AppColors.InspectionBackground
                                else -> null
                            }
                        }
                    )
                }
            }
            ToastNotification(
                message = uiState.notificationMessage,
                notificationId = uiState.notificationId,
                onDismiss = onNotificationDismiss,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}
