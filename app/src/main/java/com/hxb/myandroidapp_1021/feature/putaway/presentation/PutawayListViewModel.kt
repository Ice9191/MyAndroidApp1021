package com.hxb.myandroidapp_1021.feature.putaway.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.feature.putaway.domain.model.PutawayDetailStatus
import com.hxb.myandroidapp_1021.feature.putaway.domain.model.PutawayOrder
import com.hxb.myandroidapp_1021.feature.putaway.domain.model.PutawayOrderStatus
import com.hxb.myandroidapp_1021.feature.putaway.domain.model.PutawayRequest
import com.hxb.myandroidapp_1021.feature.putaway.domain.repository.PutawayRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PutawayListViewModel @Inject constructor(
    private val putawayRepository: PutawayRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PutawayListUiState())
    val uiState: StateFlow<PutawayListUiState> = _uiState.asStateFlow()

    /**
     * 首次进入和下拉刷新都走这里；加载期间保留已有列表，完成后统一替换。
     */
    fun refresh() {
        if (_uiState.value.isRefreshing) return
        _uiState.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            val orders = putawayRepository.loadOrders()
            _uiState.update {
                it.copy(isRefreshing = false, hasLoaded = true, orders = orders)
            }
        }
    }

    fun onSearchQueryChange(value: String) {
        _uiState.update { it.copy(searchQuery = value) }
    }

    fun onFilterChange(value: PutawayFilter) {
        _uiState.update { it.copy(selectedFilter = value) }
    }

    fun selectOrder(order: PutawayOrder): Boolean {
        if (order.status == PutawayOrderStatus.ABNORMAL) {
            _uiState.update { it.copy(errorMessage = "异常单据不能启动上架，请核对入库来源单据") }
            return false
        }
        _uiState.update { it.copy(selectedOrderNo = order.orderNo, errorMessage = null) }
        return true
    }

    /**
     * 文档要求按明细更新库存；不能等整单完成才入库。
     * 模拟成功后只更新当前明细，再汇总单据进度，返回列表时保留已完成结果。
     */
    fun confirmItem(request: PutawayRequest): Boolean {
        val order = _uiState.value.selectedOrder ?: return false
        val detail =
            order.details.firstOrNull { it.lineNo == request.lineNo && it.rfid == request.rfid }
                ?: return false
        if (order.orderNo != request.orderNo || detail.isCompleted || request.location.isBlank() || request.operator.isBlank()) return false
        val putawayTime =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        _uiState.update { state ->
            state.copy(orders = state.orders.map { current ->
                if (current.orderNo != request.orderNo) current else {
                    val details = current.details.map { item ->
                        if (item.lineNo != request.lineNo) item else item.copy(
                            status = PutawayDetailStatus.COMPLETED
                        )
                    }
                    current.copy(
                        details = details,
                        status = if (details.all { it.isCompleted }) {
                            PutawayOrderStatus.COMPLETED
                        } else {
                            PutawayOrderStatus.PARTIALLY
                        },
                        operator = request.operator,
                        putawayTime = putawayTime
                    )
                }
            })
        }
        return true
    }
}
