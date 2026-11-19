package com.hxb.myandroidapp_1021.feature.stockdetail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.feature.stockdetail.domain.model.StockDetailFilterState
import com.hxb.myandroidapp_1021.feature.stockdetail.domain.model.ToolCondition
import com.hxb.myandroidapp_1021.feature.stockdetail.domain.repository.StockDetailRepository
import com.hxb.myandroidapp_1021.utils.filterDateError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 库存明细列表：管理刷新、筛选草稿、详情抽屉和好坏件变更弹窗状态。
 */
@HiltViewModel
class StockDetailListViewModel @Inject constructor(
    private val stockDetailRepository: StockDetailRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(StockDetailListUiState())
    val uiState: StateFlow<StockDetailListUiState> = _uiState.asStateFlow()

    /**
     * 加载库存明细：首次进入由下拉刷新容器在页面完全展示后自动触发，手动下拉复用同一入口。
     */
    fun refresh() {
        if (_uiState.value.isRefreshing) return
        _uiState.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            val records = stockDetailRepository.loadRecords()
            _uiState.update { it.copy(isRefreshing = false, hasLoaded = true, records = records) }
        }
    }

    fun onSearchQueryChange(value: String) {
        _uiState.update { it.copy(searchQuery = value) }
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
     * 仅模拟好坏件变更，不推断库存状态、故障描述和可用数量的联动规则。
     */
    fun confirmConditionChange() {
        // TODO: 字段未确定：变更接口、权限、允许变更的库存状态及故障描述规则，真实接入后以响应为准。
        _uiState.update { state ->
            if (state.conditionRecord == null ||
                state.pendingCondition !in listOf(ToolCondition.GOOD.label, ToolCondition.BAD.label)
            ) {
                return@update state
            }
            state.copy(
                records = state.records.map {
                    if (it.rfid == state.conditionRfid) it.copy(condition = state.pendingCondition) else it
                },
                conditionRfid = null,
                pendingCondition = "",
                message = "模拟变更完成，尚未提交后端"
            )
        }
    }
}
