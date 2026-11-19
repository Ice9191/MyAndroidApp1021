package com.hxb.myandroidapp_1021.feature.tempborrow.presentation

import com.hxb.myandroidapp_1021.feature.tempborrow.domain.model.TempBorrowDetailStatus
import com.hxb.myandroidapp_1021.feature.tempborrow.domain.model.TempBorrowInventoryItem
import com.hxb.myandroidapp_1021.feature.tempborrow.domain.model.TempBorrowOrder
import com.hxb.myandroidapp_1021.feature.tempborrow.domain.model.TempBorrowStatus

/**
 * 临时借出页面状态：列表、新建表单与检索工器具页共用，页面只读取并通过事件回调修改。
 */
data class TempBorrowUiState(
    val orders: List<TempBorrowOrder> = emptyList(),
    val inventory: List<TempBorrowInventoryItem> = emptyList(),
    val selectedOrderNo: String? = null,
    val editing: Boolean = false,
    val selectedTab: Int = 0,
    val query: String = "",
    val toolQuery: String = "",
    val borrower: String = "",
    val workOrderNo: String = "",
    val workshop: String = "",
    val workGroup: String = "",
    val remark: String = "",
    val selectedRfids: Set<String> = emptySet(),
    val pickedRfids: Set<String> = emptySet(),
    val isSubmitting: Boolean = false,
    val notificationMessage: String? = null,
    val notificationId: Int = 0
) {
    val selectedOrder: TempBorrowOrder?
        get() = orders.firstOrNull { it.orderNo == selectedOrderNo }

    val visibleOrders: List<TempBorrowOrder>
        get() = orders.filter { order ->
            val keyword = query.trim()
            (keyword.isEmpty() || listOf(order.orderNo, order.workOrderNo, order.borrower).any {
                it.contains(keyword, ignoreCase = true)
            }) && when (selectedTab) {
                1 -> order.status == TempBorrowStatus.PENDING
                2 -> order.status == TempBorrowStatus.BORROWED
                else -> true
            }
        }

    /**
     * 检索页输入框为空时不返回任何器具，只有输入关键字后才按编码、名称或标签 ID 过滤。
     */
    val visibleInventory: List<TempBorrowInventoryItem>
        get() {
            val keyword = toolQuery.trim()
            if (keyword.isEmpty()) return emptyList()
            return inventory.filter { item ->
                listOf(item.rfid, item.itemCode, item.description).any {
                    it.contains(keyword, ignoreCase = true)
                }
            }
        }

    /**
     * 新建表单中已通过检索页添加、尚未保存到单据的器具。
     */
    val selectedTools: List<TempBorrowInventoryItem>
        get() = inventory.filter { it.rfid in selectedRfids }

    val borrowedCount: Int
        get() = selectedOrder?.details.orEmpty()
            .count { it.status == TempBorrowDetailStatus.BORROWED }
}
