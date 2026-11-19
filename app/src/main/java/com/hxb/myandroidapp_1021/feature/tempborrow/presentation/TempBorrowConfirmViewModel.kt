package com.hxb.myandroidapp_1021.feature.tempborrow.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.feature.tempborrow.domain.model.TempBorrowConfirmRequest
import com.hxb.myandroidapp_1021.feature.tempborrow.domain.model.TempBorrowDetailStatus
import com.hxb.myandroidapp_1021.feature.tempborrow.domain.model.TempBorrowOrder
import com.hxb.myandroidapp_1021.feature.tempborrow.domain.repository.TempBorrowRepository
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
 * 临时借出确认页的 ViewModel：接收列表页选中的单据，负责逐条确认借出并回传更新后的单据。
 */
@HiltViewModel
class TempBorrowConfirmViewModel @Inject constructor(
    private val repository: TempBorrowRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TempBorrowConfirmUiState())
    val uiState = _uiState.asStateFlow()

    private val mockOperator = "演示材料员"

    /**
     * 绑定列表页当前选中的单据；换单时清空提交与提示状态，同一单据刷新时保留提示。
     */
    fun bindOrder(order: TempBorrowOrder?, pickedRfids: Set<String>) {
        _uiState.update { state ->
            if (state.order?.orderNo != order?.orderNo) {
                state.copy(
                    order = order,
                    pickedRfids = pickedRfids,
                    isSubmitting = false,
                    notificationMessage = null
                )
            } else {
                state.copy(order = order, pickedRfids = pickedRfids)
            }
        }
    }

    /**
     * 逐条确认借出；成功后把整单回传给列表页，避免列表与确认页状态不一致。
     */
    fun confirmLine(lineNo: Int, onSuccess: (TempBorrowOrder) -> Unit) {
        val state = _uiState.value
        if (state.isSubmitting) return
        val order = state.order ?: return
        val line = order.details.firstOrNull {
            it.lineNo == lineNo && it.status == TempBorrowDetailStatus.PENDING
        } ?: return
        _uiState.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            val success = repository.submitBorrow(
                TempBorrowConfirmRequest(orderNo = order.orderNo, lineNo = line.lineNo, rfid = line.rfid)
            )
            if (!success) {
                _uiState.update { it.copy(isSubmitting = false) }
                notify("确认借出失败，请重试")
                return@launch
            }
            val currentOrder = _uiState.value.order ?: return@launch
            val updated = currentOrder.markBorrowed(lineNo, mockOperator, now())
            _uiState.update {
                it.copy(
                    order = updated,
                    isSubmitting = false,
                    notificationMessage = "已确认借出，可在临时归还中查看待还记录",
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
