package com.hxb.myandroidapp_1021.feature.otherputaway.presentation

import com.hxb.myandroidapp_1021.feature.otherputaway.domain.model.OtherPutawayOrder
import com.hxb.myandroidapp_1021.feature.otherputaway.domain.model.PutawayStatus
import com.hxb.myandroidapp_1021.feature.otherputaway.domain.model.SourceType

/**
 * 其他上架筛选条件；null 表示全部来源或全部状态，只用于页面筛选，不写入业务模型。
 */
data class OtherPutawayFilter(
    val upstreamType: SourceType? = null,
    val status: PutawayStatus? = null
)

/**
 * 其他上架列表状态；isRefreshing、hasLoaded、showFilters 等只供页面使用。
 */
data class OtherPutawayListUiState(
    val isRefreshing: Boolean = false,
    val hasLoaded: Boolean = false,
    val searchQuery: String = "",
    val orders: List<OtherPutawayOrder> = emptyList(),
    val filter: OtherPutawayFilter = OtherPutawayFilter(),
    val draftFilter: OtherPutawayFilter = OtherPutawayFilter(),
    val showFilters: Boolean = false
) {
    val filteredOrders: List<OtherPutawayOrder>
        get() = orders.filter { order ->
            val matchesQuery = searchQuery.isBlank() ||
                order.putawayOrderNo.contains(searchQuery, ignoreCase = true) ||
                order.upstreamNo.contains(searchQuery, ignoreCase = true)
            val matchesSource = filter.upstreamType == null || order.upstreamType == filter.upstreamType
            val matchesStatus = filter.status == null || order.status == filter.status
            matchesQuery && matchesSource && matchesStatus
        }
}
