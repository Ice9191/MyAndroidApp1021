package com.hxb.myandroidapp_1021.ui.otherpicking

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OtherPickingScanViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OtherPickingScanUiState())
    val uiState = _uiState.asStateFlow()

    fun selectTask(task: OtherPickingTaskUi) {
        _uiState.value = OtherPickingScanUiState(task = task)
    }
    fun onRfidChange(value: String) { _uiState.update { it.copy(rfid = value, message = null) } }
    fun onTabSelected(value: Int) { _uiState.update { it.copy(selectedTab = value) } }

    /**
     * 只核对当前任务的未完成工具，重复读取不累计，本地读取不等同于业务完成。
     */
    fun identifyTool() {
        _uiState.update { state ->
            val rfid = state.rfid.trim()
            val tool = state.task?.tools?.firstOrNull { it.rfid == rfid }
            when {
                rfid.isBlank() -> state.copy(message = "请先扫描或输入 RFID")
                tool == null -> state.copy(message = "RFID 不属于当前任务")
                tool.completed -> state.copy(message = "该工具已完成，无需重复操作")
                rfid in state.readRfids -> state.copy(message = "该工具已读取，不重复计数")
                else -> state.copy(readRfids = state.readRfids + rfid, rfid = "", message = "已核对：${tool.name}")
            }
        }
    }

    fun clearReads() { _uiState.update { it.copy(readRfids = emptySet(), message = null) } }

    /**
     * 仅演示本批次确认后的页面进度，不能代替各来源业务的出库接口。
     */
    fun confirmBatch() {
        _uiState.update { state ->
            if (state.readRfids.isEmpty()) state else state.copy(
                task = state.task?.let { task -> task.copy(tools = task.tools.map {
                    if (it.rfid in state.readRfids) it.copy(completed = true) else it
                }) },
                readRfids = emptySet(),
                message = "本批次已演示确认，未提交业务单据"
            )
        }
    }
}
