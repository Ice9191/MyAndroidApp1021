package com.hxb.myandroidapp_1021.feature.returns.presentation

import com.hxb.myandroidapp_1021.feature.returns.domain.model.ReturnOrder
import com.hxb.myandroidapp_1021.feature.returns.domain.model.ReturnOrderStatus

/**
 * 列表状态筛选；全部表示不限制状态，其余与上架单的三个展示状态一一对应。
 */
enum class ReturnFilter(val status: ReturnOrderStatus? = null) {
    ALL(),
    PENDING(ReturnOrderStatus.PENDING),
    PARTIALLY(ReturnOrderStatus.PARTIALLY),
    COMPLETED(ReturnOrderStatus.COMPLETED);

    val label: String get() = status?.label ?: "全部"
}

/**
 * 归还上架列表状态；orders 是接口原始数据，filteredOrders 是搜索和筛选后的展示数据。
 */
data class ReturnListUiState(
    val isRefreshing: Boolean = false,
    val hasLoaded: Boolean = false,
    val searchQuery: String = "",
    val orders: List<ReturnOrder> = emptyList(),
    val selectedFilter: ReturnFilter = ReturnFilter.ALL
) {
    val filteredOrders: List<ReturnOrder>
        get() = orders.filter { order ->
            val matchesFilter =
                selectedFilter.status == null || order.status == selectedFilter.status
            val query = searchQuery.trim()
            val matchesQuery = query.isEmpty() ||
                order.orderNo.contains(query, true) ||
                order.returnOrderNo.contains(query, true)
            matchesFilter && matchesQuery
        }
}
