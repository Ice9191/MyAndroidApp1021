package com.hxb.myandroidapp_1021.ui.picking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class PickingConfirmViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PickingConfirmUiState())
    val uiState: StateFlow<PickingConfirmUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            delay(500.milliseconds)
            val mockConfirmation =
                PickingConfirmData(handler = "王芳（材料员）", outboundTime = "2026-11-11 09:30")
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    handler = mockConfirmation.handler,
                    outboundTime = mockConfirmation.outboundTime
                )
            }
        }
    }

    fun onEmployeeIdChange(value: String) {
        _uiState.update { it.copy(employeeId = value, errorMessage = null) }
    }

    /** 只提交当前流程已读签的 RFID；请求校验与重复提交保护均在 ViewModel 完成。 */
    fun confirmOutbound(
        order: PickingOrder?,
        tools: List<PickingToolScanItem>,
        onSuccess: () -> Unit
    ) {
        val state = _uiState.value
        if (state.isLoading || state.isSubmitting || order == null || tools.isEmpty()) return
        val request =
            PickingConfirmRequest(order.orderNo, state.employeeId.trim(), tools.map { it.rfid })
        if (request.employeeId.isBlank()) {
            _uiState.update { it.copy(errorMessage = "请输入领料人工号") }
            return
        }
        _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
        viewModelScope.launch {
            // TODO: 字段未确定，确认后将 request 交给真实出库接口。
            delay(800.milliseconds)
            _uiState.update { it.copy(isSubmitting = false) }
            onSuccess()
        }
    }
}
