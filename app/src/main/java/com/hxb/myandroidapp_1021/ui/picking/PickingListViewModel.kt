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
                    "2023-10-30 09:30"
                ),
                PickingOrder(
                    "JC20261028002",
                    PickingStatus.PICKED,
                    "架大修二车间",
                    "李四",
                    2,
                    "2023-10-30 10:00"
                ),
                PickingOrder(
                    "JC20261028003",
                    PickingStatus.PICKED,
                    "架大修一车间",
                    "王五",
                    1,
                    "2023-10-30 10:30"
                )
            )

            // 加载成功后，原始列表和过滤列表都赋值为全量数据
            _uiState.update { oldState ->
                oldState.copy(
                    isLoading = false,
                    orders = mockOrders,
                    filteredOrders = mockOrders
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        val currentOrders = _uiState.value.orders

        // 过滤
        val filtered = if (query.isBlank()) {
            currentOrders
        } else {
            currentOrders.filter { it.orderNo.contains(query, ignoreCase = true) }
        }

        _uiState.update { oldState ->
            oldState.copy(
                searchQuery = query,
                filteredOrders = filtered
            )
        }
    }
}