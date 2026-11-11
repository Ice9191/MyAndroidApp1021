package com.hxb.myandroidapp_1021.ui.otherpicking

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hxb.myandroidapp_1021.components.BottomButton
import com.hxb.myandroidapp_1021.components.ConfirmDialog
import com.hxb.myandroidapp_1021.components.ScanTopInfo
import com.hxb.myandroidapp_1021.components.TaskPage
import com.hxb.myandroidapp_1021.components.ToolsInfoList
import com.hxb.myandroidapp_1021.ui.common.DisplayField
import com.hxb.myandroidapp_1021.ui.common.ToolInfoRow

@Composable
fun OtherPickingConfirmScreen(
    flowViewModel: OtherPickingScanViewModel,
    listViewModel: OtherPickingListViewModel,
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
    viewModel: OtherPickingConfirmViewModel = viewModel()
) {
    val state by flowViewModel.uiState.collectAsStateWithLifecycle()
    val confirmState by viewModel.uiState.collectAsStateWithLifecycle()
    OtherPickingConfirmContent(state, confirmState.showDialog, viewModel::onDialogChange, onBackClick, {
        viewModel.onDialogChange(false)
        flowViewModel.confirmBatch()
        listViewModel.updateTask(flowViewModel.uiState.value.task)
        onComplete()
    })
}

@Composable
fun OtherPickingConfirmContent(
    state: OtherPickingScanUiState,
    showDialog: Boolean,
    onDialogChange: (Boolean) -> Unit,
    onBackClick: () -> Unit,
    onConfirm: () -> Unit
) {
    val tools = state.task?.tools.orEmpty().filter { it.rfid in state.readRfids }
    TaskPage("其他拣选 · 本批次确认", onBackClick, bottomBar = {
        BottomButton("确认本批次 ${tools.size} 件", { onDialogChange(true) }, tools.isNotEmpty())
    }) {
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            ScanTopInfo(listOf(
                DisplayField("来源类型", state.task?.sourceType.orEmpty()),
                DisplayField("来源单号", state.task?.sourceNo.orEmpty()),
                DisplayField("本次核对", "${tools.size} 件"),
                DisplayField("剩余待处理", "${state.task?.tools?.count { !it.completed && it.rfid !in state.readRfids } ?: 0} 件")
            ))
            Text("请核对本批次实物及任务来源。", Modifier.padding(16.dp))
            // 文档要求按来源单据执行，不能统一当成借用交接并要求领料人工号。
            Text("页面演示：本次确认只更新演示进度，不执行出库或库存核减。",
                Modifier.padding(horizontal = 16.dp, vertical = 8.dp), color = Color.Gray)
            ToolsInfoList(rows = tools.map { ToolInfoRow(rfid = it.rfid, name = it.name) })
        }
    }
    if (showDialog && tools.isNotEmpty()) {
        ConfirmDialog("确认本批次", onConfirm, { onDialogChange(false) }) {
            Text("来源：${state.task?.sourceType}\n本次 ${tools.size} 件，是否确认演示操作？")
        }
    }
}

