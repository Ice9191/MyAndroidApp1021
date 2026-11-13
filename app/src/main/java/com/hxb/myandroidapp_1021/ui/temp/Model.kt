package com.hxb.myandroidapp_1021.ui.temp

/**
 * 依据临时借出、临时归还的表关系图；属性是应用内业务命名。
 * TODO: 字段未确定：接口字段名、状态编码及单号生成规则需后端确认，不将这些属性直接用作 DTO。
 */
data class TempTool(
    val rfid: String,
    val itemCode: String,
    val name: String,
    val unit: String,
    val location: String
)

data class TempBorrowLine(
    val lineNo: Int,
    val tool: TempTool,
    val borrowed: Boolean = false,
    val borrowedAt: String = ""
)

data class TempBorrowOrder(
    val orderNo: String,
    val workOrderNo: String,
    val borrower: String,
    val workshop: String,
    val team: String,
    val remark: String,
    val createdBy: String,
    val createdAt: String,
    val lines: List<TempBorrowLine>
) {
    // 依据单据状态图使用“草稿”，不沿用正文中与图不一致的“待确认”单据初态。
    val status: String get() = if (lines.isNotEmpty() && lines.all { it.borrowed }) "已借出" else "草稿"
}

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
    val allPicked: Boolean = false
)

data class TempReturnLine(
    val lineNo: Int,
    val tool: TempTool,
    val borrowOrderNo: String,
    val borrower: String,
    val borrowedAt: String,
    val returned: Boolean = false,
    val returnedAt: String = ""
)

data class TempReturnOrder(
    val orderNo: String,
    val workOrderNo: String,
    val returner: String,
    val location: String,
    val remark: String,
    val createdBy: String,
    val createdAt: String,
    val lines: List<TempReturnLine>
) {
    val status: String get() = if (lines.isNotEmpty() && lines.all { it.returned }) "已归还" else "待确认"
}

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
    val hasReturned: Boolean = false
)
