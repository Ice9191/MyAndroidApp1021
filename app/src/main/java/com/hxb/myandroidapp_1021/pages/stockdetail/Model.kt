package com.hxb.myandroidapp_1021.pages.stockdetail

/**
 * 单件库存业务信息，依据文档库存表关系图及库存明细正文。
 * TODO: 字段未确定：以下为本地语义命名，接口字段名、类型、可空性及状态编码待确认。
 */
data class StockDetail(
    val rfid: String,
    val itemCode: String,
    val description: String,
    val specification: String?,
    val unit: String,
    val category: String?,
    val inventoryStatus: String,
    val condition: String,
    val faultDescription: String?,
    val warehouse: String,
    val location: String?,
    val requiresInspection: Boolean?,
    val inspectionCycle: String?,
    val nextInspectionDate: String? = null
)

/**
 * 前端筛选条件，日期输入格式为 yyyy-MM-dd；接口日期字段及格式待确认。
 */
data class StockDetailFilterState(
    val status: String = "",
    val condition: String = "",
    val warehouse: String = "",
    val location: String = "",
    val startDate: String = "",
    val endDate: String = ""
)

/**
 * 前端查询及交互状态，不是后端库存表字段。
 */
data class StockDetailListUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val filter: StockDetailFilterState = StockDetailFilterState(),
    val draftFilter: StockDetailFilterState = StockDetailFilterState(),
    val filterError: String? = null,
    val warehouse: String = "",
    val records: List<StockDetail> = emptyList(),
    val filteredRecords: List<StockDetail> = emptyList(),
    val selectedRfid: String? = null,
    val conditionRfid: String? = null,
    val pendingCondition: String = "",
    val showFilters: Boolean = false,
    val message: String? = null
) {
    val selectedRecord: StockDetail? get() = records.find { it.rfid == selectedRfid }
    val conditionRecord: StockDetail? get() = records.find { it.rfid == conditionRfid }
}
