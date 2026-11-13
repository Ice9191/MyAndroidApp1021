package com.hxb.myandroidapp_1021.ui.otherpicking

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hxb.myandroidapp_1021.components.BottomButton
import com.hxb.myandroidapp_1021.components.InputFieldOne
import com.hxb.myandroidapp_1021.components.ScanControlButton
import com.hxb.myandroidapp_1021.components.ScanControlButtons
import com.hxb.myandroidapp_1021.components.ScanTabs
import com.hxb.myandroidapp_1021.components.ScanTopInfo
import com.hxb.myandroidapp_1021.components.ToolsScanList
import com.hxb.myandroidapp_1021.components.TopAppBar
import com.hxb.myandroidapp_1021.components.TopNotification
import com.hxb.myandroidapp_1021.ui.commonmodel.DisplayField

@Composable
fun OtherPickingScanScreen(
    viewModel: OtherPickingScanViewModel,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
    onHomeClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    OtherPickingScanContent(
        state, viewModel::onRfidChange, viewModel::onTabSelected,
        viewModel::identifyTool, viewModel::clearReads, onBackClick, onNextClick, onHomeClick,
        viewModel::dismissNotification
    )
}

@Composable
fun OtherPickingScanContent(
    state: OtherPickingScanUiState,
    onRfidChange: (String) -> Unit,
    onTabSelected: (Int) -> Unit,
    onIdentify: () -> Unit,
    onClear: () -> Unit,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
    onHomeClick: () -> Unit,
    onNotificationDismiss: () -> Unit
) {
    val bgColor = MaterialTheme.colorScheme.background
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val dismissInput: () -> Unit = {
        focusManager.clearFocus()
        keyboardController?.hide()
    }
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
    val tools = state.task?.tools.orEmpty()
    val pending = tools.filterNot { it.completed }
    val visible = when (state.selectedTab) {
        1 -> pending.filter { it.rfid in state.readRfids }
        2 -> pending.filterNot { it.rfid in state.readRfids }
        else -> tools
    }
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = "其他拣选 · 核对工具",
                modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { dismissInput() }) },
                onBackClick = { dismissInput(); onBackClick() },
                onHomeClick = { dismissInput(); onHomeClick() }
            )
        },
        bottomBar = {
            BottomButton(
                "下一步",
                { dismissInput(); onNextClick() },
                state.readRfids.isNotEmpty(),
                modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { dismissInput() }) }
            )
        }
    ) { paddingValues ->
        Box(Modifier.fillMaxSize().padding(paddingValues).clipToBounds()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged { viewportHeight = it.height }
                    .pointerInput(Unit) { detectTapGestures(onTap = { dismissInput() }) }
            ) {
                item {
                    Column {
                        ScanTopInfo(
                            listOf(
                                DisplayField(
                                    "来源",
                                    "${state.task?.sourceType?.label.orEmpty()} · ${state.task?.sourceNo.orEmpty()}"
                                )
                            )
                        )
                        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                            Text("拣选进度 ${state.task?.completedCount ?: 0} / ${tools.size} 件")
                            LinearProgressIndicator(
                                progress = {
                                    if (tools.isEmpty()) 0f else
                                        ((state.task?.completedCount ?: 0).toFloat() / tools.size).coerceIn(0f, 1f)
                                },
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                            )
                        }

                        HorizontalDivider(thickness = 8.dp, color = bgColor)
                        InputFieldOne(
                            state.rfid,
                            onRfidChange,
                            "扫描或输入 RFID",
                            showSearchIcon = false
                        )
                        HorizontalDivider(thickness = 8.dp, color = bgColor)
                        Column(Modifier.onSizeChanged { controlsAndTabsHeight = it.height }) {

                            ScanControlButtons(
                                ScanControlButton("RFID 读取", {}, false),
                                ScanControlButton(
                                    "核对 RFID",
                                    { dismissInput(); onIdentify() },
                                    state.rfid.isNotBlank()
                                ),
                                ScanControlButton(
                                    "清空本次",
                                    { dismissInput(); onClear() },
                                    state.readRfids.isNotEmpty()
                                )
                            )
                            state.message?.let {
                                Text(
                                    it,
                                    Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            HorizontalDivider(thickness = 8.dp, color = bgColor)
                            ScanTabs(
                                listOf(
                                    "全部 ${tools.size}", "已读取 ${state.readRfids.size}",
                                    "待读取 ${pending.size - state.readRfids.size}"
                                ), state.selectedTab, { dismissInput(); onTabSelected(it) }
                            )
                        }
                    }
                }
                item {
                    ToolsScanList(
                        tools = visible,
                        key = { it.rfid },
                        title = { it.name },
                        lineLabel = { it.rfid },
                        statusText = { if (it.completed) "已完成" else if (it.rfid in state.readRfids) "已读取" else "待读取" },
                        isRead = { it.completed || it.rfid in state.readRfids },
                        fields = {
                            listOf(
                                DisplayField("RFID", it.rfid),
                                DisplayField("所在货位", it.location)
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
            TopNotification(
                message = state.notificationMessage,
                notificationId = state.notificationId,
                onDismiss = onNotificationDismiss
            )
        }
    }
}

