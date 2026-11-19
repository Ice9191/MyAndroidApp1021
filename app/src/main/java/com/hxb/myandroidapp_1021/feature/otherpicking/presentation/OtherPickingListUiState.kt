package com.hxb.myandroidapp_1021.feature.otherpicking.presentation

import com.hxb.myandroidapp_1021.feature.otherpicking.domain.model.OtherPickingOrder
import com.hxb.myandroidapp_1021.feature.otherpicking.domain.model.PickingStatus
import com.hxb.myandroidapp_1021.feature.otherpicking.domain.model.SourceType

/**
 * 其他拣选筛选条件；null 表示全部来源或全部状态，只用于页面筛选，不写入业务模型。
 */
data class OtherPickingFilter(
    val sourceType: SourceType? = null,
    val status: PickingStatus? = null
)

/**
 * 其他拣选列表状态；isRefreshing、hasLoaded、showFilters 等只供页面使用。
 */
data class OtherPickingListUiState(
    val isRefreshing: Boolean = false,
    val hasLoaded: Boolean = false,
    val searchQuery: String = "",
    val orders: List<OtherPickingOrder> = emptyList(),
    val filter: OtherPickingFilter = OtherPickingFilter(),
    val draftFilter: OtherPickingFilter = OtherPickingFilter(),
    val showFilters: Boolean = false,
    val toastMessage: String? = null,
    val toastId: Int = 0
) {
    val filteredOrders: List<OtherPickingOrder>
        get() = orders.filter { order ->
            val matchesQuery = searchQuery.isBlank() ||
                order.pickingOrderNo.contains(searchQuery, ignoreCase = true) ||
                order.upstreamNo.contains(searchQuery, ignoreCase = true)
            val matchesSource = filter.sourceType == null || order.upstreamType == filter.sourceType
            val matchesStatus = filter.status == null || order.status == filter.status
            matchesQuery && matchesSource && matchesStatus
        }
}
