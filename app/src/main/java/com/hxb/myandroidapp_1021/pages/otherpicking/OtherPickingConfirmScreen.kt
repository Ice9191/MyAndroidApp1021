package com.hxb.myandroidapp_1021.pages.otherpicking

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
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hxb.myandroidapp_1021.components.BottomButton
import com.hxb.myandroidapp_1021.components.ConfirmDialog
import com.hxb.myandroidapp_1021.components.DataPage
import com.hxb.myandroidapp_1021.components.ToolsInfoList
import com.hxb.myandroidapp_1021.components.TopAppBar
import com.hxb.myandroidapp_1021.components.TopInfo
import com.hxb.myandroidapp_1021.pages.commonmodel.DisplayField
import com.hxb.myandroidapp_1021.pages.commonmodel.ToolInfoRow


@Composable
fun OtherPickingConfirmScreen(
    flowViewModel: OtherPickingScanViewModel,
    listViewModel: OtherPickingListViewModel,
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
    onHomeClick: () -> Unit,
    viewModel: OtherPickingConfirmViewModel = hiltViewModel()
) {
    val state by flowViewModel.uiState.collectAsStateWithLifecycle()
    val confirmState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(confirmState.completionPending) {
        if (confirmState.completionPending) {
            // 先关闭弹窗，等待服务端确认成功后再更新批次并跳转。
            viewModel.onCompletionHandled()
            flowViewModel.confirmBatch { result ->
                listViewModel.updateTask(result)
                onComplete()
            }
        }
    }
    DataPage(
        state.isLoading || state.isSubmitting,
        state.fromCache,
        state.errorMessage
    ) {
        OtherPickingConfirmContent(
            state,
            confirmState.showDialog,
            viewModel::onDialogChange,
            onBackClick,
            onHomeClick,
            viewModel::onConfirm
        )
    }
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
        topBar = {
            TopAppBar(
                title = "其他拣选",
                onBackClick = onBackClick,
                onHomeClick = onHomeClick
            )
        },
        bottomBar = {
            BottomButton(
                if (state.isSubmitting) "提交中" else "确认本批次 ${tools.size} 件",
                { onDialogChange(true) },
                tools.isNotEmpty() && !state.isSubmitting
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
                TopInfo(
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
