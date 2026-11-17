package com.hxb.myandroidapp_1021.ui.pages.returns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.domain.returns.ReturnRepository
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
class ReturnListViewModel @Inject constructor(private val repository: ReturnRepository) :
    ViewModel() {
    private val _uiState = MutableStateFlow(ReturnListUiState(isLoading = true))
    val uiState: StateFlow<ReturnListUiState> = _uiState.asStateFlow()

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

    fun onStatusChange(value: ReturnStatus?) {
        _uiState.update { it.copy(selectedStatus = value).filterOrders() }
    }

    /**
     * 逐条上架成功后同步列表单据的已上架数量与状态。
     */
    fun updatePutawayProgress(orderNo: String, doneCount: Int) {
        refresh()
    }

    /**
     * 在同一份原始数据上组合搜索和状态筛选，加载完成时也保留用户已选条件。
     */
    private fun ReturnListUiState.filterOrders(): ReturnListUiState = copy(
        filteredOrders = orders.filter { order ->
            (selectedStatus == null || order.status == selectedStatus) &&
                    order.orderNo.contains(searchQuery.trim(), ignoreCase = true)
        }
    )
}
