package com.hxb.myandroidapp_1021.pages.putaway

import com.hxb.myandroidapp_1021.domain.scanner.RfidReaderFactory
import com.hxb.myandroidapp_1021.pages.scanner.ScanViewModel


import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.hxb.myandroidapp_1021.utils.operationResult
import com.hxb.myandroidapp_1021.utils.userMessage
import com.hxb.myandroidapp_1021.domain.putaway.PutawayRepository
import com.hxb.myandroidapp_1021.domain.session.SessionRepository

@HiltViewModel
class PutawayConfirmViewModel @Inject constructor(
    reader: RfidReaderFactory, private val repository: PutawayRepository,
    session: SessionRepository
) : ScanViewModel(reader) {
    private val _uiState = MutableStateFlow(
        PutawayConfirmUiState(
            isLoading = false,
            operator = session.state.value.user?.userName.orEmpty()
        )
    )
    val uiState = _uiState.asStateFlow()

    fun onRfidChange(value: String) {
        _uiState.update { it.copy(rfid = value, selectedLineNo = null, boundLocation = "") }
    }

    fun onLocationChange(value: String) {
        _uiState.update { it.copy(locationCode = value, boundLocation = "") }
    }

    fun onTabSelected(value: Int) {
        _uiState.update { it.copy(selectedTab = value) }
    }

    fun dismissNotification() {
        _uiState.update { it.copy(notificationMessage = null) }
    }

    fun identifyTool(order: PutawayOrder?) {
        if (_uiState.value.isSubmitting) return
        val rfid = _uiState.value.rfid.trim()
        val detail = order?.details?.firstOrNull { it.rfid == rfid }
        val error = when {
            rfid.isBlank() -> "请先扫描或输入 RFID"
            detail == null -> "RFID 不属于当前上架单"
            detail.isCompleted -> "该工具已上架，无需重复操作"
            else -> null
        }
        _uiState.update {
            it.copy(
                selectedLineNo = if (error == null) detail?.lineNo else null,
                boundLocation = "",
                notificationMessage = error,
                notificationId = it.notificationId + 1
            )
        }
    }

    /**
     * 真实校验货位所属仓库，输入变化后不能沿用旧绑定结果。
     */
    fun bindLocation(order: PutawayOrder?) {
        val snapshot = _uiState.value
        if (snapshot.isSubmitting) return
        val detail = order?.details?.firstOrNull { it.lineNo == snapshot.selectedLineNo }
        if (detail == null) {
            notify("请先识别当前工具"); return
        }
        if (snapshot.locationCode.isBlank()) {
            notify("请输入实际货位"); return
        }
        _uiState.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            operationResult {
                repository.validateLocation(
                    detail.warehouse,
                    snapshot.locationCode.trim()
                )
            }
                .onSuccess { location ->
                    _uiState.update { current ->
                        if (current.selectedLineNo != snapshot.selectedLineNo || current.locationCode != snapshot.locationCode) current.copy(
                            isSubmitting = false
                        )
                        else current.copy(
                            isSubmitting = false,
                            boundLocation = location.code,
                            notificationMessage = "已绑定货位 ${location.code}",
                            notificationId = current.notificationId + 1
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isSubmitting = false) }; notify(
                    error.userMessage()
                )
                }
        }
    }

    fun confirmPutaway(order: PutawayOrder?, onPutaway: (PutawayOrder) -> Unit) {
        val state = _uiState.value
        val currentOrder = order ?: return
        val detail =
            currentOrder.details.firstOrNull { it.lineNo == state.selectedLineNo } ?: return
        if (state.isSubmitting || detail.isCompleted || state.boundLocation.isBlank()) return
        _uiState.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            // 文档 1.3.3：逐件提交即更新库存，不等待整单全部完成。
            operationResult {
                repository.confirm(
                    PutawayRequest(
                        currentOrder.orderNo,
                        detail.lineNo,
                        detail.rfid,
                        state.boundLocation
                    )
                )
            }
                .onSuccess { result ->
                    onPutaway(result)
                    _uiState.update {
                        it.copy(
                            rfid = "",
                            locationCode = "",
                            boundLocation = "",
                            selectedLineNo = null,
                            isSubmitting = false
                        )
                    }
                    notify(if (result.isCompleted) "本单已全部上架，工具库存已更新" else "上架成功，可继续扫描下一件")
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isSubmitting = false) }; notify(
                    error.userMessage()
                )
                }
        }
    }

    private fun notify(message: String) {
        _uiState.update {
            it.copy(
                notificationMessage = message,
                notificationId = it.notificationId + 1
            )
        }
    }

    private var currentOrder: PutawayOrder? = null
    fun setOrder(order: PutawayOrder?) {
        currentOrder = order
    }

    override fun onTagRead(rfid: String) {
        if (_uiState.value.isSubmitting) return
        onRfidChange(rfid)
        identifyTool(currentOrder)
    }
}
