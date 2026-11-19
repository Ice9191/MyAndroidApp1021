package com.hxb.myandroidapp_1021.feature.tempborrow.domain.model

/**
 * 依据《临时借出功能表间关系》建立临时借出业务模型，属性名用于应用内表达。
 * TODO: 字段未确定：接口字段名、状态编码、单号生成规则及时间格式需后端确认，不直接将属性名当作 DTO。
 */
enum class TempBorrowStatus(val label: String) {
    PENDING("待确认"),
    BORROWED("已借出")
}

enum class TempBorrowDetailStatus(val label: String) {
    PENDING("待确认"),
    BORROWED("已借出")
}

/**
 * 临时借出单，details 对应“包含”关系中的临时借出明细数组。
 * borrowTime 在确认借出时记录为最近一次操作时间，具体口径待后端确认。
 */
data class TempBorrowOrder(
    val orderNo: String,
    val status: TempBorrowStatus = TempBorrowStatus.PENDING,
    val workOrderNo: String,
    val borrower: String,
    val workshop: String,
    val workGroup: String,
    val borrowTime: String = "",
    val remark: String = "",
    val createdBy: String = "",
    val createdAt: String = "",
    val updatedBy: String = "",
    val updatedAt: String = "",
    val details: List<TempBorrowDetail> = emptyList()
) {
    val totalCount: Int get() = details.size
    val borrowedCount: Int get() = details.count { it.status == TempBorrowDetailStatus.BORROWED }
    val pendingCount: Int get() = totalCount - borrowedCount

    /**
     * 确认单件借出：只更新当前明细；首次确认后单据进入待确认，全部明细确认后变为已借出。
     */
    fun markBorrowed(lineNo: Int, operator: String, at: String): TempBorrowOrder {
        val updatedDetails = details.map { detail ->
            if (detail.lineNo != lineNo || detail.status == TempBorrowDetailStatus.BORROWED) {
                detail
            } else {
                detail.copy(
                    status = TempBorrowDetailStatus.BORROWED,
                    updatedBy = operator,
                    updatedAt = at
                )
            }
        }
        val allBorrowed = updatedDetails.isNotEmpty() &&
            updatedDetails.all { it.status == TempBorrowDetailStatus.BORROWED }
        return copy(
            status = if (allBorrowed) TempBorrowStatus.BORROWED else TempBorrowStatus.PENDING,
            borrowTime = at,
            updatedBy = operator,
            updatedAt = at,
            details = updatedDetails
        )
    }
}

data class TempBorrowDetail(
    val orderNo: String,
    val lineNo: Int,
    val status: TempBorrowDetailStatus = TempBorrowDetailStatus.PENDING,
    val rfid: String,
    val itemCode: String,
    val description: String,
    val unit: String,
    val createdBy: String = "",
    val createdAt: String = "",
    val updatedBy: String = "",
    val updatedAt: String = ""
)

/**
 * 新建临时借出单时提交的字段；器具范围由库存可借器具接口筛选。
 */
data class TempBorrowCreateRequest(
    val workOrderNo: String,
    val borrower: String,
    val workshop: String,
    val workGroup: String,
    val remark: String,
    val rfids: List<String>
)

data class TempBorrowConfirmRequest(
    val orderNo: String,
    val lineNo: Int,
    val rfid: String
)

/**
 * 新建单据时的可选库存器具，不属于临时借出单据表字段。
 * TODO: 字段未确定：库存接口字段名与可借过滤条件需后端确认。
 */
data class TempBorrowInventoryItem(
    val rfid: String,
    val itemCode: String,
    val description: String,
    val unit: String,
    val location: String
)
