package com.hxb.myandroidapp_1021.ui.otherputaway

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OtherPutawayListViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OtherPutawayListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val mockTasks = listOf(
            OtherPutawayTaskUi(
                "QT20261111001", SourceType.TEMPORARY, "2026-11-11", listOf(
                    OtherPutawayToolUi("E280 0001 0001", "绝缘扳手", "A1-01"),
                    OtherPutawayToolUi("E280 0001 0002", "数字万用表", "A1-02"),
                    OtherPutawayToolUi("E280 0001 0003", "游标卡尺", "A1-03")
                )
            ),
            OtherPutawayTaskUi(
                "QT20261111002", SourceType.INSPECTION, "2026-11-11", listOf(
                    OtherPutawayToolUi("E280 0002 0001", "绝缘扳手", "A1-01"),
                    OtherPutawayToolUi("E280 0002 0002", "数字万用表", "A1-02"),
                    OtherPutawayToolUi("E280 0002 0003", "游标卡尺", "A1-03", completed = true)
                )
            ),
            OtherPutawayTaskUi(
                "QT20261111003", SourceType.REPAIR, "2026-11-11", listOf(
                    OtherPutawayToolUi("E280 0003 0001", "绝缘扳手", "A1-01"),
                    OtherPutawayToolUi("E280 0003 0002", "数字万用表", "A1-02"),
                    OtherPutawayToolUi("E280 0003 0003", "游标卡尺", "A1-03", completed = true)
                )
            ),
            OtherPutawayTaskUi(
                "QT20261111004", SourceType.SCRAP, "2026-11-11", listOf(
                    OtherPutawayToolUi("E280 0004 0001", "绝缘扳手", "A1-01"),
                    OtherPutawayToolUi("E280 0004 0002", "数字万用表", "A1-02"),
                    OtherPutawayToolUi("E280 0004 0003", "游标卡尺", "A1-03", completed = true)
                )
            ),
            OtherPutawayTaskUi(
                "QT20261111005", SourceType.RETURN, "2026-11-11", listOf(
                    OtherPutawayToolUi("E280 0005 0001", "绝缘扳手", "A1-01", completed = true),
                    OtherPutawayToolUi("E280 0005 0002", "数字万用表", "A1-02", completed = true),
                    OtherPutawayToolUi("E280 0005 0003", "游标卡尺", "A1-03", completed = true)
                )
            )
        )
        _uiState.update { it.copy(tasks = mockTasks) }
    }

    fun onQueryChange(value: String) {
        _uiState.update { it.copy(query = value) }
    }

    fun onSourceChange(value: Int) {
        _uiState.update { it.copy(selectedSource = SourceType.entries.getOrNull(value - 1)) }
    }

    fun onStatusChange(value: Int) {
        _uiState.update { it.copy(selectedStatus = PutawayStatus.entries.getOrNull(value - 1)) }
    }

    // 只更新演示列表进度，不代表上游单据或库存已变更。
    fun updateTask(task: OtherPutawayTaskUi?) {
        if (task == null) return
        _uiState.update { state ->
            state.copy(tasks = state.tasks.map { if (it.sourceNo == task.sourceNo) task else it })
        }
    }
}
