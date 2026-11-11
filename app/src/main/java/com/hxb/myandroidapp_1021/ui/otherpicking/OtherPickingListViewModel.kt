package com.hxb.myandroidapp_1021.ui.otherpicking

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OtherPickingListViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OtherPickingListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val mockTasks = listOf(
            OtherPickingTaskUi("QT20261111001", "临时", "2026-11-11", listOf(
                OtherPickingToolUi("E280 0001 0001", "绝缘扳手", "A1-01"),
                OtherPickingToolUi("E280 0001 0002", "数字万用表", "A1-02"),
                OtherPickingToolUi("E280 0001 0003", "游标卡尺", "A1-03")
            )),
            OtherPickingTaskUi("QT20261111002", "送检", "2026-11-11", listOf(
                OtherPickingToolUi("E280 0002 0001", "绝缘扳手", "A1-01"),
                OtherPickingToolUi("E280 0002 0002", "数字万用表", "A1-02"),
                OtherPickingToolUi("E280 0002 0003", "游标卡尺", "A1-03", completed = true)
            )),
            OtherPickingTaskUi("QT20261111003", "送修", "2026-11-11", listOf(
                OtherPickingToolUi("E280 0003 0001", "绝缘扳手", "A1-01"),
                OtherPickingToolUi("E280 0003 0002", "数字万用表", "A1-02"),
                OtherPickingToolUi("E280 0003 0003", "游标卡尺", "A1-03", completed = true)
            )),
            OtherPickingTaskUi("QT20261111004", "报废", "2026-11-11", listOf(
                OtherPickingToolUi("E280 0004 0001", "绝缘扳手", "A1-01"),
                OtherPickingToolUi("E280 0004 0002", "数字万用表", "A1-02"),
                OtherPickingToolUi("E280 0004 0003", "游标卡尺", "A1-03", completed = true)
            )),
            OtherPickingTaskUi("QT20261111005", "退货", "2026-11-11", listOf(
                OtherPickingToolUi("E280 0005 0001", "绝缘扳手", "A1-01", completed = true),
                OtherPickingToolUi("E280 0005 0002", "数字万用表", "A1-02", completed = true),
                OtherPickingToolUi("E280 0005 0003", "游标卡尺", "A1-03", completed = true)
            ))
        )
        _uiState.update { it.copy(tasks = mockTasks) }
    }

    fun onQueryChange(value: String) { _uiState.update { it.copy(query = value) } }
    fun onSourceChange(value: Int) { _uiState.update { it.copy(sourceIndex = value) } }
    fun onStatusChange(value: Int) { _uiState.update { it.copy(statusIndex = value) } }

    // 只更新演示列表进度，不代表上游单据或库存已变更。
    fun updateTask(task: OtherPickingTaskUi?) {
        if (task == null) return
        _uiState.update { state ->
            state.copy(tasks = state.tasks.map { if (it.sourceNo == task.sourceNo) task else it })
        }
    }
}
