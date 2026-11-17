package com.hxb.myandroidapp_1021.pages.returns

import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.domain.returns.ReturnRepository
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
class ReturnScanViewModel @Inject constructor(
    reader: RfidReaderFactory,
    private val repository: ReturnRepository
) : ScanViewModel(reader) {
    private val _uiState = MutableStateFlow(ReturnScanUiState())
    val uiState: StateFlow<ReturnScanUiState> = _uiState.asStateFlow()


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
    fun selectOrder(order: ReturnOrder) {
        if (_uiState.value.isSubmitting) return
        _uiState.value = ReturnScanUiState(order = order)
        refresh()
    }

    /**
     * 点击明细打开单条上架抽屉：只选中并预置推荐仓库与货位，不提交、不改读取状态。
     */
    fun selectTool(lineId: Int) {
        _uiState.update { state ->
            val tool = state.tools.firstOrNull { it.id == lineId } ?: return@update state
            state.copy(
                selectedLineId = lineId,
                showConfirmDrawer = true,
                draftWarehouse = tool.warehouse,
                draftLocation = if (tool.isPutaway || tool.isBadCondition) "" else tool.location,
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
            if (state.isSubmitting) state
            else state.copy(
                showConfirmDrawer = false,
                selectedLineId = null,
                draftLocation = "",
                draftWarehouse = "",
                submitError = null
            )
        }
    }

    /**
     * 只提交当前选中明细：先要求完成 RFID 核对，成功后更新该明细与单据进度并回调已上架件数。
     */
    fun confirmPutaway(onSuccess: (String, Int) -> Unit) {
        val state = _uiState.value
        val order = state.order ?: return
        val tool = state.selectedTool ?: return
        if (state.isSubmitting || tool.isPutaway) return
        val error = when {
            !tool.canProcess || tool.isBadCondition -> tool.blockedReason.ifBlank { "该器具需转送修或送检，不能正常上架" }
            !tool.isRead -> "请先完成该器具的 RFID 核对"
            state.draftLocation.isBlank() -> "请输入实际货位"
            else -> null
        }
        if (error != null) {
            _uiState.update { it.copy(submitError = error) }; return
        }
        _uiState.update { it.copy(isSubmitting = true, submitError = null) }
        viewModelScope.launch {
            operationResult {
                repository.confirm(
                    ReturnConfirmRequest(
                        order.orderNo,
                        tool.id,
                        tool.rfid,
                        state.draftLocation.trim()
                    )
                )
            }
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            order = result.order,
                            tools = result.tools.map { item -> item.toScanItem() },
                            selectedLineId = null,
                            showConfirmDrawer = false,
                            draftLocation = "",
                            draftWarehouse = "",
                            isSubmitting = false,
                            fromCache = false,
                            submitError = null
                        )
                    }
                    onSuccess(order.orderNo, result.order.returnedCount)
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
                if (state.isSubmitting || tool.id !in ids || tool.isPutaway) tool else tool.copy(
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
        if (!tool.isRead && !tool.isPutaway) toggleRead(setOf(tool.id))
    }
}
