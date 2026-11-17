package com.hxb.myandroidapp_1021.pages.picking

import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.domain.picking.PickingOrderDetail
import com.hxb.myandroidapp_1021.domain.picking.PickingRepository
import com.hxb.myandroidapp_1021.domain.scanner.RfidReaderFactory
import com.hxb.myandroidapp_1021.pages.scanner.ScanViewModel
import com.hxb.myandroidapp_1021.utils.operationResult
import com.hxb.myandroidapp_1021.utils.userMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@HiltViewModel
class PickingScanViewModel @Inject constructor(
    reader: RfidReaderFactory,
    private val repository: PickingRepository
) : ScanViewModel(reader) {
    private val _uiState = MutableStateFlow(PickingScanUiState())
    val uiState: StateFlow<PickingScanUiState> = _uiState.asStateFlow()


    private var loadJob: Job? = null

    fun refresh() {
        val orderNo = _uiState.value.order?.orderNo ?: return
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            operationResult { repository.detail(orderNo) }
                .onSuccess { result ->
                    _uiState.update { state ->
                        if (state.order?.orderNo != orderNo) state else state.copy(
                            isLoading = false, order = result.data.order,
                            tools = result.data.tools.map { it.toScanItem() },
                            fromCache = result.fromCache, errorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { state ->
                        if (state.order?.orderNo != orderNo) state else state.copy(
                            isLoading = false,
                            errorMessage = error.userMessage()
                        )
                    }
                }
        }
    }

    /**
     * 新单据进入同一流程时清空上次读签结果；过期的查询响应不会覆盖新选择的单据。
     */
    fun selectOrder(order: PickingOrder) {
        if (_uiState.value.isSubmitting) return
        _uiState.value = PickingScanUiState(order = order)
        refresh()
    }

    /**
     * 本批次确认成功后累加已拣数量并移除已确认明细；剩余为 0 时单据状态置为已拣选。
     * 未完成时保持部分拣选，允许下次继续扫描剩余器具。
     */
    private fun applyConfirmed(detail: PickingOrderDetail) {
        _uiState.update {
            it.copy(
                order = detail.order, tools = detail.tools.map { tool -> tool.toScanItem() },
                isSubmitting = false, showConfirmDrawer = false, fromCache = false
            )
        }
    }

    /**
     * 打开本批次确认抽屉；只改展示状态，不动读签结果，关闭后可继续核对。
     */
    fun openConfirmDrawer() {
        _uiState.update { state ->
            if (state.isSubmitting || state.readList.isEmpty()) state
            else state.copy(showConfirmDrawer = true, submitError = null)
        }
    }

    fun dismissConfirmDrawer() {
        _uiState.update { state ->
            if (state.isSubmitting) state else state.copy(
                showConfirmDrawer = false,
                submitError = null
            )
        }
    }

    /**
     * 提交抽屉展示的这一批器具；成功后累加数量、移除本批明细，并回调单据号与件数。
     */
    fun submitBatch(onSuccess: (String, Int) -> Unit) {
        val state = _uiState.value
        val order = state.order ?: return
        val batch = state.readList
        val blocked = batch.firstOrNull { !it.canProcess }
        if (blocked != null) {
            _uiState.update { it.copy(submitError = blocked.blockedReason.ifBlank { "本批存在当前不能拣选的器具，请重新核对" }) }
            return
        }
        if (state.isSubmitting || state.isLoading || batch.isEmpty()) return
        _uiState.update { it.copy(isSubmitting = true, submitError = null) }
        viewModelScope.launch {
            // 文档 1.4.2：确认只完成拣选并置为已备货，实际借出由一体机交接完成。
            operationResult {
                repository.confirm(
                    PickingConfirmRequest(
                        order.orderNo,
                        batch.map { it.rfid })
                )
            }
                .onSuccess { result ->
                    applyConfirmed(result); onSuccess(
                    order.orderNo,
                    batch.size
                )
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            submitError = error.userMessage()
                        )
                    }
                }
        }
    }

    /**
     * 只切换本次读签状态及时间，不直接改动单据已完成数量或库存。
     */
    fun toggleRead(ids: Set<Int>) {
        val readDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        _uiState.update { state ->
            state.copy(errorMessage = null, tools = state.tools.map { tool ->
                if (state.isSubmitting || tool.id !in ids || tool.completed) tool else tool.copy(
                    isRead = !tool.isRead,
                    readDate = if (tool.isRead) "" else readDate
                )
            })
        }
    }

    override fun onTagRead(rfid: String) {
        val state = _uiState.value
        if (state.isLoading || state.isSubmitting) return
        val tool = state.tools.firstOrNull { it.rfid.equals(rfid.trim(), ignoreCase = true) }
        if (tool == null) {
            _uiState.update { it.copy(errorMessage = "读到当前单据以外的 RFID，请核对实物") }; return
        }
        if (!tool.isRead && !tool.completed) toggleRead(setOf(tool.id))
    }
}
