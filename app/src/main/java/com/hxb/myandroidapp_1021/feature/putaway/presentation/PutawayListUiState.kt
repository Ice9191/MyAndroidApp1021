package com.hxb.myandroidapp_1021.feature.putaway.presentation

import com.hxb.myandroidapp_1021.feature.putaway.domain.model.PutawayOrder
import com.hxb.myandroidapp_1021.feature.putaway.domain.model.PutawayOrderStatus

/**
 * 列表状态筛选；全部表示不限制状态，其余与上架单的三个展示状态一一对应。
 */
enum class PutawayFilter(val status: PutawayOrderStatus? = null) {
    ALL(),
    PENDING(PutawayOrderStatus.PENDING),
    PARTIALLY(PutawayOrderStatus.PARTIALLY),
    COMPLETED(PutawayOrderStatus.COMPLETED);

    val label: String get() = status?.label ?: "全部"
}

/**
 * 上架列表页的查询、筛选、刷新和选中状态。
 */
data class PutawayListUiState(
    val isRefreshing: Boolean = false,
    val hasLoaded: Boolean = false,
    val orders: List<PutawayOrder> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: PutawayFilter = PutawayFilter.ALL,
    val selectedOrderNo: String? = null,
    val errorMessage: String? = null
) {
    val selectedOrder: PutawayOrder? get() = orders.firstOrNull { it.orderNo == selectedOrderNo }
    val filteredOrders: List<PutawayOrder>
        get() = orders.filter { order ->
            val matchesFilter =
                selectedFilter.status == null || order.status == selectedFilter.status
            val query = searchQuery.trim()
            matchesFilter && (order.orderNo.contains(query, true) || order.inboundOrderNo.contains(
                query,
                true
            ))
        }
}
