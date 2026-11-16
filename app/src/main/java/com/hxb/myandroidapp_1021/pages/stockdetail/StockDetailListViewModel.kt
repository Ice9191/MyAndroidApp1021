package com.hxb.myandroidapp_1021.pages.stockdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class StockDetailListViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(StockDetailListUiState(isLoading = true))
    val uiState: StateFlow<StockDetailListUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            delay(500.milliseconds)
            // TODO: 字段未确定：库存查询接口及锁定的默认仓库来源；暂用同一仓库的模拟单件记录。
            val mockWarehouse = "二号库"
            val mockRecords = listOf(
                StockDetail(
                    "E2801160600002094A5B6C92",
                    "270200000003",
                    "液压压接钳 16-300mm²",
                    "16-300mm²",
                    "把",
                    "工器具",
                    "已备货",
                    "好件",
                    null,
                    mockWarehouse,
                    "C-02-01-05",
                    true,
                    "12个月"
                ),
                StockDetail(
                    "R-00000141",
                    "270000000001",
                    "活动扳手",
                    "12寸",
                    "把",
                    "工器具",
                    "在库",
                    "好件",
                    null,
                    mockWarehouse,
                    "A-01-03",
                    false,
                    null
                ),
                StockDetail(
                    "R-00000143",
                    "280000000001",
                    "数显游标卡尺",
                    "0-150mm",
                    "把",
                    "量具",
                    "在库",
                    "坏件",
                    "显示异常，待核查",
                    mockWarehouse,
                    "B-02-04",
                    true,
                    "12个月"
                )
            )
            _uiState.update {
                it.copy(
                    isLoading = false,
                    warehouse = mockWarehouse,
                    records = mockRecords
                ).filterRecords()
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
            val filter = state.draftFilter
            val dates = listOf(filter.startDate, filter.endDate)
            val format = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.ROOT)
                .apply { isLenient = false }
            val validDates = dates.all { value ->
                value.isBlank() || (value.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) &&
                        runCatching { format.parse(value) }.isSuccess)
            }
            when {
                !validDates -> state.copy(filterError = "日期请按 yyyy-MM-dd 输入有效日期")
                filter.startDate.isNotBlank() && filter.endDate.isNotBlank() && filter.startDate > filter.endDate ->
                    state.copy(filterError = "开始日期不能晚于结束日期")

                else -> state.copy(filter = filter, showFilters = false, filterError = null)
                    .filterRecords()
            }
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
            if (state.conditionRecord == null || state.pendingCondition !in listOf(
                    "好件",
                    "坏件"
                )
            ) return@update state
            state.copy(
                records = state.records.map { if (it.rfid == state.conditionRfid) it.copy(condition = state.pendingCondition) else it },
                conditionRfid = null,
                pendingCondition = "",
                message = "模拟变更完成，尚未提交后端"
            ).filterRecords()
        }
    }

    /**
     * 当前在已加载数据中组合筛选，保持原始库存记录不变。
     */
    private fun StockDetailListUiState.filterRecords(): StockDetailListUiState {
        // TODO: 字段未确定：后端组合查询、分页、数量口径及下次送检日期字段；当前仅按 yyyy-MM-dd 本地比较。
        return copy(filteredRecords = records.filter { record ->
            listOf(
                record.rfid,
                record.itemCode,
                record.description
            ).any { it.contains(searchQuery.trim(), true) } &&
                    (filter.status.isBlank() || record.inventoryStatus == filter.status) &&
                    (filter.condition.isBlank() || record.condition == filter.condition) &&
                    record.warehouse.contains(filter.warehouse.trim(), true) &&
                    record.location.orEmpty().contains(filter.location.trim(), true) &&
                    (filter.startDate.isBlank() || record.nextInspectionDate?.let { it >= filter.startDate } == true) &&
                    (filter.endDate.isBlank() || record.nextInspectionDate?.let { it <= filter.endDate } == true)
        })
    }
}