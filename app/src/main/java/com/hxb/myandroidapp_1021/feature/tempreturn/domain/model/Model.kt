package com.hxb.myandroidapp_1021.feature.tempreturn.domain.model

/**
 * 依据《临时归还功能表间关系》建立临时归还业务模型，属性名用于应用内表达。
 * TODO: 字段未确定：接口字段名、状态编码、单号生成规则及时间格式需后端确认，不直接将属性名当作 DTO。
 */
enum class TempReturnStatus(val label: String) {
    PENDING("待确认"),
    RETURNED("已归还")
}

enum class TempReturnDetailStatus(val label: String) {
    PENDING("待确认"),
    RETURNED("已归还")
}

/**
 * 临时归还单，details 对应“包含”关系中的临时归还明细数组。
 * returnTime 在确认归还时记录为最近一次操作时间，具体口径待后端确认。
 */
data class TempReturnOrder(
    val orderNo: String,
    val status: TempReturnStatus = TempReturnStatus.PENDING,
    val workOrderNo: String = "",
    val returner: String = "",
    val returnTime: String = "",
    val returnLocation: String = "",
    val remark: String = "",
    val createdBy: String = "",
    val createdAt: String = "",
    val updatedBy: String = "",
    val updatedAt: String = "",
    val details: List<TempReturnDetail> = emptyList()
) {
    val totalCount: Int get() = details.size
    val returnedCount: Int get() = details.count { it.status == TempReturnDetailStatus.RETURNED }
    val pendingCount: Int get() = totalCount - returnedCount

    /**
     * 确认单件归还：同步登记归还人、工单号、归还库位与备注，整单状态按全部明细是否已归还重算。
     */
    fun markReturned(
        lineNo: Int,
        operator: String,
        at: String,
        returner: String,
        workOrderNo: String,
        returnLocation: String,
        remark: String
    ): TempReturnOrder {
        val updatedDetails = details.map { detail ->
            if (detail.lineNo != lineNo || detail.status == TempReturnDetailStatus.RETURNED) {
                detail
            } else {
                detail.copy(
                    status = TempReturnDetailStatus.RETURNED,
                    updatedBy = operator,
                    updatedAt = at
                )
            }
        }
        val allReturned = updatedDetails.isNotEmpty() &&
            updatedDetails.all { it.status == TempReturnDetailStatus.RETURNED }
        return copy(
            status = if (allReturned) TempReturnStatus.RETURNED else TempReturnStatus.PENDING,
            workOrderNo = workOrderNo,
            returner = returner,
            returnTime = at,
            returnLocation = returnLocation,
            remark = remark,
            updatedBy = operator,
            updatedAt = at,
            details = updatedDetails
        )
    }
}

data class TempReturnDetail(
    val orderNo: String,
    val lineNo: Int,
    val status: TempReturnDetailStatus = TempReturnDetailStatus.PENDING,
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
 * 确认单件归还时提交的登记字段；归还库位对应归还单的“归还库位”。
 */
data class TempReturnConfirmRequest(
    val orderNo: String,
    val lineNo: Int,
    val rfid: String,
    val returner: String,
    val workOrderNo: String,
    val returnLocation: String,
    val remark: String
)

/**
 * 新建归还单时从待还器具带入的明细字段。
 * TODO: 字段未确定：接口字段名需后端确认。
 */
data class TempReturnCreateDetail(
    val rfid: String,
    val itemCode: String,
    val description: String,
    val unit: String
)

/**
 * 新建归还单请求；明细由检索待还器具后选中的器具生成。
 */
data class TempReturnCreateRequest(
    val returner: String,
    val workOrderNo: String,
    val returnLocation: String,
    val remark: String,
    val details: List<TempReturnCreateDetail>
)

/**
 * 可检索的待还器具；来源于临时借出已确认的明细，仅用于展示和生成归还明细，不单独持久化。
 * TODO: 字段未确定：接口字段名需后端确认。
 */
data class TempReturnPendingTool(
    val borrowOrderNo: String,
    val workOrderNo: String,
    val borrower: String,
    val rfid: String,
    val itemCode: String,
    val description: String,
    val unit: String
)
