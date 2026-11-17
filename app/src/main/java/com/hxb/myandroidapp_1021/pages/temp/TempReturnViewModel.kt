package com.hxb.myandroidapp_1021.pages.temp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.domain.temp.*
import com.hxb.myandroidapp_1021.utils.operationResult
import com.hxb.myandroidapp_1021.utils.userMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@HiltViewModel
class TempReturnViewModel @Inject constructor(private val repository: TempRepository) :
    ViewModel() {
    private val _uiState = MutableStateFlow(TempReturnUiState())
    val uiState = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        viewModelScope.launch { repository.revision.collect { refresh() } }
    }

    fun refresh() {
        updateState { it.copy(isLoading = true, errorMessage = null) }
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            operationResult { repository.returnOrders() }
                .onSuccess { result ->
                    updateState {
                        it.copy(
                            orders = result.data,
                            isLoading = false,
                            fromCache = result.fromCache
                        )
                    }
                }
                .onFailure { error ->
                    updateState {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.userMessage()
                        )
                    }
                }
        }
    }

    fun onQueryChange(value: String) {
        updateState { it.copy(query = value) }
    }

    fun onTabSelected(value: Int) {
        updateState { it.copy(selectedTab = value) }
    }

    fun onReturnerChange(value: String) {
        updateState { it.copy(returner = value, notificationMessage = null) }
    }

    fun onWorkOrderChange(value: String) {
        updateState { it.copy(workOrderNo = value, notificationMessage = null) }
    }

    fun onLocationChange(value: String) {
        updateState { it.copy(location = value, notificationMessage = null) }
    }

    fun onRemarkChange(value: String) {
        updateState { it.copy(remark = value, notificationMessage = null) }
    }

    fun selectOrder(orderNo: String) {
        updateState { state ->
            val order =
                state.orders.firstOrNull { it.orderNo == orderNo } ?: return@updateState state
            state.copy(
                selectedOrderNo = orderNo,
                returner = order.returnerEmployeeNo,
                workOrderNo = order.workOrderNo,
                location = order.location,
                remark = order.remark,
                notificationMessage = null
            )
        }
    }

    fun showList() {
        updateState {
            it.copy(
                selectedOrderNo = null,
                query = "",
                confirmationRfid = null,
                notificationMessage = null
            )
        }
    }


    fun requestConfirmation(rfid: String?) {
        updateState { state ->
            val error = when {
                rfid == null -> null
                state.returner.isBlank() -> "请填写归还人工号"
                state.location.isBlank() -> "请填写归还库位"
                else -> null
            }
            state.copy(
                confirmationRfid = if (error == null) rfid else null,
                notificationMessage = error,
                notificationId = if (error == null) state.notificationId else state.notificationId + 1
            )
        }
    }

    /**
     * 按已确认规则逐件联网归还，实际货位和库存结果以服务端响应为准。
     */
    fun confirmLine() {
        val state = _uiState.value
        val order = state.selectedOrder ?: return
        val line = order.lines.firstOrNull { it.tool.rfid == state.confirmationRfid } ?: return
        if (state.isSubmitting || line.returned || state.returner.isBlank() || state.location.isBlank()) return
        updateState { it.copy(isSubmitting = true, errorMessage = null) }
        val request = TempReturnConfirmRequest(
            order.orderNo,
            line.lineNo,
            line.tool.rfid,
            line.borrowOrderNo,
            state.returner.trim(),
            state.workOrderNo.trim(),
            state.location.trim(),
            state.remark.trim()
        )
        viewModelScope.launch {
            // 用户 2026-11-17 确认：临时归还逐件记录实际货位并直接恢复在库，不沿用文档中转待上架的冲突分支。
            operationResult { repository.returnTool(request) }
                .onSuccess { updated ->
                    updateState { current ->
                        current.copy(
                            orders = current.orders.map { if (it.orderNo == updated.orderNo) updated else it },
                            confirmationRfid = null,
                            isSubmitting = false,
                            fromCache = false,
                            notificationMessage = "已确认归还，器具已恢复在库",
                            notificationId = current.notificationId + 1
                        )
                    }
                }
                .onFailure { error ->
                    updateState {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = error.userMessage()
                        )
                    }
                }
        }
    }

    fun dismissNotification() {
        updateState { it.copy(notificationMessage = null) }
    }


    /**
     * 所有状态变更统一走这里，先改业务状态，再刷新筛选列表和派生状态。
     */
    private fun updateState(transform: (TempReturnUiState) -> TempReturnUiState) {
        _uiState.update { transform(it).withDerivedState() }
    }

    /**
     * 刷新查询条件、标签页对应的列表，以及所选单据和归还状态；展示字段由页面拼装。
     */
    private fun TempReturnUiState.withDerivedState(): TempReturnUiState {
        val visibleOrders = orders.filter { order ->
            (query.isBlank() || listOf(order.orderNo, order.workOrderNo, order.returner).plus(
                order.lines.flatMap { line -> listOf(line.borrower, line.borrowOrderNo) }
            ).any { value -> value.contains(query.trim(), true) }) &&
                    when (selectedTab) {
                        1 -> order.status == "待确认"
                        2 -> order.status == "已归还"
                        else -> true
                    }
        }
        val order = orders.firstOrNull { it.orderNo == selectedOrderNo }
        return copy(
            selectedOrder = order,
            visibleOrders = visibleOrders,
            hasReturned = order?.lines?.any { it.returned } ?: false
        )
    }
}
