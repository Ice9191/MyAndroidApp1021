package com.hxb.myandroidapp_1021.feature.putaway.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.feature.putaway.domain.model.PutawayOrder
import com.hxb.myandroidapp_1021.feature.putaway.domain.model.PutawayRequest
import com.hxb.myandroidapp_1021.feature.putaway.domain.repository.PutawayRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PutawayConfirmViewModel @Inject constructor(
    private val putawayRepository: PutawayRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PutawayConfirmUiState())
    val uiState: StateFlow<PutawayConfirmUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val operator = putawayRepository.loadOperator()
            _uiState.update { it.copy(isLoading = false, operator = operator) }
        }
    }

    fun onRfidChange(value: String) {
        _uiState.update {
            it.copy(rfid = value, selectedLineNo = null, boundLocation = "")
        }
    }

    fun onLocationChange(value: String) {
        _uiState.update {
            it.copy(locationCode = value, boundLocation = "")
        }
    }

    fun onTabSelected(value: Int) {
        _uiState.update { it.copy(selectedTab = value) }
    }

    /**
     * Toast 只展示 1 秒，由页面在到期后回调清除。
     */
    fun dismissNotification() {
        _uiState.update { it.copy(notificationMessage = null) }
    }

    /**
     * 识别 RFID 并只允许当前上架单内的未完成件；校验结果通过顶部 Toast 提示。
     */
    fun identifyTool(order: PutawayOrder?) {
        val rfid = _uiState.value.rfid.trim()
        val detail = order?.details?.firstOrNull { it.rfid == rfid }
        val error = when {
            rfid.isBlank() -> "请先扫描或输入 RFID"
            detail == null -> "RFID 不属于当前上架单，请核对工具"
            detail.isCompleted -> "该工具已上架，无需重复操作"
            else -> null
        }
        _uiState.update { state ->
            state.copy(
                selectedLineNo = if (error == null) detail?.lineNo else null,
                boundLocation = "",
                notificationMessage = error,
                notificationId = if (error != null) state.notificationId + 1 else state.notificationId
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
        viewModelScope.launch {
            val location = putawayRepository.validateLocation(state.locationCode.trim())
            val error = when {
                detail == null -> "请先识别当前工具"
                location == null -> "货位不存在，请重新扫描或输入"
                location.warehouse != detail.warehouse -> "货位不属于当前工具的目标仓库"
                else -> null
            }
            _uiState.update {
                it.copy(
                    boundLocation = if (error == null) location?.code.orEmpty() else "",
                    notificationMessage = error ?: "已绑定货位 ${location?.code}",
                    notificationId = it.notificationId + 1
                )
            }
        }
    }

    /**
     * 模拟单件提交，成功才更新流程中的库存明细；失败保留输入，允许重试。
     * 整单全部完成后用顶部 Toast 提示库存已更新。
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
            val success = putawayRepository.confirmPutaway(request) && onPutaway(request)
            // 提交前先判断本件之外是否还有未完成明细，用于区分“单件完成”和“整单完成”
            val remainsUncompleted =
                currentOrder.details.any { !it.isCompleted && it.lineNo != detail.lineNo }
            _uiState.update { current ->
                if (success) current.copy(
                    rfid = "", locationCode = "", boundLocation = "", selectedLineNo = null,
                    isSubmitting = false,
                    notificationMessage = if (remainsUncompleted) {
                        "上架成功，可继续扫描下一件"
                    } else {
                        "本单已全部上架，工具库存已更新"
                    },
                    notificationId = current.notificationId + 1
                ) else current.copy(
                    isSubmitting = false,
                    notificationMessage = "上架未成功，请核对明细后重试",
                    notificationId = current.notificationId + 1
                )
            }
        }
    }
}
