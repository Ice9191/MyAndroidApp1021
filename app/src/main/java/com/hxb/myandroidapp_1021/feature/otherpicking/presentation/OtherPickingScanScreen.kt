package com.hxb.myandroidapp_1021.feature.otherpicking.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hxb.myandroidapp_1021.core.ui.components.FormFilter
import com.hxb.myandroidapp_1021.core.ui.components.ScanControlButton
import com.hxb.myandroidapp_1021.core.ui.components.ScanControlButtons
import com.hxb.myandroidapp_1021.core.ui.components.ScanTabs
import com.hxb.myandroidapp_1021.core.ui.components.ToolsInfoList
import com.hxb.myandroidapp_1021.core.ui.components.ToolsScanList
import com.hxb.myandroidapp_1021.core.ui.components.TopAppBar
import com.hxb.myandroidapp_1021.core.ui.components.TopInfo
import com.hxb.myandroidapp_1021.core.ui.common.DisplayField
import com.hxb.myandroidapp_1021.core.ui.common.ScanTab
import com.hxb.myandroidapp_1021.core.ui.common.ToolInfoRow

/**
 * 其他拣选扫描页；人工拣选与确认抽屉都在本页完成，提交成功后返回列表。
 */
@Composable
fun OtherPickingScanScreen(
    viewModel: OtherPickingScanViewModel,
    listViewModel: OtherPickingListViewModel,
    onBackClick: () -> Unit = {},
    onSubmittedClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showManual by rememberSaveable { mutableStateOf(false) }
    BackHandler(enabled = showManual) { showManual = false }

    if (showManual) {
        OtherPickingManualContent(
            uiState = uiState,
            onBackClick = { showManual = false },
            onHomeClick = onHomeClick,
            onConfirmToggleRead = viewModel::toggleRead
        )
    } else {
        OtherPickingScanContent(
            uiState = uiState,
            onManualClick = { showManual = true },
            onBackClick = onBackClick,
            onConfirmClick = viewModel::openConfirmDrawer,
            onHomeClick = onHomeClick
        )
    }

    if (uiState.showConfirmDrawer) {
        FormFilter(
            error = uiState.submitError,
            title = "本批次拣选，共 ${uiState.readList.size} 件",
            confirmText = if (uiState.isSubmitting) "提交中" else "确认本批次拣选",
            confirmEnabled = !uiState.isSubmitting &&
                    !uiState.submitSucceeded &&
                    uiState.readList.isNotEmpty(),
            onConfirm = viewModel::submitBatch,
            onDismiss = viewModel::dismissConfirmDrawer,
            closeRequest = uiState.submitSucceeded,
            onClosed = {
                // 抽屉收起后再回写列表并跳转，避免跳转过程中抽屉仍在收起。
                val pickingOrderNo = uiState.order?.pickingOrderNo.orEmpty()
                val rfids = uiState.readList.map { it.rfid }
                viewModel.finishSubmit()
                listViewModel.confirmPicked(pickingOrderNo, rfids)
                onSubmittedClick()
            }
        ) {
            ToolsInfoList(rows = uiState.readList.map { ToolInfoRow(it.rfid, it.description) })
        }
    }
}

/**
 * 扫描内容：顶部单据信息、读取控制区与已读取/未读取分页，下方固定高度展示明细。
 */
@Composable
fun OtherPickingScanContent(
    uiState: OtherPickingScanUiState,
    onManualClick: () -> Unit,
    onBackClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    val bgColor = MaterialTheme.colorScheme.background
    val order = uiState.order
    val infoFields = listOf(
        DisplayField("拣选单号", order?.pickingOrderNo.orEmpty()),
        DisplayField("上游类型", order?.upstreamType?.label.orEmpty()),
        DisplayField("上游单号", order?.upstreamNo.orEmpty()),
        DisplayField("车间", order?.workshop.orEmpty()),
        DisplayField("工班", order?.workGroup.orEmpty()),
        DisplayField("计划日期", order?.plannedDate.orEmpty()),
        DisplayField("待拣数量", "${uiState.tools.size}件")
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
    val isConfirmEnabled = uiState.readList.isNotEmpty() && !uiState.isSubmitting
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
                title = "其他拣选",
                onBackClick = onBackClick,
                onHomeClick = onHomeClick
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
                                "人工拣选",
                                onClick = onManualClick,
                                enabled = true
                            ),
                            thirdButton = ScanControlButton(
                                "确认",
                                onClick = onConfirmClick,
                                enabled = isConfirmEnabled
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
                    statusText = { it.scanStatusLabel },
                    isRead = { it.isRead },
                    fields = { it.displayFields() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(with(density) {
                            (viewportHeight - controlsAndTabsHeight).coerceAtLeast(0).toDp()
                        })
                        .nestedScroll(listScrollConnection),
                    shape = RectangleShape,
                    containerColor = Color.White
                )
            }
        }
    }
}
