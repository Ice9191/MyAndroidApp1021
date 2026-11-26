package com.hxb.myandroidapp_1021.feature.picking.presentation

import com.hxb.myandroidapp_1021.feature.picking.domain.model.PickingOrder
import com.hxb.myandroidapp_1021.feature.picking.domain.model.PickingStatus

/**
 * 前端列表筛选条件；工班对应单据的 workGroup 字段。
 * TODO: 字段未确定：查询接口的工班、计划开始及结束日期参数名与日期格式待后端确认。
 */
data class PickingListFilter(
    val status: PickingStatus? = null,
    val workGroup: String? = null,
    val startDate: String = "",
    val endDate: String = ""
)

/**
 * 前端列表状态，不作为数据库字段或后端响应对象；filter 为已应用条件，draftFilter 为抽屉草稿。
 */
data class PickingListUiState(
    val isRefreshing: Boolean = false,
    val hasLoaded: Boolean = false,
    val searchQuery: String = "",
    val orders: List<PickingOrder> = emptyList(),
    val filter: PickingListFilter = PickingListFilter(),
    val draftFilter: PickingListFilter = PickingListFilter(),
    val showFilters: Boolean = false,
    val filterError: String? = null,
    val showWorkGroupQuery: Boolean = false,
    val workGroupQuery: String = "",
    val selectedOrderNo: String? = null,
    val closingOrderNo: String? = null,
    val toastMessage: String? = null,
    val toastId: Int = 0
) {
    val selectedOrder: PickingOrder? get() = orders.firstOrNull { it.orderNo == selectedOrderNo }

    /**
     * 工班查询弹窗候选：取自当前单据列表，去重排序后按弹窗关键字过滤；未选时由输入框占位文案提示选择。
     */
    val workGroupQueryOptions: List<String>
        get() {
            val keyword = workGroupQuery.trim()
            return orders.map { it.workGroup }
                .filter { it.isNotBlank() }
                .distinct()
                .sorted()
                .filter { keyword.isEmpty() || it.contains(keyword, ignoreCase = true) }
        }

    /**
     * 在同一份原始数据上组合搜索、状态、工班和计划日期筛选。
     */
    val filteredOrders: List<PickingOrder>
        get() {
            val query = searchQuery.trim()
            return orders.filter { order ->
                (filter.status == null || order.status == filter.status) &&
                        (filter.workGroup == null || order.workGroup == filter.workGroup) &&
                        inDateRange(order.plannedDate) &&
                        (query.isEmpty() ||
                                order.orderNo.contains(query, ignoreCase = true) ||
                                order.borrowOrderNo.contains(query, ignoreCase = true))
            }
        }

    /**
     * 计划日期形如“2026-11-11 09:30”，只比较日期部分；空值不参与筛选。
     */
    private fun inDateRange(dateTime: String): Boolean {
        val date = dateTime.take(10)
        if (date.isBlank()) return true
        return (filter.startDate.isBlank() || date >= filter.startDate) &&
                (filter.endDate.isBlank() || date <= filter.endDate)
    }
}
