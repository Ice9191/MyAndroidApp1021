package com.hxb.myandroidapp_1021.feature.stockdetail.domain.model

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
 * 库存状态筛选项；接口状态编码及完整枚举待后端确认。
 * TODO: 字段未确定：正式编码与接口取值需与后端核对，当前 label 仅用于界面展示与本地匹配。
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
 * status、condition、category 为 null 表示“全部”，其余文本条件为空表示“不限”。
 */
data class StockDetailFilterState(
    val status: InventoryStatus? = null,
    val condition: ToolCondition? = null,
    val warehouse: String = "",
    val location: String = "",
    val category: String? = null,
    val specification: String = "",
    val startDate: String = "",
    val endDate: String = ""
)
