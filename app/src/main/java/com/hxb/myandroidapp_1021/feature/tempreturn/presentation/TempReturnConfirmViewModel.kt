package com.hxb.myandroidapp_1021.feature.tempreturn.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnConfirmRequest
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnDetailStatus
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnOrder
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.repository.TempReturnRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 临时归还确认页的 ViewModel：接收列表页选中的单据，负责逐条确认归还并回传更新后的单据。
 */
@HiltViewModel
class TempReturnConfirmViewModel @Inject constructor(
    private val repository: TempReturnRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TempReturnConfirmUiState())
    val uiState = _uiState.asStateFlow()

    private val mockOperator = "演示材料员"

    /**
     * 绑定列表页当前选中的单据；换单时清空提交与提示状态，同一单据刷新时保留提示。
     */
    fun bindOrder(order: TempReturnOrder?) {
        _uiState.update { state ->
            if (state.order?.orderNo != order?.orderNo) {
                state.copy(
                    order = order,
                    isSubmitting = false,
                    notificationMessage = null
                )
            } else {
                state.copy(order = order)
            }
        }
    }

    /**
     * 逐条确认归还；成功后把整单回传给列表页，避免列表与确认页状态不一致。
     */
    fun confirmLine(lineNo: Int, onSuccess: (TempReturnOrder) -> Unit) {
        val state = _uiState.value
        if (state.isSubmitting) return
        val order = state.order ?: return
        val line = order.details.firstOrNull {
            it.lineNo == lineNo && it.status == TempReturnDetailStatus.PENDING
        } ?: return
        _uiState.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            val success = repository.submitReturn(
                TempReturnConfirmRequest(
                    orderNo = order.orderNo,
                    lineNo = line.lineNo,
                    rfid = line.rfid,
                    returner = order.returner,
                    workOrderNo = order.workOrderNo,
                    returnLocation = order.returnLocation,
                    remark = order.remark
                )
            )
            if (!success) {
                _uiState.update { it.copy(isSubmitting = false) }
                notify("确认归还失败，请重试")
                return@launch
            }
            val currentOrder = _uiState.value.order ?: return@launch
            val updated = currentOrder.markReturned(
                lineNo = line.lineNo,
                operator = mockOperator,
                at = now(),
                returner = currentOrder.returner,
                workOrderNo = currentOrder.workOrderNo,
                returnLocation = currentOrder.returnLocation,
                remark = currentOrder.remark
            )
            _uiState.update {
                it.copy(
                    order = updated,
                    isSubmitting = false,
                    notificationMessage = "已确认归还，器具待上架",
                    notificationId = it.notificationId + 1
                )
            }
            onSuccess(updated)
        }
    }

    fun dismissNotification() {
        _uiState.update {
            it.copy(notificationMessage = null, notificationId = it.notificationId + 1)
        }
    }

    private fun notify(message: String) {
        _uiState.update {
            it.copy(notificationMessage = message, notificationId = it.notificationId + 1)
        }
    }

    private fun now(): String =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
}
