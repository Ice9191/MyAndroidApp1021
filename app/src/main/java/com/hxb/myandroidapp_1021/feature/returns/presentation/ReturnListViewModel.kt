package com.hxb.myandroidapp_1021.feature.returns.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.feature.returns.domain.model.ReturnOrder
import com.hxb.myandroidapp_1021.feature.returns.domain.repository.ReturnRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 归还上架列表：与拣选列表保持同一套刷新、搜索和筛选流程。
 */
@HiltViewModel
class ReturnListViewModel @Inject constructor(
    private val returnRepository: ReturnRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReturnListUiState())
    val uiState: StateFlow<ReturnListUiState> = _uiState.asStateFlow()

    /**
     * 首次进入由 PullToRefresh 在页面完全展示后自动触发一次下拉刷新，加载期间保留已有列表，完成后统一替换。
     */
    fun refresh() {
        if (_uiState.value.isRefreshing) return
        _uiState.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            val orders = returnRepository.loadOrders()
            _uiState.update { state ->
                state.copy(
                    isRefreshing = false,
                    hasLoaded = true,
                    orders = orders
                )
            }
        }
    }

    fun onSearchQueryChange(value: String) {
        _uiState.update { it.copy(searchQuery = value) }
    }

    fun onFilterChange(value: ReturnFilter) {
        _uiState.update { it.copy(selectedFilter = value) }
    }

    /**
     * 扫描页确认单条上架后，用返回的整张单据同步列表中的进度、上架人/时间和状态。
     */
    fun updateOrder(order: ReturnOrder) {
        _uiState.update { state ->
            state.copy(
                orders = state.orders.map { current ->
                    if (current.orderNo == order.orderNo) order else current
                }
            )
        }
    }
}
