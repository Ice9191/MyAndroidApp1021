package com.hxb.myandroidapp_1021.ui.otherpicking

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hxb.myandroidapp_1021.components.InputFieldOne
import com.hxb.myandroidapp_1021.components.OrderList
import com.hxb.myandroidapp_1021.components.OrderProgress
import com.hxb.myandroidapp_1021.components.ScanTabs
import com.hxb.myandroidapp_1021.components.TaskPage
import com.hxb.myandroidapp_1021.ui.common.DisplayField

@Composable
fun OtherPickingListScreen(
    viewModel: OtherPickingListViewModel,
    flowViewModel: OtherPickingScanViewModel,
    onBackClick: () -> Unit,
    onStartClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    OtherPickingListContent(state, viewModel::onQueryChange, viewModel::onSourceChange,
        viewModel::onStatusChange, onBackClick, {
            flowViewModel.selectTask(it)
            onStartClick()
        })
}

@Composable
fun OtherPickingListContent(
    state: OtherPickingListUiState,
    onQueryChange: (String) -> Unit,
    onSourceChange: (Int) -> Unit,
    onStatusChange: (Int) -> Unit,
    onBackClick: () -> Unit,
    onStartClick: (OtherPickingTaskUi) -> Unit
) {
    TaskPage("其他拣选", onBackClick) {
        InputFieldOne(state.query, onQueryChange, "请输入来源单号")
        // 文档功能清单要求一体机创建上游单据，PDA 此处只选择执行任务。
        OrderList(
            orders = state.visibleTasks,
            listState = rememberLazyListState(),
            emptyText = "暂无匹配的拣选任务",
            title = { "${it.sourceType} · ${it.sourceNo}" },
            statusText = { it.status },
            fields = { listOf(DisplayField("来源类型", it.sourceType), DisplayField("任务日期", it.date)) },
            progress = { OrderProgress("拣选进度", it.completedCount, it.tools.size) },
            actionText = "查看任务",
            onActionClick = onStartClick,
            filterOptions = state.sourceOptions,
            selectedFilterIndex = state.sourceIndex,
            onFilterChange = onSourceChange,
            header = {
                ScanTabs(state.statusOptions, state.statusIndex, onStatusChange)
            },
            modifier = Modifier.weight(1f)
        )
    }
}

