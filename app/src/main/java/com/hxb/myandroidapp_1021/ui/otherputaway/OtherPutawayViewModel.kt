package com.hxb.myandroidapp_1021.ui.otherputaway

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OtherPutawayViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OtherPutawayUiState())
    val uiState = _uiState.asStateFlow()

    fun selectTask(task: OtherPutawayTaskUi) {
        _uiState.value = OtherPutawayUiState(task = task)
    }

    fun onRfidChange(value: String) {
        _uiState.update {
            it.copy(
                rfid = value,
                identifiedRfid = "",
                location = "",
                showDialog = false,
                message = null
            )
        }
    }

    fun onLocationChange(value: String) {
        _uiState.update { it.copy(location = value, showDialog = false, message = null) }
    }

    fun onTabSelected(value: Int) {
        _uiState.update { it.copy(selectedTab = value) }
    }

    fun onDialogChange(value: Boolean) {
        _uiState.update { it.copy(showDialog = value && it.canConfirm) }
    }

    /**
     * 识别当前任务未完成工具；送检、送修按文档默认带出原始出库货位。
     */
    fun identifyTool() {
        _uiState.update { state ->
            val tool = state.task?.tools?.firstOrNull { it.rfid == state.rfid.trim() }
            when {
                state.rfid.isBlank() -> state.copy(message = "请先扫描或输入 RFID")
                tool == null -> state.copy(message = "RFID 不属于当前任务")
                tool.completed -> state.copy(message = "该工具已上架，无需重复操作")
                else -> state.copy(
                    identifiedRfid = tool.rfid,
                    location = if (state.task.sourceType in listOf(
                            SourceType.INSPECTION,
                            SourceType.REPAIR
                        )
                    ) tool.location else "",
                    message = "已识别：${tool.name}，请核对实际货位"
                )
            }
        }
    }

    /**
     * 页面演示仅逐件记录实际货位，不统一恢复不同业务来源的库存可用状态。
     * TODO: 字段未确定，真实货位校验、上架资格及提交协议需后端确认。
     */
    fun confirmItem() {
        _uiState.update { state ->
            if (!state.canConfirm) state else state.copy(
                task = state.task?.let { task ->
                    task.copy(tools = task.tools.map {
                        if (it.rfid == state.identifiedRfid) it.copy(
                            completed = true,
                            location = state.location.trim()
                        ) else it
                    })
                },
                rfid = "", identifiedRfid = "", location = "", showDialog = false,
                message = "本件已演示上架，未提交业务单据"
            )
        }
    }
}
