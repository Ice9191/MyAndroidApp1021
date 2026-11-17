package com.hxb.myandroidapp_1021.pages.picking

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PickingConfirmViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PickingConfirmUiState())
    val uiState: StateFlow<PickingConfirmUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        _uiState.update { it.copy(isLoading = false) }
    }

    fun onEmployeeIdChange(value: String) {
        _uiState.update { it.copy(employeeId = value, errorMessage = null) }
    }

    /**
     * 只提交当前流程已读签的 RFID；请求校验与重复提交保护均在 ViewModel 完成。
     */
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
        // 归档流程未接入服务端，不得本地伪造提交成功。
        _uiState.update { it.copy(errorMessage = "请使用当前工单拣选页面提交") }
    }
}
