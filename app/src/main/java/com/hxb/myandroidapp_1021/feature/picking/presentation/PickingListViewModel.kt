package com.hxb.myandroidapp_1021.feature.picking.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.feature.picking.domain.model.PickingStatus
import com.hxb.myandroidapp_1021.feature.picking.domain.repository.PickingRepository
import com.hxb.myandroidapp_1021.utils.filterDateError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PickingListViewModel @Inject constructor(
    private val pickingRepository: PickingRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PickingListUiState())
    val uiState: StateFlow<PickingListUiState> = _uiState.asStateFlow()

    /**
     * 首次进入由 PullToRefresh 自动触发一次下拉刷新，加载期间保留已有列表，完成后统一替换。
     */
    fun refresh() {
        if (_uiState.value.isRefreshing) return
        _uiState.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            val orders = pickingRepository.loadOrders()
            _uiState.update {
                it.copy(isRefreshing = false, hasLoaded = true, orders = orders)
            }
        }
    }

    fun onSearchQueryChange(value: String) {
        _uiState.update { it.copy(searchQuery = value) }
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
        }
    }

    /**
     * 选中单据用于关闭操作，再次点击同一单据取消选中。
     */
    fun selectOrder(orderNo: String) {
        _uiState.update {
            it.copy(selectedOrderNo = if (it.selectedOrderNo == orderNo) null else orderNo)
        }
    }

    /**
     * 已关闭或没有剩余未拣选明细的单据不允许关闭。
     */
    fun requestCloseOrder() {
        _uiState.update { state ->
            val order = state.selectedOrder
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
        _uiState.update { state ->
            val orderNo = state.closingOrderNo ?: return@update state
            state.copy(
                orders = state.orders.map {
                    if (it.orderNo == orderNo) it.copy(status = PickingStatus.CLOSED) else it
                },
                selectedOrderNo = null,
                closingOrderNo = null
            )
        }
    }

    /**
     * 本批次确认后按 RFID 将对应明细置为已拣选，单据状态由剩余明细重新汇总。
     */
    fun confirmPicked(orderNo: String, rfids: List<String>) {
        _uiState.update { state ->
            state.copy(
                orders = state.orders.map { order ->
                    if (order.orderNo == orderNo) order.markPicked(rfids.toSet()) else order
                },
                toastMessage = "拣选成功",
                toastId = state.toastId + 1
            )
        }
    }

    /** 提示条展示结束或手动消失后清空消息。 */
    fun dismissToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
