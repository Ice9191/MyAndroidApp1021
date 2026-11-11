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
        loadOrders()
    }

    private fun loadOrders() {
        viewModelScope.launch {
            // 模拟网络请求延迟
            delay(800.milliseconds)

            val mockOrders = listOf(
                PickingOrder(
                    "JC20261028001",
                    PickingStatus.PICKED,
                    "架大修一车间",
                    "张三",
                    3,
                    "2023-10-30 09:30",
                    totalCount = 3,
                    pickedCount = 3
                ),
                PickingOrder(
                    "JC20261028002",
                    PickingStatus.PICKED,
                    "架大修二车间",
                    "李四",
                    2,
                    "2023-10-30 10:00",
                    totalCount = 2,
                    pickedCount = 2
                ),
                PickingOrder(
                    "JC20261028003",
                    PickingStatus.PICKED,
                    "架大修一车间",
                    "王五",
                    1,
                    "2023-10-30 10:30",
                    totalCount = 1,
                    pickedCount = 1
                )
            )

            // 加载成功后，按当前搜索词和状态筛选。
            _uiState.update { oldState ->
                oldState.copy(
                    isLoading = false,
                    orders = mockOrders
                ).filterOrders()
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query).filterOrders() }
    }

    fun onStatusChange(status: PickingStatus?) {
        _uiState.update { it.copy(selectedStatus = status).filterOrders() }
    }

    private fun PickingListUiState.filterOrders(): PickingListUiState = copy(
        filteredOrders = orders.filter { order ->
            (selectedStatus == null || order.status == selectedStatus) &&
                    order.orderNo.contains(searchQuery.trim(), ignoreCase = true)
        }
    )
}
