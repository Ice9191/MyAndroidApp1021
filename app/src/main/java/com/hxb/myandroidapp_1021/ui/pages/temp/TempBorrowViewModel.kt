package com.hxb.myandroidapp_1021.ui.pages.temp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.domain.temp.*
import com.hxb.myandroidapp_1021.utils.operationResult
import com.hxb.myandroidapp_1021.utils.userMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@HiltViewModel
class TempBorrowViewModel @Inject constructor(private val repository: TempRepository) :
    ViewModel() {
    private val _uiState = MutableStateFlow(TempBorrowUiState())
    val uiState = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        viewModelScope.launch { repository.revision.collect { refresh() } }
    }

    fun refresh() {
        updateState { it.copy(isLoading = true, errorMessage = null) }
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            operationResult {
                val orders = repository.borrowOrders()
                val inventory = repository.availableTools()
                orders to inventory
            }.onSuccess { (orders, inventory) ->
                updateState { state ->
                    state.copy(
                        orders = orders.data, inventory = inventory.data, isLoading = false,
                        pickedRfids = orders.data.flatMap { it.lines }.filter { it.picked }
                            .map { it.tool.rfid }.toSet(),
                        fromCache = orders.fromCache || inventory.fromCache
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

    fun onBorrowerChange(value: String) {
        updateState { it.copy(borrower = value, notificationMessage = null) }
    }

    fun onWorkOrderChange(value: String) {
        updateState { it.copy(workOrderNo = value, notificationMessage = null) }
    }

    fun onWorkshopChange(value: String) {
        updateState { it.copy(workshop = value) }
    }

    fun onTeamChange(value: String) {
        updateState { it.copy(team = value) }
    }

    fun onRemarkChange(value: String) {
        updateState { it.copy(remark = value) }
    }

    private var requestId = UUID.randomUUID().toString()

    fun startNewOrder() {
        requestId = UUID.randomUUID().toString()
        updateState {
            it.copy(
                editing = true,
                selectedOrderNo = null,
                query = "",
                borrower = "",
                workOrderNo = "",
                workshop = "",
                team = "",
                remark = "",
                selectedRfids = emptySet(),
                notificationMessage = null
            )
        }
    }

    fun selectOrder(orderNo: String) {
        updateState {
            it.copy(
                selectedOrderNo = orderNo,
                editing = false,
                notificationMessage = null
            )
        }
    }

    fun showList() {
        updateState {
            it.copy(
                selectedOrderNo = null,
                editing = false,
                query = "",
                confirmationRfid = null,
                notificationMessage = null
            )
        }
    }

    fun toggleTool(rfid: String) {
        updateState { state ->
            if (state.inventory.none { it.rfid == rfid }) state else state.copy(
                selectedRfids = if (rfid in state.selectedRfids) state.selectedRfids - rfid else state.selectedRfids + rfid,
                notificationMessage = null
            )
        }
    }

    /**
     * 先联网保存器具清单，单号和关联拣选任务由服务端生成。
     */
    fun saveOrder() {
        val state = _uiState.value
        if (state.isSubmitting) return
        val error = when {
            state.borrower.isBlank() -> "请填写借用人工号"
            state.selectedRfids.isEmpty() -> "请至少选择一件在库器具"
            else -> null
        }
        if (error != null) {
            updateState { it.copy(errorMessage = error) }; return
        }
        updateState { it.copy(isSubmitting = true, errorMessage = null) }
        val request = TempBorrowRequest(
            requestId,
            state.borrower.trim(),
            state.workOrderNo.trim(),
            state.workshop.trim(),
            state.team.trim(),
            state.remark.trim(),
            state.selectedRfids.toList()
        )
        viewModelScope.launch {
            operationResult { repository.create(request) }
                .onSuccess { order ->
                    updateState { current ->
                        current.copy(
                            orders = current.orders.filterNot { it.orderNo == order.orderNo } + order,
                            selectedOrderNo = order.orderNo,
                            editing = false,
                            selectedRfids = emptySet(),
                            query = "",
                            isSubmitting = false,
                            fromCache = false
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

    fun requestConfirmation(rfid: String?) {
        updateState { it.copy(confirmationRfid = rfid, notificationMessage = null) }
    }

    /**
     * 按用户确认的顺序，整单拣选完成后才允许逐条借出，重复确认不改变结果。
     */
    fun confirmLine() {
        val state = _uiState.value
        val order = state.selectedOrder ?: return
        val line = order.lines.firstOrNull { it.tool.rfid == state.confirmationRfid } ?: return
        if (state.isSubmitting || line.borrowed) return
        if (!state.allPicked) {
            updateState { it.copy(errorMessage = "请先完成本单全部器具的 PDA 拣选") }; return
        }
        updateState { it.copy(isSubmitting = true, errorMessage = null) }
        viewModelScope.launch {
            operationResult {
                repository.borrow(
                    TempBorrowConfirmRequest(
                        order.orderNo,
                        line.lineNo,
                        line.tool.rfid
                    )
                )
            }
                .onSuccess { updated ->
                    updateState { current ->
                        current.copy(
                            orders = current.orders.map { if (it.orderNo == updated.orderNo) updated else it },
                            confirmationRfid = null,
                            isSubmitting = false,
                            fromCache = false,
                            notificationMessage = "已确认借出，服务端已生成待还记录",
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
    private fun updateState(transform: (TempBorrowUiState) -> TempBorrowUiState) {
        _uiState.update { transform(it).withDerivedState() }
    }

    /**
     * 刷新查询条件、标签页对应的列表，以及所选单据和整单拣选进度；展示字段由页面拼装。
     */
    private fun TempBorrowUiState.withDerivedState(): TempBorrowUiState {
        val visibleOrders = orders.filter { order ->
            (query.isBlank() || listOf(
                order.orderNo,
                order.workOrderNo,
                order.borrower
            ).any { value -> value.contains(query.trim(), true) }) &&
                    when (selectedTab) {
                        1 -> order.status == "草稿"
                        2 -> order.status == "已借出"
                        else -> true
                    }
        }
        val visibleTools = inventory.filter { tool ->
            query.isBlank() || listOf(tool.rfid, tool.itemCode, tool.name).any { value ->
                value.contains(
                    query.trim(),
                    true
                )
            }
        }
        val order = orders.firstOrNull { it.orderNo == selectedOrderNo }
        return copy(
            selectedOrder = order,
            visibleOrders = visibleOrders,
            visibleTools = visibleTools,
            allPicked = order?.lines?.let { it.isNotEmpty() && it.all { line -> line.picked } }
                ?: false
        )
    }
}
