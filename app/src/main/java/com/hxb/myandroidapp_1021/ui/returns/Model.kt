package com.hxb.myandroidapp_1021.ui.returns

import com.hxb.myandroidapp_1021.data.models.FilterType
import kotlinx.serialization.Serializable

@Serializable
enum class ReturnStatus(val label: String) {
    RETURNED("已归还"),
    UNRETURNED("待归还"),
    PARTIALLY_RETURNED("部分归还")
}

data class ReturnOrder(
    val orderNo: String,
    val status: ReturnStatus,
    val workshop: String,
    val returner: String,
    val pendingCount: Int,
    val returnedDate: String,
    val totalCount: Int,
    val returnedCount: Int
)

data class ReturnListUiState(
    val orders: List<ReturnOrder> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: FilterType = FilterType.ALL
) {
    val filteredOrders: List<ReturnOrder>
        get() = orders.filter { order ->
            val matchesStatus = when (selectedFilter) {
                FilterType.ALL -> true
                FilterType.PARTIAL -> order.status == ReturnStatus.PARTIALLY_RETURNED
                FilterType.UNRETURNED -> order.status == ReturnStatus.UNRETURNED
            }
            matchesStatus && order.orderNo.contains(searchQuery.trim(), ignoreCase = true)
        }
}
