package com.hxb.myandroidapp_1021.ui.returns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class ReturnConfirmViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ReturnConfirmUiState())
    val uiState: StateFlow<ReturnConfirmUiState> = _uiState.asStateFlow()

    fun onEmployeeIdChange(value: String) {
        _uiState.update { it.copy(employeeId = value) }
    }

    fun onLocationChange(value: String) {
        _uiState.update { it.copy(locationCode = value, boundLocation = "") }
    }

    fun bindLocation() {
        _uiState.update { it.copy(boundLocation = it.locationCode.trim()) }
    }

    /**
     * 库位修改会撤销绑定；确认时统一校验表单及读取清单，并阻止重复提交。
     */
    fun confirmReturn(order: ReturnOrder?, tools: List<ReturnToolScanItem>, onSuccess: () -> Unit) {
        val state = _uiState.value
        if (order == null || tools.isEmpty() || !state.canConfirm) return
        val request = ReturnConfirmRequest(
            order.orderNo,
            state.employeeId.trim(),
            state.boundLocation,
            tools.map { it.rfid })
        if (request.employeeId.isBlank() || request.locationCode.isBlank()) return
        _uiState.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            // TODO: 字段未确定，确认后将 request 交给真实归还上架接口。
            delay(800.milliseconds)
            _uiState.update { it.copy(isSubmitting = false) }
            onSuccess()
        }
    }
}
