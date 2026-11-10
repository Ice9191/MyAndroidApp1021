package com.hxb.myandroidapp_1021.ui.picking

data class PickingOrder(
    val orderNo: String,
    val status: PickingStatus,
    val workshop: String,
    val borrower: String,
    val pendingCount: Int,
    val creationDate: String
)

enum class PickingStatus(val label: String){
    PICKED("已拣选"),
    UNPICKED("未拣选"),
    PARTIALLY_PICKED("部分拣选"),
    CLOSED("已关闭")
}

data class PickingListUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val orders: List<PickingOrder> = emptyList(), // 原始数据
    val filteredOrders: List<PickingOrder> = emptyList()  // 过滤后的数据
)