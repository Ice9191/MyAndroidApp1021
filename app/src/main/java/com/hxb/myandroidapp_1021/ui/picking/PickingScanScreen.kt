package com.hxb.myandroidapp_1021.ui.picking

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hxb.myandroidapp_1021.components.ScanControlButton
import com.hxb.myandroidapp_1021.components.ScanControlButtons
import com.hxb.myandroidapp_1021.components.ScanTabs
import com.hxb.myandroidapp_1021.components.ScanTopInfo
import com.hxb.myandroidapp_1021.components.ToolsScanList
import com.hxb.myandroidapp_1021.data.models.DisplayField
import com.hxb.myandroidapp_1021.data.models.ScanTab

@Composable
fun PickingScanScreen(
    viewModel: PickingScanViewModel = viewModel(),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickingScanContent(
    uiState: PickingScanUiState,
    onManualClick: () -> Unit,
    onBackClick: () -> Unit = {},
    onNextClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    val themeBlue = Color(0xFF1E71C8)
    val bgColor = Color(0xFFF5F6F8)
    // UI 展示的字段列表，根据 state 动态生成
    val mockFields = remember(uiState) {
        listOf(
            DisplayField("拣选单号", uiState.orderNo),
            DisplayField("单据状态", uiState.orderStatus.label),
            DisplayField("工单号", "GD20231028088"),
            DisplayField("归属车间", uiState.workshop),
            DisplayField("借用人", uiState.borrower),
            DisplayField("工具数量", "${uiState.tools.size}件"),
            DisplayField("计划日期", "2023-10-30 09:30"),
            DisplayField("创建人", "李四"),
            DisplayField("创建时间", "2023-10-28 10:00")
        )
    }
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
    val isNextEnabled = uiState.readList.isNotEmpty() && uiState.unreadList.isEmpty()
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
                modifier = Modifier.height(75.dp),
                title = {
                    Text(
                        text = "借出·拣选",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.offset(x = (-16).dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.offset(x = (-7).dp)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSettingsClick, modifier = Modifier.offset(x = (-4).dp)) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Setting",
                            tint = Color.White
                        )
                    }

                    IconButton(onClick = onHomeClick, modifier = Modifier.offset(x = (-4).dp)) {
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
                    ScanTopInfo(fields = mockFields)
                    HorizontalDivider(thickness = 8.dp, color = bgColor)

                    Column(
                        modifier = Modifier.onSizeChanged { controlsAndTabsHeight = it.height }
                    ) {
                        // === 2. 读取控制区 ===
                        ScanControlButtons(
                            firstButton = ScanControlButton("RFID 待开放", onClick = {}, enabled = false),
                            secondButton = ScanControlButton("人工拣选", onClick = onManualClick),
                            thirdButton = ScanControlButton("下一步", onClick = onNextClick, enabled = isNextEnabled)
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
                    statusText = { it.status.label },
                    isRead = { it.status == ToolScanItemStatus.PICKED },
                    fields = { tool ->
                        listOf(
                            DisplayField("物资编码", tool.itemCode),
                            DisplayField("RFID编码", tool.rfid),
                            DisplayField("推荐货位", tool.recommendedLocation),
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
