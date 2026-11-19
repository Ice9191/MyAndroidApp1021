package com.hxb.myandroidapp_1021.feature.stockdetail.presentation

import com.hxb.myandroidapp_1021.feature.stockdetail.domain.model.StockDetail
import com.hxb.myandroidapp_1021.feature.stockdetail.domain.model.StockDetailFilterState

/**
 * 库存明细列表状态：刷新、搜索、筛选草稿、明细记录及抽屉/弹窗开关。
 * filteredRecords 由搜索条件和已应用筛选派生，不写回状态，保持原始记录不变。
 */
data class StockDetailListUiState(
    val isRefreshing: Boolean = false,
    val hasLoaded: Boolean = false,
    val searchQuery: String = "",
    val filter: StockDetailFilterState = StockDetailFilterState(),
    val draftFilter: StockDetailFilterState = StockDetailFilterState(),
    val filterError: String? = null,
    val records: List<StockDetail> = emptyList(),
    val selectedRfid: String? = null,
    val conditionRfid: String? = null,
    val pendingCondition: String = "",
    val showFilters: Boolean = false,
    val message: String? = null
) {
    // TODO: 字段未确定：后端组合查询、分页、数量口径及下次送检日期字段；当前仅按 yyyy-MM-dd 本地比较。
    val filteredRecords: List<StockDetail>
        get() = records.filter { record ->
            listOf(
                record.rfid,
                record.itemCode,
                record.description
            ).any { it.contains(searchQuery.trim(), true) } &&
                    (filter.status == null || record.inventoryStatus == filter.status.label) &&
                    (filter.condition == null || record.condition == filter.condition.label) &&
                    record.warehouse.contains(filter.warehouse.trim(), true) &&
                    record.location.orEmpty().contains(filter.location.trim(), true) &&
                    (filter.category == null || record.category == filter.category) &&
                    record.specification.orEmpty().contains(filter.specification.trim(), true) &&
                    (filter.startDate.isBlank() || record.nextInspectionDate?.let { it >= filter.startDate } == true) &&
                    (filter.endDate.isBlank() || record.nextInspectionDate?.let { it <= filter.endDate } == true)
        }

    /**
     * 物资分类下拉选项：当前从已加载记录去重提取。
     * TODO: 字段未确定：接入后端分页后应从完整分类数据获取，避免下拉框只包含当前页出现过的分类。
     */
    val categoryOptions: List<String>
        get() = records.mapNotNull { it.category?.takeIf { category -> category.isNotBlank() } }.distinct()

    val selectedRecord: StockDetail? get() = records.find { it.rfid == selectedRfid }
    val conditionRecord: StockDetail? get() = records.find { it.rfid == conditionRfid }
}
