package com.hxb.myandroidapp_1021.feature.otherputaway.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.feature.otherputaway.domain.model.OtherPutawayConfirmRequest
import com.hxb.myandroidapp_1021.feature.otherputaway.domain.model.OtherPutawayOrder
import com.hxb.myandroidapp_1021.feature.otherputaway.domain.model.PutawayDetailStatus
import com.hxb.myandroidapp_1021.feature.otherputaway.domain.repository.OtherPutawayRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 其他上架扫描状态管理；人工读取与单条上架确认都在本页完成，确认成功后回写列表。
 */
@HiltViewModel
class OtherPutawayScanViewModel @Inject constructor(
    private val otherPutawayRepository: OtherPutawayRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OtherPutawayScanUiState())
    val uiState: StateFlow<OtherPutawayScanUiState> = _uiState.asStateFlow()

    /**
     * 进入扫描页时装载列表选中的单据，只保留待上架明细。
     */
    fun selectOrder(order: OtherPutawayOrder) {
        val pendingDetails = order.details.filter { it.status == PutawayDetailStatus.PENDING }
        _uiState.value = OtherPutawayScanUiState(
            order = order,
            tools = pendingDetails.map { it.toScanItem() }
        )
    }

    /**
     * 点击明细打开单条上架抽屉：只允许待上架明细，实际货位不预填，由扫描或输入确定。
     */
    fun selectTool(lineNo: Int) {
        val tool = _uiState.value.tools.firstOrNull { it.lineNo == lineNo } ?: return
        if (_uiState.value.isSubmitting) return
        _uiState.update {
            it.copy(
                selectedLineNo = tool.lineNo,
                showConfirmDrawer = true,
                draftLocation = "",
                submitError = null
            )
        }
    }

    fun onDraftLocationChange(value: String) {
        _uiState.update { it.copy(draftLocation = value, submitError = null) }
    }

    /** 收起上架抽屉；提交请求进行中不允许关闭。 */
    fun dismissConfirmDrawer() {
        if (_uiState.value.isSubmitting) return
        _uiState.update {
            it.copy(showConfirmDrawer = false, selectedLineNo = null, draftLocation = "", submitError = null)
        }
    }

    /**
     * 提交单件上架：校验读取状态与实际货位后调用仓库，成功后移除该明细并提示上架成功。
     * TODO: 字段未确定：实际货位校验规则与提交成功后的库存流转待后端确认。
     */
    fun confirmPutaway(onSuccess: (OtherPutawayOrder) -> Unit) {
        val state = _uiState.value
        val order = state.order ?: return
        val tool = state.selectedTool ?: return
        if (state.isSubmitting) return
        if (!tool.isRead) {
            _uiState.update { it.copy(submitError = "请先完成该器具的人工读取") }
            return
        }
        val location = state.draftLocation.trim()
        if (location.isEmpty()) {
            _uiState.update { it.copy(submitError = "请扫描或输入实际货位码") }
            return
        }
        _uiState.update { it.copy(isSubmitting = true, submitError = null) }
        viewModelScope.launch {
            val success = otherPutawayRepository.submitPutaway(
                OtherPutawayConfirmRequest(
                    putawayOrderNo = order.putawayOrderNo,
                    lineNo = tool.lineNo,
                    rfid = tool.rfid,
                    locationCode = location
                )
            )
            if (!success) {
                _uiState.update { it.copy(isSubmitting = false, submitError = "提交失败，请重试") }
                return@launch
            }
            val updatedOrder = order.markPutaway(tool.lineNo, location)
            _uiState.update {
                it.copy(
                    order = updatedOrder,
                    tools = it.tools.filterNot { item -> item.lineNo == tool.lineNo },
                    selectedLineNo = null,
                    showConfirmDrawer = false,
                    draftLocation = "",
                    isSubmitting = false,
                    submitError = null,
                    notificationMessage = "上架成功",
                    notificationId = it.notificationId + 1
                )
            }
            onSuccess(updatedOrder)
        }
    }

    /** 人工读取页整批切换读取状态。 */
    fun toggleRead(lineNos: Set<Int>) {
        _uiState.update { state ->
            state.copy(
                tools = state.tools.map {
                    if (it.lineNo in lineNos) it.copy(isRead = !it.isRead) else it
                }
            )
        }
    }

    fun onNotificationDismiss() {
        _uiState.update { it.copy(notificationMessage = null) }
    }
}
