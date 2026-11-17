package com.hxb.myandroidapp_1021.pages.putaway

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.domain.putaway.PutawayRepository
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
class PutawayListViewModel @Inject constructor(private val repository: PutawayRepository) :
    ViewModel() {
    private val _uiState = MutableStateFlow(PutawayListUiState(isLoading = true))
    val uiState: StateFlow<PutawayListUiState> = _uiState.asStateFlow()

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
                        )
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
        _uiState.update { it.copy(searchQuery = value) }
    }

    fun onFilterChange(value: PutawayFilter) {
        _uiState.update { it.copy(selectedFilter = value) }
    }

    fun selectOrder(order: PutawayOrder): Boolean {
        if (order.status == "异常") {
            _uiState.update { it.copy(errorMessage = "异常单据不能启动上架，请核对入库来源单据") }
            return false
        }
        _uiState.update { it.copy(selectedOrderNo = order.orderNo, errorMessage = null) }
        return true
    }

    /**
     * 文档要求按明细更新库存；不能等整单完成才入库。
     * 单件确认成功后使用服务端完整响应刷新单据，保留实际货位和完成进度。
     */
    fun updateOrder(order: PutawayOrder) {
        _uiState.update { state -> state.copy(orders = state.orders.map { if (it.orderNo == order.orderNo) order else it }) }
    }
}
