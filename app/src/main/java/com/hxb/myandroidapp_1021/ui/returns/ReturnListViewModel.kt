package com.hxb.myandroidapp_1021.ui.returns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class ReturnListViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ReturnListUiState(isLoading = true))
    val uiState: StateFlow<ReturnListUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            delay(500.milliseconds)
            val mockOrders = listOf(
                ReturnOrder(
                    orderNo = "GH20261111001",
                    status = ReturnStatus.PARTIALLY_RETURNED,
                    workshop = "检修一班",
                    returner = "张伟",
                    pendingCount = 3,
                    returnedDate = "2026-11-11 09:30",
                    totalCount = 5,
                    returnedCount = 2,
                    workOrderNo = "GD20261111001",
                    createdBy = "材料员",
                    createdAt = "2026-11-11 08:00"
                ),
                ReturnOrder(
                    orderNo = "GH20261111002",
                    status = ReturnStatus.UNRETURNED,
                    workshop = "维修二班",
                    returner = "李明",
                    pendingCount = 3,
                    returnedDate = "",
                    totalCount = 3,
                    returnedCount = 0,
                    workOrderNo = "GD20261111001",
                    createdBy = "材料员",
                    createdAt = "2026-11-11 08:00"
                ),
                ReturnOrder(
                    orderNo = "GH20261111003",
                    status = ReturnStatus.RETURNED,
                    workshop = "电气班组",
                    returner = "王健",
                    pendingCount = 0,
                    returnedDate = "2026-11-11 10:30",
                    totalCount = 2,
                    returnedCount = 2,
                    workOrderNo = "GD20261111001",
                    createdBy = "材料员",
                    createdAt = "2026-11-11 08:00"
                )
            )
            _uiState.update { state ->
                state.copy(isLoading = false, orders = mockOrders).filterOrders()
            }
        }
    }

    fun onSearchQueryChange(value: String) {
        _uiState.update { it.copy(searchQuery = value).filterOrders() }
    }

    fun onStatusChange(value: ReturnStatus?) {
        _uiState.update { it.copy(selectedStatus = value).filterOrders() }
    }

    /** 在同一份原始数据上组合搜索和状态筛选，加载完成时也保留用户已选条件。 */
    private fun ReturnListUiState.filterOrders(): ReturnListUiState = copy(
        filteredOrders = orders.filter { order ->
            (selectedStatus == null || order.status == selectedStatus) &&
                    order.orderNo.contains(searchQuery.trim(), ignoreCase = true)
        }
    )
}
