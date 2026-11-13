package com.hxb.myandroidapp_1021.ui.otherputaway

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OtherPutawayScanViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OtherPutawayScanUiState())
    val uiState = _uiState.asStateFlow()

    fun selectTask(task: OtherPutawayTaskUi) {
        _uiState.value = OtherPutawayScanUiState(task = task)
    }

    fun onRfidChange(value: String) {
        _uiState.update { state ->
            val tool = state.task?.tools?.firstOrNull { it.rfid == value.trim() }
            val defaultLocation =
                if (state.task?.sourceType in listOf(SourceType.INSPECTION, SourceType.REPAIR)) {
                    tool?.location.orEmpty()
                } else ""
            state.copy(rfid = value, location = defaultLocation, message = null)
        }
    }

    fun onLocationChange(value: String) {
        _uiState.update { it.copy(location = value, message = null) }
    }

    fun onTabSelected(value: Int) {
        _uiState.update { it.copy(selectedTab = value) }
    }

    /**
     * 按文档保留 RFID 与实际货位绑定，核对后暂存到本批次；重复读取不累计，本地读取不等同于业务完成。
     */
    fun identifyTool() {
        _uiState.update { state ->
            val rfid = state.rfid.trim()
            val tool = state.task?.tools?.firstOrNull { it.rfid == rfid }
            val nextState = when {
                state.task?.closed == true -> state.copy(notificationMessage = "任务已关闭，无法继续操作")
                rfid.isBlank() -> state.copy(notificationMessage = "请先扫描或输入 RFID")
                tool == null -> state.copy(notificationMessage = "RFID 不属于当前任务")
                tool.completed -> state.copy(notificationMessage = "该工具已完成，无需重复操作")
                state.location.isBlank() -> state.copy(notificationMessage = "请扫描或输入实际货位码")
                rfid in state.readRfids -> state.copy(notificationMessage = "该工具已读取，不重复计数")
                else -> state.copy(
                    readRfids = state.readRfids + rfid,
                    readLocations = state.readLocations + (rfid to state.location.trim()),
                    rfid = "",
                    notificationMessage = "已核对：${tool.name}"
                )
            }
            nextState.copy(message = null, notificationId = state.notificationId + 1)
        }
    }

    fun dismissNotification() {
        _uiState.update { it.copy(notificationMessage = null) }
    }

    fun clearReads() {
        _uiState.update {
            it.copy(
                readRfids = emptySet(),
                readLocations = emptyMap(),
                message = null
            )
        }
    }

    /**
     * 确认本批次上架结果，保存实际货位并清空本次读取记录。
     */
    fun confirmBatch() {
        _uiState.update { state ->
            if (state.task?.closed == true || state.readRfids.isEmpty()) state else state.copy(
                task = state.task?.let { task ->
                    task.copy(tools = task.tools.map {
                        if (it.rfid in state.readRfids) it.copy(
                            completed = true,
                            location = state.readLocations.getValue(it.rfid)
                        ) else it
                    })
                },
                readRfids = emptySet(),
                readLocations = emptyMap(),
                location = "",
                message = null
            )
        }
    }
}
