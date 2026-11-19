package com.hxb.myandroidapp_1021.feature.otherpicking.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.feature.otherpicking.domain.model.OtherPickingConfirmRequest
import com.hxb.myandroidapp_1021.feature.otherpicking.domain.model.OtherPickingOrder
import com.hxb.myandroidapp_1021.feature.otherpicking.domain.model.PickingDetailStatus
import com.hxb.myandroidapp_1021.feature.otherpicking.domain.repository.OtherPickingRepository
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

/**
 * 其他拣选扫描页状态管理；人工拣选、本批次提交和进度回写都在这里完成。
 */
@HiltViewModel
class OtherPickingScanViewModel @Inject constructor(
    private val otherPickingRepository: OtherPickingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OtherPickingScanUiState())
    val uiState: StateFlow<OtherPickingScanUiState> = _uiState.asStateFlow()

    /** 接收列表页选中的单据，只保留待拣选明细。 */
    fun selectOrder(order: OtherPickingOrder) {
        _uiState.value = OtherPickingScanUiState(
            order = order,
            tools = order.details
                .filter { it.status == PickingDetailStatus.UNPICKED }
                .map { detail ->
                    OtherPickingToolScanItem(
                        lineNo = detail.lineNo,
                        rfid = detail.rfid,
                        itemCode = detail.itemCode,
                        description = detail.description,
                        unit = detail.unit,
                        batchNo = detail.batchNo,
                        warehouse = detail.warehouse,
                        location = detail.location
                    )
                }
        )
    }

    /**
     * 人工读取/取消读取选中的明细，读取时间只在首次读取时记录。
     */
    fun toggleRead(lineNos: Set<Int>) {
        if (lineNos.isEmpty()) return
        val readTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        _uiState.update { state ->
            state.copy(
                tools = state.tools.map { tool ->
                    if (tool.lineNo in lineNos) {
                        tool.copy(
                            isRead = !tool.isRead,
                            readDate = if (tool.isRead) "" else readTime
                        )
                    } else {
                        tool
                    }
                }
            )
        }
    }

    /** 打开本批次确认抽屉；提交中或没有已读取明细时不响应。 */
    fun openConfirmDrawer() {
        val state = _uiState.value
        if (state.isSubmitting || state.submitSucceeded || state.readList.isEmpty()) return
        _uiState.update { it.copy(showConfirmDrawer = true, submitSucceeded = false, submitError = null) }
    }

    fun dismissConfirmDrawer() {
        if (_uiState.value.isSubmitting || _uiState.value.submitSucceeded) return
        _uiState.update { it.copy(showConfirmDrawer = false, submitError = null) }
    }

    /**
     * 提交本批次已读取明细；成功后只标记提交完成，由页面先播放抽屉收起动画再执行后续动作。
     */
    fun submitBatch() {
        val state = _uiState.value
        val order = state.order ?: return
        val rfids = state.readList.map { it.rfid }
        if (rfids.isEmpty() || state.isSubmitting) return
        _uiState.update { it.copy(isSubmitting = true, submitError = null) }
        viewModelScope.launch {
            val success = otherPickingRepository.submitBatch(
                OtherPickingConfirmRequest(
                    pickingOrderNo = order.pickingOrderNo,
                    rfids = rfids
                )
            )
            if (success) {
                // 保持抽屉显示，等页面播放完收起动画后调用 finishSubmit。
                _uiState.update {
                    it.copy(isSubmitting = false, submitSucceeded = true, submitError = null)
                }
            } else {
                _uiState.update {
                    it.copy(isSubmitting = false, submitError = "提交失败，请重试")
                }
            }
        }
    }

    /**
     * 确认抽屉收起动画结束后收起抽屉，并把本批次已读取明细从待拣列表移除。
     */
    fun finishSubmit() {
        _uiState.update { current ->
            current.copy(
                showConfirmDrawer = false,
                submitSucceeded = false,
                tools = current.tools.filterNot { it.isRead }
            )
        }
    }
}
