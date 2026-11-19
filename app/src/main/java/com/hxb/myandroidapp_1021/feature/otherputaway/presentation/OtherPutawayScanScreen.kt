package com.hxb.myandroidapp_1021.feature.otherputaway.presentation

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
import com.hxb.myandroidapp_1021.core.ui.components.ToastNotification
import com.hxb.myandroidapp_1021.core.ui.components.ToolsScanList
import com.hxb.myandroidapp_1021.core.ui.components.TopAppBar
import com.hxb.myandroidapp_1021.core.ui.components.TopInfo
import com.hxb.myandroidapp_1021.commonmodel.DisplayField
import com.hxb.myandroidapp_1021.commonmodel.ScanTab

/**
 * 其他上架扫描页；人工读取与单条上架抽屉都在本页完成，确认成功后留在本页继续上架。
 */
@Composable
fun OtherPutawayScanScreen(
    viewModel: OtherPutawayScanViewModel,
    listViewModel: OtherPutawayListViewModel,
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showManual by rememberSaveable { mutableStateOf(false) }
    BackHandler(enabled = showManual) { showManual = false }

    if (showManual) {
        OtherPutawayManualContent(
            uiState = uiState,
            onBackClick = { showManual = false },
            onHomeClick = onHomeClick,
            onConfirmToggleRead = viewModel::toggleRead
        )
    } else {
        OtherPutawayScanContent(
            uiState = uiState,
            onManualClick = { showManual = true },
            onToolClick = viewModel::selectTool,
            onNotificationDismiss = viewModel::onNotificationDismiss,
            onBackClick = onBackClick,
            onHomeClick = onHomeClick
        )
    }

    val selectedTool = uiState.selectedTool
    if (uiState.showConfirmDrawer && selectedTool != null) {
        FormFilter(
            error = uiState.submitError,
            title = "上架确认",
            resetText = "取消",
            onReset = viewModel::dismissConfirmDrawer,
            confirmText = if (uiState.isSubmitting) "提交中" else "确认上架",
            confirmEnabled = !uiState.isSubmitting &&
                    selectedTool.isRead &&
                    uiState.draftLocation.isNotBlank(),
            onConfirm = { viewModel.confirmPutaway(listViewModel::updateOrder) },
            onDismiss = viewModel::dismissConfirmDrawer
        ) {
            PutawayDrawerFieldRow("物资编码", selectedTool.itemCode)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "物资描述",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = selectedTool.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            PutawayDrawerFieldRow("RFID编码", selectedTool.rfid)
            Text(
                text = "实际货位",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            // 实际货位不预填系统推荐值，必须扫描或输入后确认。
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

/**
 * 扫描内容：顶部单据信息、读取控制区与已读取/未读取分页，下方固定高度展示明细。
 */
@Composable
fun OtherPutawayScanContent(
    uiState: OtherPutawayScanUiState,
    onManualClick: () -> Unit,
    onToolClick: (Int) -> Unit,
    onNotificationDismiss: () -> Unit,
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    val bgColor = MaterialTheme.colorScheme.background
    val order = uiState.order
    val infoFields = listOf(
        DisplayField("上架单号", order?.putawayOrderNo.orEmpty()),
        DisplayField("上游类型", order?.upstreamType?.label.orEmpty()),
        DisplayField("上游单号", order?.upstreamNo.orEmpty()),
        DisplayField("创建时间", order?.createdAt.orEmpty()),
        DisplayField("工具数量", "${uiState.tools.size}件")
    )
    var selectedTab by rememberSaveable { mutableStateOf(ScanTab.All) }
    var viewportHeight by remember { mutableIntStateOf(0) }
    var controlsAndTabsHeight by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current

    // 明细列表放在外层 LazyColumn 的单个 item 内，需要由它自己消费滚动，避免与外层争抢。
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
                title = "其他上架",
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
                        TopInfo(fields = infoFields)
                        HorizontalDivider(thickness = 8.dp, color = bgColor)

                        Column(
                            modifier = Modifier.onSizeChanged { controlsAndTabsHeight = it.height }
                        ) {
                            ScanControlButtons(
                                firstButton = ScanControlButton(
                                    "RFID 扫描",
                                    onClick = {},
                                    enabled = false
                                ),
                                secondButton = ScanControlButton(
                                    "人工读取",
                                    onClick = onManualClick,
                                    enabled = true
                                )
                            )
                            HorizontalDivider(thickness = 8.dp, color = bgColor)

                            ScanTabs(
                                labels = tabLabels,
                                selectedIndex = selectedTab.ordinal,
                                onTabSelected = { selectedTab = ScanTab.entries[it] }
                            )
                        }
                    }
                }

                item {
                    ToolsScanList(
                        tools = visibleTools,
                        key = { it.lineNo },
                        title = { it.cardTitle },
                        lineLabel = { it.lineLabel },
                        statusText = { it.statusLabel },
                        isRead = { it.isRead },
                        fields = { it.displayFields() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(with(density) {
                                (viewportHeight - controlsAndTabsHeight).coerceAtLeast(0).toDp()
                            })
                            .nestedScroll(listScrollConnection),
                        shape = RectangleShape,
                        containerColor = Color.White,
                        selectedTool = uiState.selectedTool,
                        onItemClick = { onToolClick(it.lineNo) }
                    )
                }
            }

            // 顶部提示条：上架成功后显示结果。
            ToastNotification(
                message = uiState.notificationMessage,
                notificationId = uiState.notificationId,
                onDismiss = onNotificationDismiss,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

/**
 * 上架抽屉内的只读字段行：标签在左、值在右。
 */
@Composable
private fun PutawayDrawerFieldRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}
