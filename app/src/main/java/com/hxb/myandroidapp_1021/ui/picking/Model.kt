package com.hxb.myandroidapp_1021.ui.picking

data class PickingOrder(
    val orderNo: String,
    val status: PickingStatus,
    val workshop: String,
    val borrower: String,
    val pendingCount: Int,
    val creationDate: String
)

enum class PickingStatus(val label: String) {
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

data class PickingToolScanItem(
    val id: Int,
    val name: String = "",
    val status: ToolScanItemStatus = ToolScanItemStatus.UNPICKED,
    val itemCode: String = "",
    val rfid: String,
    val recommendedLocation: String,
    val unit: String
)

enum class ToolScanItemStatus(val label: String) {
    PICKED("已拣选"),
    UNPICKED("未拣选")
}

data class PickingScanUiState(
    val orderNo: String = "",
    val orderStatus: PickingStatus = PickingStatus.UNPICKED,
    val workshop: String = "",
    val borrower: String = "",
    val tools: List<PickingToolScanItem> = emptyList(),
    val isLoading: Boolean = false
) {
    val readList: List<PickingToolScanItem>
        get() = tools.filter { it.status == ToolScanItemStatus.PICKED }

    val unreadList: List<PickingToolScanItem>
        get() = tools.filter { it.status == ToolScanItemStatus.UNPICKED }
}