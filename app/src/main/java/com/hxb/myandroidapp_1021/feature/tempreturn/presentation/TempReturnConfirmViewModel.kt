package com.hxb.myandroidapp_1021.feature.tempreturn.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnConfirmRequest
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnDetailStatus
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnOrder
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.repository.TempReturnRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 临时归还确认页的 ViewModel：接收列表页选中的单据，负责登记归还信息、逐条确认归还并回传更新后的单据。
 */
@HiltViewModel
class TempReturnConfirmViewModel @Inject constructor(
    private val repository: TempReturnRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TempReturnConfirmUiState())
    val uiState = _uiState.asStateFlow()

    /**
     * 绑定列表页当前选中的单据；换单时用单据上的登记信息初始化，同一单据刷新时保留已登记内容。
     */
    fun bindOrder(order: TempReturnOrder?) {
        _uiState.update { state ->
            if (state.order?.orderNo != order?.orderNo) {
                state.copy(
                    order = order,
                    returner = order?.returner.orEmpty(),
                    workOrderNo = order?.workOrderNo.orEmpty(),
                    returnLocation = order?.returnLocation.orEmpty(),
                    remark = order?.remark.orEmpty(),
                    infoDialogVisible = false,
                    infoError = null,
                    isSubmitting = false,
                    notificationMessage = null
                )
            } else {
                state.copy(order = order)
            }
        }
    }

    /**
     * 打开归还信息登记弹窗，草稿从当前已登记内容复制，取消时不影响已登记内容。
     */
    fun openInfoDialog() {
        _uiState.update {
            it.copy(
                infoDialogVisible = true,
                infoError = null,
                draftReturner = it.returner,
                draftWorkOrderNo = it.workOrderNo,
                draftReturnLocation = it.returnLocation,
                draftRemark = it.remark
            )
        }
    }

    fun onDraftReturnerChange(value: String) {
        _uiState.update { it.copy(draftReturner = value, infoError = null) }
    }

    fun onDraftWorkOrderNoChange(value: String) {
        _uiState.update { it.copy(draftWorkOrderNo = value, infoError = null) }
    }

    fun onDraftReturnLocationChange(value: String) {
        _uiState.update { it.copy(draftReturnLocation = value, infoError = null) }
    }

    fun onDraftRemarkChange(value: String) {
        _uiState.update { it.copy(draftRemark = value, infoError = null) }
    }

    /**
     * 校验必填项后把草稿登记为待提交字段并关闭弹窗；缺项时保留弹窗并在内容区提示。
     */
    fun confirmInfo() {
        val state = _uiState.value
        val error = when {
            state.draftReturner.isBlank() -> "请填写归还人"
            state.draftReturnLocation.isBlank() -> "请填写归还库位"
            else -> null
        }
        if (error != null) {
            _uiState.update { it.copy(infoError = error) }
            return
        }
        _uiState.update {
            it.copy(
                returner = it.draftReturner.trim(),
                workOrderNo = it.draftWorkOrderNo.trim(),
                returnLocation = it.draftReturnLocation.trim(),
                remark = it.draftRemark.trim(),
                infoDialogVisible = false,
                infoError = null
            )
        }
    }

    fun dismissInfoDialog() {
        _uiState.update { it.copy(infoDialogVisible = false, infoError = null) }
    }

    /**
     * 逐条确认归还；未登记归还人和归还库位时先提示登记，成功后把整单回传给列表页。
     */
    fun confirmLine(lineNo: Int, onSuccess: (TempReturnOrder) -> Unit) {
        val state = _uiState.value
        if (state.isSubmitting) return
        val order = state.order ?: return
        if (state.returner.isBlank() || state.returnLocation.isBlank()) {
            notify("请先登记归还信息")
            return
        }
        val line = order.details.firstOrNull {
            it.lineNo == lineNo && it.status == TempReturnDetailStatus.PENDING
        } ?: return
        _uiState.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            val updated = repository.submitReturn(
                TempReturnConfirmRequest(
                    orderNo = order.orderNo,
                    lineNo = line.lineNo,
                    rfid = line.rfid,
                    returner = state.returner,
                    workOrderNo = state.workOrderNo,
                    returnLocation = state.returnLocation,
                    remark = state.remark
                )
            )
            if (updated == null) {
                _uiState.update { it.copy(isSubmitting = false) }
                notify("确认归还失败，请重试")
                return@launch
            }
            _uiState.update {
                it.copy(
                    order = updated,
                    returner = updated.returner,
                    workOrderNo = updated.workOrderNo,
                    returnLocation = updated.returnLocation,
                    remark = updated.remark,
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
}
