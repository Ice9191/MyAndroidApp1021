package com.hxb.myandroidapp_1021.pages.putaway

import com.hxb.myandroidapp_1021.domain.putaway.PutawayDetail as DomainPutawayDetail
import com.hxb.myandroidapp_1021.domain.putaway.PutawayLocation as DomainPutawayLocation
import com.hxb.myandroidapp_1021.domain.putaway.PutawayOrder as DomainPutawayOrder
import com.hxb.myandroidapp_1021.domain.putaway.PutawayRequest as DomainPutawayRequest


/**
 * 文档“上架单功能表间关系”：入库单号、上架单号、单据状态、上架人、上架时间。
 * details 是接口聚合的上架明细，不是上架单表的物理列。
 */
typealias PutawayOrder = DomainPutawayOrder

/**
 * 明细字段对应文档关系图；inventoryStatus 来自关联库存明细，不是上架明细表新增列。
 */
typealias PutawayDetail = DomainPutawayDetail

/**
 * 单件上架请求；操作人由服务端根据当前登录凭证记录。
 */
typealias PutawayRequest = DomainPutawayRequest

/**
 * 货位校验响应，仓库/货位业务含义来自文档基础档案。
 */
typealias PutawayLocation = DomainPutawayLocation

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
    val errorMessage: String? = null,
    val isSubmitting: Boolean = false,
    val fromCache: Boolean = false
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
            matchesFilter && (order.orderNo.contains(query, true) || order.inboundOrderNo.contains(
                query,
                true
            ))
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
    val notificationMessage: String? = null,
    val notificationId: Int = 0
)
