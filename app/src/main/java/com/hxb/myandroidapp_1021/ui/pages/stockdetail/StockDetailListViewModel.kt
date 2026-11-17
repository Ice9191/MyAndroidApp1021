package com.hxb.myandroidapp_1021.ui.pages.stockdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.domain.stockdetail.StockDetailRepository
import com.hxb.myandroidapp_1021.utils.filterDateError
import com.hxb.myandroidapp_1021.utils.operationResult
import com.hxb.myandroidapp_1021.utils.userMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class StockDetailListViewModel @Inject constructor(private val repository: StockDetailRepository) :
    ViewModel() {
    private val _uiState = MutableStateFlow(StockDetailListUiState(isLoading = true))
    val uiState: StateFlow<StockDetailListUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        viewModelScope.launch { repository.revision.collect { refresh() } }
    }

    fun refresh() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            operationResult { repository.records() }
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            records = result.data,
                            fromCache = result.fromCache,
                            errorMessage = null,
                            warehouse = result.data.firstOrNull()?.warehouse.orEmpty()
                        ).filterRecords()
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
        _uiState.update { it.copy(searchQuery = value).filterRecords() }
    }

    fun showFilters() {
        _uiState.update { it.copy(showFilters = true, draftFilter = it.filter, filterError = null) }
    }

    fun dismissFilters() {
        _uiState.update { it.copy(showFilters = false, filterError = null) }
    }

    fun onFilterChange(filter: StockDetailFilterState) {
        _uiState.update { it.copy(draftFilter = filter, filterError = null) }
    }

    fun resetFilters() {
        _uiState.update { it.copy(draftFilter = StockDetailFilterState(), filterError = null) }
    }

    /**
     * 确认时校验日期范围并应用草稿，关闭抽屉不会修改已应用的条件。
     */
    fun applyFilters() {
        _uiState.update { state ->
            val error = filterDateError(state.draftFilter.startDate, state.draftFilter.endDate)
            if (error != null) state.copy(filterError = error)
            else state.copy(filter = state.draftFilter, showFilters = false, filterError = null)
                .filterRecords()
        }
    }

    fun selectRecord(rfid: String) {
        _uiState.update { it.copy(selectedRfid = rfid) }
    }

    fun dismissDetail() {
        _uiState.update { it.copy(selectedRfid = null) }
    }

    fun openConditionChange(rfid: String) {
        _uiState.update { state ->
            val record = state.records.find { it.rfid == rfid } ?: return@update state
            state.copy(conditionRfid = rfid, pendingCondition = record.condition, message = null)
        }
    }

    fun onConditionChange(value: String) {
        _uiState.update { it.copy(pendingCondition = value) }
    }

    fun dismissConditionChange() {
        _uiState.update { it.copy(conditionRfid = null, pendingCondition = "") }
    }

    /**
     * 联网提交好坏件变更，库存状态、故障描述和可用数量以服务端结果为准。
     */
    fun confirmConditionChange() {
        val state = _uiState.value
        val record = state.conditionRecord ?: return
        if (state.isSubmitting || state.pendingCondition !in listOf("好件", "坏件")) return
        _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
        viewModelScope.launch {
            operationResult { repository.changeCondition(record.rfid, state.pendingCondition) }
                .onSuccess { updated ->
                    _uiState.update { current ->
                        current.copy(
                            records = current.records.map { if (it.rfid == updated.rfid) updated else it },
                            conditionRfid = null,
                            pendingCondition = "",
                            isSubmitting = false,
                            message = "变更成功"
                        ).filterRecords()
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = error.userMessage()
                        )
                    }
                }
        }
    }

    /**
     * 当前在已加载数据中组合筛选，保持原始库存记录不变。
     */
    private fun StockDetailListUiState.filterRecords(): StockDetailListUiState {
        return copy(filteredRecords = records.filter { record ->
            listOf(
                record.rfid,
                record.itemCode,
                record.description
            ).any { it.contains(searchQuery.trim(), true) } &&
                    (filter.status == null || record.inventoryStatus == filter.status.label) &&
                    (filter.condition == null || record.condition == filter.condition.label) &&
                    record.warehouse.contains(filter.warehouse.trim(), true) &&
                    record.location.orEmpty().contains(filter.location.trim(), true) &&
                    (filter.startDate.isBlank() || record.nextInspectionDate?.let { it >= filter.startDate } == true) &&
                    (filter.endDate.isBlank() || record.nextInspectionDate?.let { it <= filter.endDate } == true)
        })
    }
}
