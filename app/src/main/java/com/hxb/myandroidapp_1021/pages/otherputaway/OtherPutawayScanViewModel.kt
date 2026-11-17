package com.hxb.myandroidapp_1021.pages.otherputaway

import com.hxb.myandroidapp_1021.domain.scanner.RfidReaderFactory
import com.hxb.myandroidapp_1021.pages.scanner.ScanViewModel


import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.hxb.myandroidapp_1021.domain.otherputaway.*
import com.hxb.myandroidapp_1021.utils.operationResult
import com.hxb.myandroidapp_1021.utils.userMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class OtherPutawayScanViewModel @Inject constructor(
    reader: RfidReaderFactory,
    private val repository: OtherPutawayRepository
) : ScanViewModel(reader) {
    private val _uiState = MutableStateFlow(OtherPutawayScanUiState())
    val uiState = _uiState.asStateFlow()

    fun selectTask(task: OtherPutawayTaskUi, fromCache: Boolean = false) {
        if (_uiState.value.isSubmitting) return
        _uiState.value = OtherPutawayScanUiState(task = task, fromCache = fromCache)
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
        if (_uiState.value.isSubmitting) return
        _uiState.update { state ->
            val rfid =
                state.task?.tools?.firstOrNull { it.rfid.equals(state.rfid.trim(), true) }?.rfid
                    ?: state.rfid.trim()
            val tool = state.task?.tools?.firstOrNull { it.rfid.equals(rfid, ignoreCase = true) }
            val nextState = when {
                state.task?.closed == true -> state.copy(notificationMessage = "任务已关闭，无法继续操作")
                rfid.isBlank() -> state.copy(notificationMessage = "请先扫描或输入 RFID")
                tool == null -> state.copy(notificationMessage = "RFID 不属于当前任务")
                !tool.canProcess -> state.copy(notificationMessage = tool.blockedReason.ifBlank { "该器具当前不能处理，请核对来源单据" })
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
        if (_uiState.value.isSubmitting) return
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
    fun confirmBatch(onSuccess: (OtherPutawayTaskUi) -> Unit) {
        val state = _uiState.value
        val task = state.task ?: return
        if (state.isSubmitting || task.closed || state.readRfids.isEmpty()) return
        _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
        viewModelScope.launch {
            operationResult {
                repository.confirm(
                    OtherPutawayRequest(
                        task.sourceNo,
                        task.sourceType,
                        state.readRfids.map {
                            PutawayBinding(
                                it,
                                state.readLocations.getValue(it)
                            )
                        })
                )
            }
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            task = result,
                            readRfids = emptySet(),
                            isSubmitting = false,
                            fromCache = false,
                            readLocations = emptyMap(),
                            location = "",
                            errorMessage = null
                        )
                    }
                    onSuccess(result)
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = error.userMessage()
                        )
                    }
                }
        }
    }

    override fun onTagRead(rfid: String) {
        if (_uiState.value.isSubmitting) return
        onRfidChange(rfid)
        if (_uiState.value.location.isNotBlank()) identifyTool()
    }
}
