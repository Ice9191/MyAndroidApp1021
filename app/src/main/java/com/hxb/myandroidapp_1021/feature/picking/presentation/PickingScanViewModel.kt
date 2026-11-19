package com.hxb.myandroidapp_1021.feature.picking.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.feature.picking.domain.model.PickingConfirmRequest
import com.hxb.myandroidapp_1021.feature.picking.domain.model.PickingDetail
import com.hxb.myandroidapp_1021.feature.picking.domain.model.PickingDetailStatus
import com.hxb.myandroidapp_1021.feature.picking.domain.model.PickingOrder
import com.hxb.myandroidapp_1021.feature.picking.domain.repository.PickingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PickingScanViewModel @Inject constructor(
    private val pickingRepository: PickingRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PickingScanUiState(isLoading = true))
    val uiState: StateFlow<PickingScanUiState> = _uiState.asStateFlow()

    init {
        loadScanOrder()
    }

    /**
     * 首次加载作业页单据；列表已选定单据时保留列表选择的单据和明细。
     */
    private fun loadScanOrder() {
        viewModelScope.launch {
            val order = pickingRepository.loadScanOrder()
            _uiState.update { state ->
                if (state.order != null) {
                    state.copy(isLoading = false)
                } else {
                    state.copy(isLoading = false, order = order, tools = order.unpickedTools())
                }
            }
        }
    }

    /**
     * 新单据进入同一流程时清空上次读签结果，只加载未拣选明细。
     */
    fun selectOrder(order: PickingOrder) {
        _uiState.update {
            it.copy(isLoading = false, order = order, tools = order.unpickedTools())
        }
    }

    /**
     * 只把未拣选明细转换为本次扫描项，已拣选明细则不进入作业列表。
     */
    private fun PickingOrder.unpickedTools(): List<PickingToolScanItem> =
        details.filter { it.status == PickingDetailStatus.UNPICKED }.map { it.toScanItem() }

    private fun PickingDetail.toScanItem(): PickingToolScanItem = PickingToolScanItem(
        lineNo = lineNo,
        description = description,
        itemCode = itemCode,
        rfid = rfid,
        batchNo = batchNo,
        warehouse = warehouse,
        location = location,
        unit = unit
    )

    /**
     * 本批次确认成功后把对应明细置为已拣选并移除已读器具；单据状态由明细重新汇总。
     */
    private fun confirmPicked() {
        _uiState.update { state ->
            val order = state.order ?: return@update state
            val confirmed = state.tools.filter { it.isRead }
            if (confirmed.isEmpty()) return@update state
            state.copy(
                order = order.markPicked(confirmed.map { it.rfid }.toSet()),
                tools = state.tools.filterNot { it.isRead }
            )
        }
    }

    /**
     * 打开本批次确认抽屉；只改展示状态，不动读签结果，关闭后可继续核对。
     */
    fun openConfirmDrawer() {
        _uiState.update { state ->
            if (state.isSubmitting || state.submitSucceeded || state.readList.isEmpty()) state
            else state.copy(showConfirmDrawer = true, submitSucceeded = false, submitError = null)
        }
    }

    fun dismissConfirmDrawer() {
        _uiState.update { state ->
            if (state.isSubmitting || state.submitSucceeded) state
            else state.copy(showConfirmDrawer = false, submitError = null)
        }
    }

    /**
     * 提交抽屉展示的这一批器具；成功后只标记提交完成，由页面先播放抽屉收起动画再回写明细。
     * TODO: 字段未确定，真实接口接入后以响应结果更新状态和失败原因。
     */
    fun submitBatch() {
        val state = _uiState.value
        val order = state.order ?: return
        val batch = state.readList
        if (state.isSubmitting || batch.isEmpty()) return
        _uiState.update { it.copy(isSubmitting = true, submitError = null) }
        viewModelScope.launch {
            val rfids = batch.map { it.rfid }
            val success = pickingRepository.submitPicking(
                PickingConfirmRequest(orderNo = order.orderNo, rfids = rfids)
            )
            if (!success) {
                _uiState.update { it.copy(isSubmitting = false, submitError = "提交失败，请重试") }
                return@launch
            }
            // 保持抽屉显示，等页面播放完收起动画后调用 completeSubmit。
            _uiState.update {
                it.copy(isSubmitting = false, submitSucceeded = true, submitError = null)
            }
        }
    }

    /**
     * 确认抽屉收起动画结束后收起抽屉，并把本批次已确认的器具从待拣列表移除。
     */
    fun completeSubmit() {
        _uiState.update { it.copy(showConfirmDrawer = false, submitSucceeded = false) }
        confirmPicked()
    }

    /**
     * 只切换本次读签状态及时间，不直接改动明细状态或库存。
     */
    fun toggleRead(ids: Set<Int>) {
        val readDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        _uiState.update { state ->
            state.copy(tools = state.tools.map { tool ->
                if (tool.lineNo !in ids) tool else tool.copy(
                    isRead = !tool.isRead,
                    readDate = if (tool.isRead) "" else readDate
                )
            })
        }
    }
}
