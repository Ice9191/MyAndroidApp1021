package com.hxb.myandroidapp_1021.ui.otherputaway

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
import com.hxb.myandroidapp_1021.components.ConfirmDialog
import com.hxb.myandroidapp_1021.components.InputFieldOne
import com.hxb.myandroidapp_1021.components.ScanControlButton
import com.hxb.myandroidapp_1021.components.ScanControlButtons
import com.hxb.myandroidapp_1021.components.ScanTabs
import com.hxb.myandroidapp_1021.components.ScanTopInfo
import com.hxb.myandroidapp_1021.components.TaskPage
import com.hxb.myandroidapp_1021.components.ToolsScanList
import com.hxb.myandroidapp_1021.ui.common.DisplayField

@Composable
fun OtherPutawayScreen(
    viewModel: OtherPutawayViewModel,
    listViewModel: OtherPutawayListViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    OtherPutawayContent(
        state, viewModel::onRfidChange, viewModel::onLocationChange,
        viewModel::onTabSelected, viewModel::identifyTool, viewModel::onDialogChange, {
            viewModel.confirmItem()
            listViewModel.updateTask(viewModel.uiState.value.task)
        }, onBackClick
    )
}

@Composable
fun OtherPutawayContent(
    state: OtherPutawayUiState,
    onRfidChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onTabSelected: (Int) -> Unit,
    onIdentify: () -> Unit,
    onDialogChange: (Boolean) -> Unit,
    onConfirm: () -> Unit,
    onBackClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val tools = state.task?.tools.orEmpty()
    val visible = when (state.selectedTab) {
        1 -> tools.filterNot { it.completed }
        2 -> tools.filter { it.completed }
        else -> tools
    }
    TaskPage("其他上架 · 工具归位", onBackClick, bottomBar = {
        BottomButton(
            "确认本件上架",
            { focusManager.clearFocus(); onDialogChange(true) },
            state.canConfirm
        )
    }) {
        ScanTopInfo(
            listOf(
                DisplayField(
                    "归还来源",
                    "${state.task?.sourceType?.label.orEmpty()} · ${state.task?.sourceNo.orEmpty()}"
                ),
                DisplayField(
                    "上架进度",
                    "已完成 ${state.task?.completedCount ?: 0} / 共 ${tools.size} 件"
                )
            )
        )
        InputFieldOne(state.rfid, onRfidChange, "1. 扫描或输入 RFID", showSearchIcon = false)
        ScanControlButtons(
            ScanControlButton("RFID 待接入", {}, false),
            ScanControlButton(
                "识别工具",
                { focusManager.clearFocus(); onIdentify() },
                state.rfid.isNotBlank()
            ),
            ScanControlButton("重新识别", { onRfidChange("") }, state.rfid.isNotBlank())
        )
        state.current?.let {
            ScanTopInfo(
                listOf(
                    DisplayField("当前工具", it.name),
                    DisplayField("原货位", it.location)
                )
            )
        }
        InputFieldOne(
            state.location, onLocationChange, "2. 扫描或输入实际货位码",
            showSearchIcon = false, enabled = state.current?.completed == false
        )
        Text(
            state.message ?: "页面演示：核对工具及实际货位后逐件确认。",
            Modifier.padding(horizontal = 16.dp, vertical = 8.dp), color = Color(0xFF1E71C8)
        )
        ScanTabs(
            listOf(
                "全部 ${tools.size}", "待上架 ${tools.count { !it.completed }}",
                "已上架 ${state.task?.completedCount ?: 0}"
            ), state.selectedTab, onTabSelected
        )
        ToolsScanList(
            tools = visible, key = { it.rfid }, title = { it.name }, lineLabel = { it.rfid },
            statusText = { if (it.completed) "已上架" else "待上架" }, isRead = { it.completed },
            fields = {
                listOf(
                    DisplayField("RFID", it.rfid),
                    DisplayField(if (it.completed) "实际货位" else "原货位", it.location)
                )
            },
            modifier = Modifier.weight(1f)
        )
    }
    if (state.showDialog && state.canConfirm) {
        ConfirmDialog("确认本件上架", onConfirm, { onDialogChange(false) }) {
            Text("工具：${state.current?.name}\nRFID：${state.identifiedRfid}\n实际货位：${state.location.trim()}\n仅演示操作，不更新真实库存。")
        }
    }
}

