package com.hxb.myandroidapp_1021.ui.putaway

import kotlinx.serialization.Serializable

/**
 * 文档“上架单功能表间关系”：入库单号、上架单号、单据状态、上架人、上架时间。
 * details 是接口聚合的上架明细，不是上架单表的物理列。
 * TODO: 字段未确定，PDA 接口的 JSON 名称和状态代码需与后端确认。
 */
@Serializable
data class PutawayOrder(
    val inboundOrderNo: String,
    val orderNo: String,
    val status: String,
    val operator: String,
    val putawayTime: String,
    val details: List<PutawayDetail>
) {
    val completedCount: Int get() = details.count { it.isCompleted }
    val isCompleted: Boolean get() = details.isNotEmpty() && completedCount == details.size
}

/**
 * 明细字段对应文档关系图；inventoryStatus 来自关联库存明细，不是上架明细表新增列。
 * TODO: 字段未确定，行号类型、状态代码及库存联查字段名需后端确认。
 */
@Serializable
data class PutawayDetail(
    val orderNo: String,
    val inboundLineNo: String,
    val lineNo: String,
    val status: String,
    val batchNo: String,
    val rfid: String,
    val itemCode: String,
    val description: String,
    val unit: String,
    val warehouse: String,
    val location: String,
    val inventoryStatus: String
) {
    val isCompleted: Boolean get() = status == "已上架"
}

/**
 * 单件上架请求；操作人暂使用模拟登录人员。
 * TODO: 字段未确定，实际接口可能需要人员、仓库和货位主键，需后端确认。
 */
@Serializable
data class PutawayRequest(
    val orderNo: String,
    val lineNo: String,
    val rfid: String,
    val location: String,
    val operator: String
)

/**
 * 货位校验响应，仓库/货位业务含义来自文档基础档案。
 * TODO: 字段未确定，货位查询接口和主键待确认。
 */
@Serializable
data class PutawayLocation(val code: String, val warehouse: String)

enum class PutawayFilter(val label: String) {
    ALL("全部"),
    UNFINISHED("未完成"),
    COMPLETED("已完成")
}

data class PutawayListUiState(
    val isLoading: Boolean = false,
    val orders: List<PutawayOrder> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: PutawayFilter = PutawayFilter.ALL,
    val selectedOrderNo: String? = null,
    val errorMessage: String? = null
) {
    val selectedOrder: PutawayOrder? get() = orders.firstOrNull { it.orderNo == selectedOrderNo }
    val filteredOrders: List<PutawayOrder>
        get() = orders.filter { order ->
            val matchesFilter = when (selectedFilter) {
                PutawayFilter.ALL -> true
                PutawayFilter.UNFINISHED -> !order.isCompleted
                PutawayFilter.COMPLETED -> order.isCompleted
            }
            val query = searchQuery.trim()
            matchesFilter && (order.orderNo.contains(query, true) || order.inboundOrderNo.contains(query, true))
        }
}

data class PutawayConfirmUiState(
    val isLoading: Boolean = true,
    val operator: String = "",
    val rfid: String = "",
    val locationCode: String = "",
    val boundLocation: String = "",
    val selectedLineNo: String? = null,
    val selectedTab: Int = 0,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val message: String? = null
)
