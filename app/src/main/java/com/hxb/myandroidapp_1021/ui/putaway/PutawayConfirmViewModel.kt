package com.hxb.myandroidapp_1021.ui.putaway

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class PutawayConfirmViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PutawayConfirmUiState())
    val uiState: StateFlow<PutawayConfirmUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            delay(500.milliseconds)
            val mockOperator = "材料员"
            _uiState.update { it.copy(isLoading = false, operator = mockOperator) }
        }
    }

    fun onRfidChange(value: String) {
        _uiState.update {
            it.copy(
                rfid = value,
                selectedLineNo = null,
                boundLocation = "",
                errorMessage = null,
                message = null
            )
        }
    }

    fun onLocationChange(value: String) {
        _uiState.update {
            it.copy(
                locationCode = value,
                boundLocation = "",
                errorMessage = null,
                message = null
            )
        }
    }

    fun onTabSelected(value: Int) {
        _uiState.update { it.copy(selectedTab = value) }
    }

    fun identifyTool(order: PutawayOrder?) {
        val rfid = _uiState.value.rfid.trim()
        val detail = order?.details?.firstOrNull { it.rfid == rfid }
        val error = when {
            rfid.isBlank() -> "请先扫描或输入 RFID"
            detail == null -> "RFID 不属于当前上架单，请核对工具"
            detail.isCompleted -> "该工具已上架，无需重复操作"
            else -> null
        }
        _uiState.update {
            it.copy(
                selectedLineNo = if (error == null) detail?.lineNo else null,
                boundLocation = "",
                errorMessage = error,
                message = null
            )
        }
    }

    /**
     * 绑定当前工具的实际货位，校验所属仓库；工具或货位输入变化后必须重新绑定。
     * TODO: 字段未确定，替换为真实货位查询与有效性校验接口。
     */
    fun bindLocation(order: PutawayOrder?) {
        val state = _uiState.value
        val detail = order?.details?.firstOrNull { it.lineNo == state.selectedLineNo }
        val mockLocations = listOf(
            PutawayLocation("A1-01", "中心工具库"),
            PutawayLocation("A1-02", "中心工具库"),
            PutawayLocation("A1-03", "中心工具库"),
            PutawayLocation("B1-01", "电气工具库"),
            PutawayLocation("B1-02", "电气工具库")
        )
        val location = mockLocations.firstOrNull { it.code == state.locationCode.trim() }
        val error = when {
            detail == null -> "请先识别当前工具"
            location == null -> "货位不存在，请重新扫描或输入"
            location.warehouse != detail.warehouse -> "货位不属于当前工具的目标仓库"
            else -> null
        }
        _uiState.update {
            it.copy(
                boundLocation = if (error == null) location?.code.orEmpty() else "",
                errorMessage = error,
                message = if (error == null) "已绑定货位 ${location?.code}" else null
            )
        }
    }

    /**
     * 模拟单件提交，成功才更新流程中的库存明细；失败保留输入，允许重试。
     * TODO: 字段未确定，接入后端后以接口成功响应为库存与货位更新依据。
     */
    fun confirmPutaway(order: PutawayOrder?, onPutaway: (PutawayRequest) -> Boolean) {
        val currentOrder = order ?: return
        val state = _uiState.value
        val detail =
            currentOrder.details.firstOrNull { it.lineNo == state.selectedLineNo } ?: return
        if (state.isLoading || state.isSubmitting || detail.isCompleted || state.boundLocation.isBlank()) return
        val request = PutawayRequest(
            currentOrder.orderNo,
            detail.lineNo,
            detail.rfid,
            state.boundLocation,
            state.operator
        )
        _uiState.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            delay(500.milliseconds)
            val success = onPutaway(request)
            _uiState.update { current ->
                if (success) current.copy(
                    rfid = "", locationCode = "", boundLocation = "", selectedLineNo = null,
                    isSubmitting = false, errorMessage = null, message = "上架成功，可继续扫描下一件"
                ) else current.copy(
                    isSubmitting = false,
                    errorMessage = "上架未成功，请核对明细后重试"
                )
            }
        }
    }
}
