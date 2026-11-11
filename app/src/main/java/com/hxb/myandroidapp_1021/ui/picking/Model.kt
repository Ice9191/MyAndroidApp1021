package com.hxb.myandroidapp_1021.ui.picking

data class PickingOrder(
    val orderNo: String,
    val status: PickingStatus,
    val workshop: String,
    val borrower: String,
    val pendingCount: Int,
    val plannedDate: String,
    val totalCount: Int,
    val pickedCount: Int
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
    val filteredOrders: List<PickingOrder> = emptyList(), // 过滤后的数据
    val selectedStatus: PickingStatus? = null
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

data class PickingConfirmUiState(
    val isLoading: Boolean = true,           // 初始加载中
    val orderNo: String = "",
    val orderStatus: PickingStatus = PickingStatus.UNPICKED,
    val borrower: String = "",
    val workshop: String = "",
    val handler: String = "",
    val outboundTime: String = "",
    val employeeId: String = "",             // 输入框工号
    val readTools: List<PickingToolScanItem> = emptyList(), // 已读取的工具列表
    val unreadCount: Int = 0,                // 未读取数量，用于顶部展示
    val isSubmitting: Boolean = false,       // 提交中状态
    val errorMessage: String? = null         // 错误提示
)