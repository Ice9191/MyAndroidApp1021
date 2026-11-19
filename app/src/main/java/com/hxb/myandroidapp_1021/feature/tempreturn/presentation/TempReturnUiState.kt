package com.hxb.myandroidapp_1021.feature.tempreturn.presentation

import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnOrder
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnPendingTool
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnStatus

/**
 * 临时归还页面状态：列表、新建表单与检索待还器具页共用，页面只读取并通过事件回调修改。
 */
data class TempReturnUiState(
    val orders: List<TempReturnOrder> = emptyList(),
    val pendingTools: List<TempReturnPendingTool> = emptyList(),
    val selectedOrderNo: String? = null,
    val editing: Boolean = false,
    val selectedTab: Int = 0,
    val query: String = "",
    val toolQuery: String = "",
    val returner: String = "",
    val workOrderNo: String = "",
    val returnLocation: String = "",
    val remark: String = "",
    val selectedRfids: Set<String> = emptySet(),
    val putawayRfids: Set<String> = emptySet(),
    val isSubmitting: Boolean = false,
    val notificationMessage: String? = null,
    val notificationId: Int = 0
) {
    val selectedOrder: TempReturnOrder?
        get() = orders.firstOrNull { it.orderNo == selectedOrderNo }

    val visibleOrders: List<TempReturnOrder>
        get() = orders.filter { order ->
            val keyword = query.trim()
            (keyword.isEmpty() || listOf(
                order.orderNo,
                order.workOrderNo,
                order.returner,
                order.returnLocation
            ).any { it.contains(keyword, ignoreCase = true) }) && when (selectedTab) {
                1 -> order.status == TempReturnStatus.PENDING
                2 -> order.status == TempReturnStatus.RETURNED
                else -> true
            }
        }

    /**
     * 检索页输入框为空时不返回任何器具，输入后按借用人、工单号或借用单号过滤。
     */
    val visiblePendingTools: List<TempReturnPendingTool>
        get() {
            val keyword = toolQuery.trim()
            if (keyword.isEmpty()) return emptyList()
            return pendingTools.filter { tool ->
                listOf(tool.borrower, tool.workOrderNo, tool.borrowOrderNo).any {
                    it.contains(keyword, ignoreCase = true)
                }
            }
        }

    val selectedTools: List<TempReturnPendingTool>
        get() = pendingTools.filter { it.rfid in selectedRfids }
}
