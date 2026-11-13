package com.hxb.myandroidapp_1021.ui.otherputaway

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hxb.myandroidapp_1021.components.InputFieldOne
import com.hxb.myandroidapp_1021.components.OrderList
import com.hxb.myandroidapp_1021.components.OrderProgress
import com.hxb.myandroidapp_1021.components.ScanTabs
import com.hxb.myandroidapp_1021.components.TopAppBar
import com.hxb.myandroidapp_1021.ui.commonmodel.DisplayField

@Composable
fun OtherPutawayListScreen(
    viewModel: OtherPutawayListViewModel,
    flowViewModel: OtherPutawayScanViewModel,
    onBackClick: () -> Unit,
    onStartClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    OtherPutawayListContent(
        state, viewModel::onQueryChange, viewModel::onSourceChange,
        viewModel::onStatusChange, onBackClick, {
            flowViewModel.selectTask(it)
            onStartClick()
        })
}

@Composable
fun OtherPutawayListContent(
    state: OtherPutawayListUiState,
    onQueryChange: (String) -> Unit,
    onSourceChange: (Int) -> Unit,
    onStatusChange: (Int) -> Unit,
    onBackClick: () -> Unit,
    onStartClick: (OtherPutawayTaskUi) -> Unit
) {
    Scaffold(
        modifier = Modifier.imePadding(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { TopAppBar(title = "其他上架", onBackClick = onBackClick) }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            InputFieldOne(state.query, onQueryChange, "请输入来源单号")
            OrderList(
                orders = state.visibleTasks,
                listState = rememberLazyListState(),
                emptyText = "暂无匹配的上架任务",
                title = { it.sourceNo },
                statusText = { it.status.label },
                fields = {
                    listOf(
                        DisplayField("来源类型", it.sourceType.label),
                        DisplayField("任务日期", it.date)
                    )
                },
                progress = { OrderProgress("上架进度", it.completedCount, it.tools.size) },
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
}

