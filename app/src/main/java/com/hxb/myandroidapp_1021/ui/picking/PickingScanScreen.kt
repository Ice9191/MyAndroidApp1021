package com.hxb.myandroidapp_1021.ui.picking

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
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
import com.hxb.myandroidapp_1021.components.TopAppBar
import com.hxb.myandroidapp_1021.components.ScanControlButton
import com.hxb.myandroidapp_1021.components.ScanControlButtons
import com.hxb.myandroidapp_1021.components.ScanTabs
import com.hxb.myandroidapp_1021.components.ScanTopInfo
import com.hxb.myandroidapp_1021.components.ToolsScanList
import com.hxb.myandroidapp_1021.ui.commonmodel.DisplayField
import com.hxb.myandroidapp_1021.ui.commonmodel.ScanTab

@Composable
fun PickingScanScreen(
    viewModel: PickingScanViewModel,
    onManualClick: () -> Unit,
    onBackClick: () -> Unit = {},
    onNextClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PickingScanContent(
        uiState = uiState,
        onManualClick = onManualClick,
        onBackClick = onBackClick,
        onNextClick = onNextClick,
        onSettingsClick = onSettingsClick,
        onHomeClick = onHomeClick
    )
}

@Composable
fun PickingScanContent(
    uiState: PickingScanUiState,
    onManualClick: () -> Unit,
    onBackClick: () -> Unit = {},
    onNextClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    val bgColor = MaterialTheme.colorScheme.background
    val order = uiState.order
    val infoFields = listOf(
        DisplayField("拣选单号", order?.orderNo.orEmpty()),
        DisplayField("单据状态", order?.status?.label.orEmpty()),
        DisplayField("工单号", order?.workOrderNo.orEmpty()),
        DisplayField("归属车间", order?.workshop.orEmpty()),
        DisplayField("借用人", order?.borrower.orEmpty()),
        DisplayField("工具数量", "${uiState.tools.size}件"),
        DisplayField("计划日期", order?.plannedDate.orEmpty()),
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
                title = "工单拣选",
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
                    ScanTopInfo(fields = infoFields)
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
                                "人工拣选",
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
                            DisplayField("推荐货位", tool.location),
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
