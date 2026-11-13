package com.hxb.myandroidapp_1021.ui.otherpicking

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hxb.myandroidapp_1021.components.BottomButton
import com.hxb.myandroidapp_1021.components.ConfirmDialog
import com.hxb.myandroidapp_1021.components.ScanTopInfo
import com.hxb.myandroidapp_1021.components.ToolsInfoList
import com.hxb.myandroidapp_1021.components.TopAppBar
import com.hxb.myandroidapp_1021.ui.commonmodel.DisplayField
import com.hxb.myandroidapp_1021.ui.commonmodel.ToolInfoRow

@Composable
fun OtherPickingConfirmScreen(
    flowViewModel: OtherPickingScanViewModel,
    listViewModel: OtherPickingListViewModel,
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
    onHomeClick: () -> Unit,
    viewModel: OtherPickingConfirmViewModel = viewModel()
) {
    val state by flowViewModel.uiState.collectAsStateWithLifecycle()
    val confirmState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(confirmState.completionPending) {
        if (confirmState.completionPending) {
            // 先移除确认弹窗，等待下一帧后再更新批次并跳转。
            withFrameNanos { }
            flowViewModel.confirmBatch()
            listViewModel.updateTask(flowViewModel.uiState.value.task)
            viewModel.onCompletionHandled()
            onComplete()
        }
    }
    OtherPickingConfirmContent(
        state,
        confirmState.showDialog,
        viewModel::onDialogChange,
        onBackClick,
        onHomeClick,
        viewModel::onConfirm)
}

@Composable
fun OtherPickingConfirmContent(
    state: OtherPickingScanUiState,
    showDialog: Boolean,
    onDialogChange: (Boolean) -> Unit,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onConfirm: () -> Unit
) {
    val tools = state.task?.tools.orEmpty().filter { it.rfid in state.readRfids }
    Scaffold(
        modifier = Modifier.imePadding(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { TopAppBar(title = "其他拣选 · 本批次确认", onBackClick = onBackClick, onHomeClick = onHomeClick) },
        bottomBar = {
            BottomButton(
                "确认本批次 ${tools.size} 件",
                { onDialogChange(true) },
                tools.isNotEmpty()
            )
        }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                ScanTopInfo(
                    listOf(
                        DisplayField("来源类型", state.task?.sourceType?.label.orEmpty()),
                        DisplayField("来源单号", state.task?.sourceNo.orEmpty()),
                        DisplayField("本次核对", "${tools.size} 件"),
                        DisplayField(
                            "剩余待处理",
                            "${state.task?.tools?.count { !it.completed && it.rfid !in state.readRfids } ?: 0} 件")
                    ))
                ToolsInfoList(rows = tools.map { ToolInfoRow(rfid = it.rfid, name = it.name) })
            }
        }
    }
    if (showDialog && tools.isNotEmpty()) {
        ConfirmDialog("确认本批次", onConfirm, { onDialogChange(false) }) {
            Text("来源：${state.task?.sourceType?.label.orEmpty()}\n本次 ${tools.size} 件，是否确认操作？")
        }
    }
}

