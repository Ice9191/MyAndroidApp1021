package com.hxb.myandroidapp_1021.ui.otherpicking

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hxb.myandroidapp_1021.components.BottomButton
import com.hxb.myandroidapp_1021.components.InputFieldOne
import com.hxb.myandroidapp_1021.components.ScanControlButton
import com.hxb.myandroidapp_1021.components.ScanControlButtons
import com.hxb.myandroidapp_1021.components.ScanTabs
import com.hxb.myandroidapp_1021.components.ScanTopInfo
import com.hxb.myandroidapp_1021.components.TaskPage
import com.hxb.myandroidapp_1021.components.ToolsScanList
import com.hxb.myandroidapp_1021.ui.common.DisplayField

@Composable
fun OtherPickingScanScreen(
    viewModel: OtherPickingScanViewModel,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    OtherPickingScanContent(state, viewModel::onRfidChange, viewModel::onTabSelected,
        viewModel::identifyTool, viewModel::clearReads, onBackClick, onNextClick)
}

@Composable
fun OtherPickingScanContent(
    state: OtherPickingScanUiState,
    onRfidChange: (String) -> Unit,
    onTabSelected: (Int) -> Unit,
    onIdentify: () -> Unit,
    onClear: () -> Unit,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val tools = state.task?.tools.orEmpty()
    val pending = tools.filterNot { it.completed }
    val visible = when (state.selectedTab) {
        1 -> pending.filter { it.rfid in state.readRfids }
        2 -> pending.filterNot { it.rfid in state.readRfids }
        else -> tools
    }
    TaskPage("其他拣选 · 核对工具", onBackClick, bottomBar = {
        BottomButton("确认本批次 ${state.readRfids.size} 件", onNextClick, state.readRfids.isNotEmpty())
    }) {
        ScanTopInfo(listOf(
            DisplayField("来源", "${state.task?.sourceType.orEmpty()} · ${state.task?.sourceNo.orEmpty()}"),
            DisplayField("拣选进度", "已完成 ${state.task?.completedCount ?: 0} / 共 ${tools.size} 件")
        ))
        Text("按任务中的 RFID 核对工具，读取后确认本批次。", Modifier.padding(16.dp), color = Color.Gray)
        InputFieldOne(state.rfid, onRfidChange, "扫描或输入 RFID", showSearchIcon = false)
        ScanControlButtons(
            ScanControlButton("RFID 待接入", {}, false),
            ScanControlButton("核对 RFID", { focusManager.clearFocus(); onIdentify() }, state.rfid.isNotBlank()),
            ScanControlButton("清空本次", onClear, state.readRfids.isNotEmpty())
        )
        state.message?.let { Text(it, Modifier.padding(horizontal = 16.dp, vertical = 4.dp), color = Color(0xFF1E71C8)) }
        ScanTabs(listOf("全部 ${tools.size}", "本次已读 ${state.readRfids.size}",
            "待读取 ${pending.size - state.readRfids.size}"), state.selectedTab, onTabSelected)
        ToolsScanList(
            tools = visible, key = { it.rfid }, title = { it.name }, lineLabel = { it.rfid },
            statusText = { if (it.completed) "已完成" else if (it.rfid in state.readRfids) "本次已读取" else "待读取" },
            isRead = { it.completed || it.rfid in state.readRfids },
            fields = { listOf(DisplayField("RFID", it.rfid), DisplayField("所在货位", it.location)) },
            modifier = Modifier.weight(1f)
        )
    }
}

