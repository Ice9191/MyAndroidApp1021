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

// 表单、搜索、弹窗等仅供本模块展示，不属于后端业务字段。
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
    val message: String? = null
) {
    val selectedOrder: TempBorrowOrder? get() = orders.firstOrNull { it.orderNo == selectedOrderNo }
    val visibleOrders: List<TempBorrowOrder>
        get() = orders.filter {
            (query.isBlank() || listOf(
                it.orderNo,
                it.workOrderNo,
                it.borrower
            ).any { value -> value.contains(query.trim(), true) }) &&
                    when (selectedTab) {
                        1 -> it.status == "草稿"; 2 -> it.status == "已借出"; else -> true
                    }
        }
    val visibleTools: List<TempTool>
        get() = inventory.filter {
            query.isBlank() || listOf(it.rfid, it.itemCode, it.name).any { value ->
                value.contains(
                    query.trim(),
                    true
                )
            }
        }
}

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
    val message: String? = null
) {
    val selectedOrder: TempReturnOrder? get() = orders.firstOrNull { it.orderNo == selectedOrderNo }
    val visibleOrders: List<TempReturnOrder>
        get() = orders.filter {
            (query.isBlank() || listOf(it.orderNo, it.workOrderNo, it.returner).plus(
                it.lines.flatMap { line -> listOf(line.borrower, line.borrowOrderNo) }
            ).any { value -> value.contains(query.trim(), true) }) &&
                    when (selectedTab) {
                        1 -> it.status == "待确认"; 2 -> it.status == "已归还"; else -> true
                    }
        }
}

