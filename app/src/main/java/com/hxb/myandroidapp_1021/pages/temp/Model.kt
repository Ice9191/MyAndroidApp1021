package com.hxb.myandroidapp_1021.pages.temp

import com.hxb.myandroidapp_1021.domain.temp.TempBorrowLine as DomainTempBorrowLine
import com.hxb.myandroidapp_1021.domain.temp.TempBorrowOrder as DomainTempBorrowOrder
import com.hxb.myandroidapp_1021.domain.temp.TempReturnLine as DomainTempReturnLine
import com.hxb.myandroidapp_1021.domain.temp.TempReturnOrder as DomainTempReturnOrder
import com.hxb.myandroidapp_1021.domain.temp.TempTool as DomainTempTool

/**
 * 依据临时借出、临时归还的表关系图；属性是应用内业务命名。
 */
typealias TempTool = DomainTempTool

typealias TempBorrowLine = DomainTempBorrowLine

typealias TempBorrowOrder = DomainTempBorrowOrder

/**
 * 临时借出页面状态；列表、所选单据等由 ViewModel 刷新后写入，页面只读取。
 */
data class TempBorrowUiState(
    val orders: List<TempBorrowOrder> = emptyList(),
    val inventory: List<TempTool> = emptyList(),
    val selectedOrderNo: String? = null,
    val editing: Boolean = false,
    val selectedTab: Int = 0,
    val query: String = "",
    val borrower: String = "",
    val workOrderNo: String = "",
    val workshop: String = "",
    val team: String = "",
    val remark: String = "",
    val selectedRfids: Set<String> = emptySet(),
    val pickedRfids: Set<String> = emptySet(),
    val confirmationRfid: String? = null,
    val notificationMessage: String? = null,
    val notificationId: Int = 0,
    val selectedOrder: TempBorrowOrder? = null,
    val visibleOrders: List<TempBorrowOrder> = emptyList(),
    val visibleTools: List<TempTool> = emptyList(),
    val allPicked: Boolean = false,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val fromCache: Boolean = false,
    val errorMessage: String? = null
)

typealias TempReturnLine = DomainTempReturnLine

typealias TempReturnOrder = DomainTempReturnOrder

/**
 * 临时归还页面状态；列表、所选单据等由 ViewModel 刷新后写入，页面只读取。
 */
data class TempReturnUiState(
    val orders: List<TempReturnOrder> = emptyList(),
    val selectedOrderNo: String? = null,
    val selectedTab: Int = 0,
    val query: String = "",
    val returner: String = "",
    val workOrderNo: String = "",
    val location: String = "",
    val remark: String = "",
    val confirmationRfid: String? = null,
    val notificationMessage: String? = null,
    val notificationId: Int = 0,
    val selectedOrder: TempReturnOrder? = null,
    val visibleOrders: List<TempReturnOrder> = emptyList(),
    val hasReturned: Boolean = false,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val fromCache: Boolean = false,
    val errorMessage: String? = null
)
