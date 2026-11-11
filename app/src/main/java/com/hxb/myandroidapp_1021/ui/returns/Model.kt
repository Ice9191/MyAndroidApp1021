package com.hxb.myandroidapp_1021.ui.returns

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

data class ReturnToolScanItem(
    val id: Int,
    val name: String,
    val itemCode: String,
    val rfid: String,
    val unit: String = "件",
    val isRead: Boolean = false,
    val readDate: String = ""
) {
    val statusLabel: String get() = if (isRead) "已读取" else "未读取"
}

data class ReturnScanUiState(
    val order: ReturnOrder,
    val tools: List<ReturnToolScanItem> = emptyList(),
    val isLoading: Boolean = false
) {
    val readList: List<ReturnToolScanItem> get() = tools.filter { it.isRead }
    val unreadList: List<ReturnToolScanItem> get() = tools.filterNot { it.isRead }
}

enum class FilterType(val label: String) {
    ALL("全部"),
    PARTIAL("部分归还"),
    UNRETURNED("未归还")
}
