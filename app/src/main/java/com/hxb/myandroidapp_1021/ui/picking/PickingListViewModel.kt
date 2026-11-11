package com.hxb.myandroidapp_1021.ui.picking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class PickingListViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PickingListUiState(isLoading = true))
    val uiState: StateFlow<PickingListUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            delay(500.milliseconds)
            val mockOrders = listOf(
                PickingOrder(
                    orderNo = "JC20261028001",
                    status = PickingStatus.PICKED,
                    workshop = "架大修一车间",
                    borrower = "张三",
                    pendingCount = 0,
                    plannedDate = "2026-11-11 09:30",
                    totalCount = 3,
                    pickedCount = 3,
                    workOrderNo = "GD20261111001",
                    createdBy = "材料员",
                    createdAt = "2026-11-11 08:00"
                ),
                PickingOrder(
                    orderNo = "JC20261028002",
                    status = PickingStatus.PARTIALLY_PICKED,
                    workshop = "架大修二车间",
                    borrower = "李四",
                    pendingCount = 1,
                    plannedDate = "2026-11-11 10:00",
                    totalCount = 2,
                    pickedCount = 1,
                    workOrderNo = "GD20261111001",
                    createdBy = "材料员",
                    createdAt = "2026-11-11 08:00"
                ),
                PickingOrder(
                    orderNo = "JC20261028003",
                    status = PickingStatus.UNPICKED,
                    workshop = "架大修一车间",
                    borrower = "王五",
                    pendingCount = 1,
                    plannedDate = "2026-11-11 10:30",
                    totalCount = 1,
                    pickedCount = 0,
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

    fun onStatusChange(value: PickingStatus?) {
        _uiState.update { it.copy(selectedStatus = value).filterOrders() }
    }

    /**
     * 在同一份原始数据上组合搜索和状态筛选，加载完成时也保留用户已选条件。
     */
    private fun PickingListUiState.filterOrders(): PickingListUiState = copy(
        filteredOrders = orders.filter { order ->
            (selectedStatus == null || order.status == selectedStatus) &&
                    order.orderNo.contains(searchQuery.trim(), ignoreCase = true)
        }
    )
}
