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
            OtherPickingTaskUi(
                "QT20261111001", SourceType.TEMPORARY, "2026-11-11", listOf(
                    OtherPickingToolUi("1", "绝缘扳手", "A1-01"),
                    OtherPickingToolUi("E280 0001 0002", "数字万用表", "A1-02"),
                    OtherPickingToolUi("E280 0001 0003", "游标卡尺", "A1-03")
                )
            ),
            OtherPickingTaskUi(
                "QT20261111002", SourceType.INSPECTION, "2026-11-11", listOf(
                    OtherPickingToolUi("E280 0002 0001", "绝缘扳手", "A1-01"),
                    OtherPickingToolUi("E280 0002 0002", "数字万用表", "A1-02"),
                    OtherPickingToolUi("E280 0002 0003", "游标卡尺", "A1-03", completed = true)
                )
            ),
            OtherPickingTaskUi(
                "QT20261111003", SourceType.REPAIR, "2026-11-11", listOf(
                    OtherPickingToolUi("E280 0003 0001", "绝缘扳手", "A1-01"),
                    OtherPickingToolUi("E280 0003 0002", "数字万用表", "A1-02"),
                    OtherPickingToolUi("E280 0003 0003", "游标卡尺", "A1-03", completed = true)
                )
            ),
            OtherPickingTaskUi(
                "QT20261111004", SourceType.SCRAP, "2026-11-11", listOf(
                    OtherPickingToolUi("E280 0004 0001", "绝缘扳手", "A1-01"),
                    OtherPickingToolUi("E280 0004 0002", "数字万用表", "A1-02"),
                    OtherPickingToolUi("E280 0004 0003", "游标卡尺", "A1-03", completed = true)
                )
            ),
            OtherPickingTaskUi(
                "QT20261111005", SourceType.RETURN, "2026-11-11", listOf(
                    OtherPickingToolUi("E280 0005 0001", "绝缘扳手", "A1-01", completed = true),
                    OtherPickingToolUi("E280 0005 0002", "数字万用表", "A1-02", completed = true),
                    OtherPickingToolUi("E280 0005 0003", "游标卡尺", "A1-03", completed = true)
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
        _uiState.update { it.copy(selectedStatus = PickingStatus.entries.getOrNull(value - 1)) }
    }

    fun updateTask(task: OtherPickingTaskUi?) {
        if (task == null) return
        _uiState.update { state ->
            state.copy(tasks = state.tasks.map { if (it.sourceNo == task.sourceNo) task else it })
        }
    }

    /**
     * 接收临时借出单的拣选任务；重复进入时保留已经完成的器具，不重复生成任务。
     */
    fun upsertTemporaryTask(task: OtherPickingTaskUi) {
        _uiState.update { state ->
            val existing = state.tasks.firstOrNull { it.sourceNo == task.sourceNo }
            val merged = task.copy(tools = task.tools.map { tool ->
                existing?.tools?.firstOrNull { it.rfid == tool.rfid } ?: tool
            }, closed = existing?.closed ?: false)
            state.copy(
                tasks = state.tasks.filterNot { it.sourceNo == task.sourceNo } + merged
            )
        }
    }

    fun openTemporaryTask(task: OtherPickingTaskUi) {
        upsertTemporaryTask(task)
        _uiState.update {
            it.copy(
                query = task.sourceNo,
                selectedSource = SourceType.TEMPORARY,
                selectedStatus = null
            )
        }
    }
}
