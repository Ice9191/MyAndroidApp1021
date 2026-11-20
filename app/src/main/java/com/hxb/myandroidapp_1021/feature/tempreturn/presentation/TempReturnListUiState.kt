package com.hxb.myandroidapp_1021.feature.tempreturn.presentation

import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnOrder
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnStatus

/**
 * 临时归还列表页状态；归还单由临时借出自动生成，页面只读取并通过事件回调修改。
 */
data class TempReturnListUiState(
    val orders: List<TempReturnOrder> = emptyList(),
    val selectedOrderNo: String? = null,
    val selectedTab: Int = 0,
    val query: String = "",
    val putawayRfids: Set<String> = emptySet()
) {
    val selectedOrder: TempReturnOrder?
        get() = orders.firstOrNull { it.orderNo == selectedOrderNo }

    /**
     * 按借出单号、工单号或借用人模糊搜索，并叠加“待确认/已归还”状态页签。
     */
    val visibleOrders: List<TempReturnOrder>
        get() = orders.filter { order ->
            val keyword = query.trim()
            (keyword.isEmpty() || listOf(
                order.borrowOrderNo,
                order.workOrderNo,
                order.borrower
            ).any { it.contains(keyword, ignoreCase = true) }) && when (selectedTab) {
                1 -> order.status == TempReturnStatus.PENDING
                2 -> order.status == TempReturnStatus.RETURNED
                else -> true
            }
        }
}
