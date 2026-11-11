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
        loadConfirmData()
    }

    private fun loadConfirmData() {
        viewModelScope.launch {
            delay(500.milliseconds)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    orderNo = "JC20231028001",
                    orderStatus = PickingStatus.UNPICKED,
                    borrower = "张三",
                    workshop = "检修一班",
                    handler = "王芳（材料员）",
                    outboundTime = "2026-10-30 09:30",
                    unreadCount = 2, // 假设还有2件未读取
                    readTools = listOf(
                        PickingToolScanItem(
                            id = 1,
                            name = "绝缘扳手",
                            status = ToolScanItemStatus.PICKED,
                            itemCode = "GJ-001",
                            rfid = "E280 1160 6000 0200 0001",
                            recommendedLocation = "A1-1",
                            unit = "个"
                        ),
                        PickingToolScanItem(
                            id = 2,
                            name = "数字万用表",
                            status = ToolScanItemStatus.PICKED,
                            itemCode = "GJ-002",
                            rfid = "E280 1160 6000 0200 0002",
                            recommendedLocation = "A1-2",
                            unit = "个"
                        )
                    )
                )
            }
        }
    }

    fun onEmployeeIdChange(newId: String) {
        _uiState.update { it.copy(employeeId = newId, errorMessage = null) }
    }

    fun confirmOutbound(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.employeeId.isBlank()) {
            _uiState.update { it.copy(errorMessage = "请输入领料人工号") }
            return
        }
        if (state.isSubmitting) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

            // 模拟提交网络请求
            delay(800.milliseconds)

            // TODO: 接入真实接口
            _uiState.update { it.copy(isSubmitting = false) }
            onSuccess()
        }
    }
}