package com.hxb.myandroidapp_1021.feature.returns.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.feature.returns.domain.model.ReturnConfirmRequest
import com.hxb.myandroidapp_1021.feature.returns.domain.model.ReturnOrder
import com.hxb.myandroidapp_1021.feature.returns.domain.repository.ReturnRepository
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
 * 归还上架扫描页：数据来自 ReturnRepository，单条确认成功后同时更新明细和单据进度。
 */
@HiltViewModel
class ReturnScanViewModel @Inject constructor(
    private val returnRepository: ReturnRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReturnScanUiState(isLoading = true))
    val uiState: StateFlow<ReturnScanUiState> = _uiState.asStateFlow()

    // TODO: 字段未确定：登录人信息尚未接入业务模块，暂用模拟上架人，接入后改为当前登录用户。
    private val mockOperator = "张伟"

    init {
        loadScanOrder()
    }

    /**
     * 首次进入加载扫描单据；若列表页已选定单据，则不被模拟响应覆盖。
     */
    private fun loadScanOrder() {
        viewModelScope.launch {
            val order = returnRepository.loadScanOrder()
            _uiState.update { state ->
                if (state.order != null) {
                    state.copy(isLoading = false)
                } else {
                    state.withOrder(order)
                }
            }
        }
    }

    /**
     * 从列表进入扫描页时切换单据，按该单据明细重建扫描视图并清空上次读取结果。
     */
    fun selectOrder(order: ReturnOrder) {
        _uiState.update { it.withOrder(order) }
    }

    /**
     * 点击明细打开单条上架抽屉：仅待上架明细可操作，实际货位不预填，由扫描或输入确定。
     */
    fun selectTool(lineNo: Int) {
        _uiState.update { state ->
            val tool = state.tools.firstOrNull { it.lineNo == lineNo } ?: return@update state
            // 损坏、待送检明细没有系统推荐货位，也不能进入上架抽屉
            if (!tool.canPutaway) return@update state
            state.copy(
                selectedLineId = lineNo,
                showConfirmDrawer = true,
                draftLocation = "",
                submitError = null
            )
        }
    }

    fun onDraftLocationChange(value: String) {
        _uiState.update { it.copy(draftLocation = value) }
    }

    /**
     * 关闭抽屉不提交，丢弃本次未保存的货位调整，保留已读取结果。
     */
    fun dismissConfirmDrawer() {
        _uiState.update { state ->
            if (state.isSubmitting) {
                state
            } else {
                state.copy(
                    showConfirmDrawer = false,
                    selectedLineId = null,
                    draftLocation = "",
                    submitError = null
                )
            }
        }
    }

    /**
     * 只提交当前选中明细：要求已完成 RFID 核对且实际货位有效，成功后更新该明细与单据进度。
     * TODO: 字段未确定，接入真实接口后以响应结果更新明细、上架人和上架时间。
     */
    fun confirmPutaway(onSuccess: (ReturnOrder) -> Unit) {
        val state = _uiState.value
        val order = state.order ?: return
        val tool = state.selectedTool ?: return
        val location = state.draftLocation.trim()
        if (state.isSubmitting || !tool.canPutaway || location.isBlank()) return
        if (!tool.isRead) {
            _uiState.update { it.copy(submitError = "请先完成该器具的 RFID 核对") }
            return
        }
        _uiState.update { it.copy(isSubmitting = true, submitError = null) }
        viewModelScope.launch {
            val success = returnRepository.submitPutaway(
                ReturnConfirmRequest(
                    orderNo = order.orderNo,
                    lineNo = tool.lineNo,
                    rfid = tool.rfid,
                    locationCode = location
                )
            )
            if (!success) {
                _uiState.update {
                    it.copy(isSubmitting = false, submitError = "提交失败，请重试")
                }
                return@launch
            }
            val at = now()
            val updatedOrder = order.markPutaway(tool.lineNo, location, mockOperator, at)
            _uiState.update { current ->
                current.copy(
                    order = updatedOrder,
                    // 上架完成的明细从待上架列表移除，扫描页只保留待上架物资
                    tools = current.tools.filterNot { it.lineNo == tool.lineNo },
                    showConfirmDrawer = false,
                    selectedLineId = null,
                    draftLocation = "",
                    isSubmitting = false,
                    submitError = null,
                    notificationMessage = "上架成功",
                    notificationId = current.notificationId + 1
                )
            }
            onSuccess(updatedOrder)
        }
    }

    /**
     * ToastNotification 自动消失后清除消息，并递增 id 以便下一次通知重新计时。
     */
    fun onNotificationDismiss() {
        _uiState.update {
            it.copy(
                notificationMessage = null,
                notificationId = it.notificationId + 1
            )
        }
    }

    private fun now(): String =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

    /**
     * 只切换本次读签状态及时间，不直接改动单据已完成数量或库存。
     */
    fun toggleRead(lineNos: Set<Int>) {
        val readDate = now()
        _uiState.update { state ->
            state.copy(
                tools = state.tools.map { tool ->
                    if (tool.lineNo !in lineNos) {
                        tool
                    } else {
                        tool.copy(
                            isRead = !tool.isRead,
                            readDate = if (tool.isRead) "" else readDate
                        )
                    }
                }
            )
        }
    }

    /**
     * 用未上架明细重建扫描视图；损坏、待送检明细保留展示，便于现场核对，但不能上架。
     */
    private fun ReturnScanUiState.withOrder(order: ReturnOrder): ReturnScanUiState {
        val tools = order.details.filterNot { it.isPutaway }.map { it.toScanItem() }
        return copy(
            isLoading = false,
            order = order,
            tools = tools,
            selectedLineId = null,
            showConfirmDrawer = false,
            draftLocation = "",
            submitError = null
        )
    }
}
