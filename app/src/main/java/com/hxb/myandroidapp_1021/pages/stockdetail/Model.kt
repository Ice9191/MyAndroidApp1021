package com.hxb.myandroidapp_1021.pages.stockdetail

import com.hxb.myandroidapp_1021.domain.stockdetail.StockDetail as DomainStockDetail

/**
 * 单件库存业务信息，依据文档库存表关系图及库存明细正文。
 */
typealias StockDetail = DomainStockDetail

/**
 * 库存状态筛选项；接口状态编码及完整枚举待后端确认。
 */
enum class InventoryStatus(val label: String) {
    IN_STOCK("在库"),
    PENDING_PICK("待拣选"),
    PREPARED("已备货"),
    BORROWED("已借出"),
    PENDING_PUTAWAY("待上架"),
    INSPECTING("已送检"),
    REPAIRING("已送修")
}

/**
 * 好坏件状态筛选项；接口编码待后端确认。
 */
enum class ToolCondition(val label: String) {
    GOOD("好件"),
    BAD("坏件")
}

/**
 * 前端筛选条件，日期输入格式为 yyyy-MM-dd；接口日期字段及格式待确认。
 * 筛选值为 null 表示“全部”。
 */
data class StockDetailFilterState(
    val status: InventoryStatus? = null,
    val condition: ToolCondition? = null,
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
    val message: String? = null,
    val isSubmitting: Boolean = false,
    val fromCache: Boolean = false,
    val errorMessage: String? = null
) {
    val selectedRecord: StockDetail? get() = records.find { it.rfid == selectedRfid }
    val conditionRecord: StockDetail? get() = records.find { it.rfid == conditionRfid }
}
