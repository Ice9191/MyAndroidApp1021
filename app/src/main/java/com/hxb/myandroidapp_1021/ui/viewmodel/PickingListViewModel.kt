package com.hxb.myandroidapp_1021.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.data.models.PickingListUiState
import com.hxb.myandroidapp_1021.data.models.PickingOrder
import com.hxb.myandroidapp_1021.data.models.PickingStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
            // TODO：模拟网络请求延迟
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
                    "JC20261028001",
                    PickingStatus.PICKED,
                    "架大修一车间",
                    "张三",
                    3,
                    "2023-10-30 09:30"
                ),
                PickingOrder(
                    "JC20261028001",
                    PickingStatus.PICKED,
                    "架大修一车间",
                    "张三",
                    3,
                    "2023-10-30 09:30"
                ),
                PickingOrder(
                    "JC20261028001",
                    PickingStatus.PICKED,
                    "架大修一车间",
                    "张三",
                    3,
                    "2023-10-30 09:30"
                )
            )

            _uiState.value = PickingListUiState(
                isLoading = false,
                orders = mockOrders
            )
        }
    }
}