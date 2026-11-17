package com.hxb.myandroidapp_1021.ui.pages.picking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.domain.picking.PickingRepository
import com.hxb.myandroidapp_1021.utils.filterDateError
import com.hxb.myandroidapp_1021.utils.operationResult
import com.hxb.myandroidapp_1021.utils.userMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@HiltViewModel
class PickingListViewModel @Inject constructor(private val repository: PickingRepository) :
    ViewModel() {
    private val _uiState = MutableStateFlow(PickingListUiState(isLoading = true))
    val uiState: StateFlow<PickingListUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        viewModelScope.launch { repository.revision.collect { refresh() } }
    }

    fun refresh() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            operationResult { repository.orders() }
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            orders = result.data,
                            fromCache = result.fromCache,
                            errorMessage = null
                        ).filterOrders()
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.userMessage()
                        )
                    }
                }
        }
    }

    fun onSearchQueryChange(value: String) {
        _uiState.update { it.copy(searchQuery = value).filterOrders() }
    }

    /**
     * 打开筛选抽屉时把已应用条件复制成草稿，取消或关闭不会影响列表。
     */
    fun showFilters() {
        _uiState.update { it.copy(showFilters = true, draftFilter = it.filter, filterError = null) }
    }

    fun dismissFilters() {
        _uiState.update { it.copy(showFilters = false, filterError = null) }
    }

    fun onFilterChange(filter: PickingListFilter) {
        _uiState.update { it.copy(draftFilter = filter, filterError = null) }
    }

    fun resetFilters() {
        _uiState.update { it.copy(draftFilter = PickingListFilter(), filterError = null) }
    }

    /**
     * 确认时校验日期范围并应用草稿，关闭抽屉不会修改已应用的条件。
     */
    fun applyFilters() {
        _uiState.update { state ->
            val error = filterDateError(state.draftFilter.startDate, state.draftFilter.endDate)
            if (error != null) state.copy(filterError = error)
            else state.copy(filter = state.draftFilter, showFilters = false, filterError = null)
                .filterOrders()
        }
    }

    /**
     * 选中单据用于关闭操作，再次点击同一单据取消选中。
     */
    fun selectOrder(orderNo: String) {
        _uiState.update { it.copy(selectedOrderNo = if (it.selectedOrderNo == orderNo) null else orderNo) }
    }

    /**
     * 已关闭或没有剩余未拣选明细的单据不允许关闭。
     */
    fun requestCloseOrder() {
        _uiState.update { state ->
            val order = state.orders.firstOrNull { it.orderNo == state.selectedOrderNo }
            if (order == null || order.status == PickingStatus.CLOSED || order.pendingCount <= 0) state
            else state.copy(closingOrderNo = order.orderNo)
        }
    }

    fun dismissCloseOrder() {
        _uiState.update { it.copy(closingOrderNo = null) }
    }

    /**
     * 关闭后保留已完成明细和历史记录，剩余明细不再处理，仅把单据状态改为已关闭。
     */
    fun confirmCloseOrder() {
        val orderNo = _uiState.value.closingOrderNo ?: return
        if (_uiState.value.isSubmitting) return
        _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
        viewModelScope.launch {
            operationResult { repository.close(orderNo) }
                .onSuccess { result ->
                    _uiState.update { state ->
                        state.copy(
                            orders = state.orders.map { if (it.orderNo == orderNo) result.order else it },
                            selectedOrderNo = null, closingOrderNo = null, isSubmitting = false
                        ).filterOrders()
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = error.userMessage()
                        )
                    }
                }
        }
    }

    /**
     * 本批次确认后累加该单已拣数量，剩余为 0 时状态置为已拣选，否则为部分拣选。
     */
    fun confirmPicked(orderNo: String, count: Int) {
        refresh()
    }

    /**
     * 在同一份原始数据上组合搜索、状态、车间/工班和计划日期筛选，加载完成时也保留用户已选条件。
     */
    private fun PickingListUiState.filterOrders(): PickingListUiState = copy(
        filteredOrders = orders.filter { order ->
            (filter.status == null || order.status == filter.status) &&
                    (filter.workshop == null || order.workshop == filter.workshop) &&
                    inDateRange(order.plannedDate) &&
                    order.orderNo.contains(searchQuery.trim(), ignoreCase = true)
        }
    )

    /**
     * 计划日期形如“2026-11-11 09:30”，只比较日期部分；空值不参与筛选。
     */
    private fun PickingListUiState.inDateRange(dateTime: String): Boolean {
        val date = dateTime.take(10)
        if (date.isBlank()) return true
        return (filter.startDate.isBlank() || date >= filter.startDate) &&
                (filter.endDate.isBlank() || date <= filter.endDate)
    }
}
