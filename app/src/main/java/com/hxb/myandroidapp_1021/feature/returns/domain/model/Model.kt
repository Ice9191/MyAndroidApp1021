package com.hxb.myandroidapp_1021.feature.returns.domain.model

/**
 * 归还上架单状态，对应文档“待上架/部分上架/已上架”。
 * TODO: 字段未确定：后端状态编码未提供，正式枚举值及映射需与后端确认。
 */
enum class ReturnOrderStatus(val label: String) {
    PENDING("待上架"),
    PARTIALLY("部分上架"),
    COMPLETED("已上架")
}

/**
 * 归还上架单明细状态；ER 图未列出状态字段，按业务要求保留，新建明细默认待上架。
 * 损坏、待送检明细不参与上架：没有系统推荐的实际货位，也不能确认上架。
 * TODO: 字段未确定：后端明细状态编码未提供，正式枚举值及映射需与后端确认。
 */
enum class ReturnDetailStatus(val label: String) {
    PENDING("待上架"),
    COMPLETED("已上架"),
    DAMAGED("损坏"),
    PENDING_INSPECTION("待送检")
}

/**
 * 归还上架单，字段对应上架单 ER 图；details 是上架单明细数组，新建单据默认待上架。
 * location 是单据货位（上架目标货位），明细的实际货位由逐条确认写入。
 * TODO: 字段未确定，接口 JSON 名称、时间格式和状态代码需与后端确认。
 */
data class ReturnOrder(
    val orderNo: String,
    val returnOrderNo: String,
    val workOrderNo: String,
    val createdBy: String,
    val createdAt: String,
    val updatedBy: String,
    val updatedAt: String,
    val putawayBy: String = "",
    val putawayAt: String = "",
    val location: String = "",
    val status: ReturnOrderStatus = ReturnOrderStatus.PENDING,
    val details: List<ReturnDetail>
) {
    val totalCount: Int get() = details.size
    val putawayCount: Int get() = details.count { it.isPutaway }
    val pendingCount: Int get() = details.count { it.canPutaway }

    /**
     * 确认单条明细上架：只写入该明细的实际货位和操作人/时间，再按明细完成情况重算单据状态。
     * 单据统计上架人/上架时间时记录最后一次确认，部分上架时也能看到最近操作人。
     */
    fun markPutaway(lineNo: Int, locationCode: String, operator: String, at: String): ReturnOrder {
        val updatedDetails = details.map { detail ->
            // 只有待上架明细可以写入实际货位并转为已上架
            if (detail.lineNo == lineNo && detail.canPutaway) {
                detail.copy(
                    actualLocation = locationCode,
                    status = ReturnDetailStatus.COMPLETED,
                    updatedBy = operator,
                    updatedAt = at
                )
            } else {
                detail
            }
        }
        return copy(
            details = updatedDetails,
            putawayBy = operator,
            putawayAt = at,
            updatedBy = operator,
            updatedAt = at,
            status = updatedDetails.toReturnOrderStatus()
        )
    }
}

/**
 * 归还上架单明细，字段对应上架单明细 ER 图，并保留业务要求的上架状态（初始待上架）。
 * actualLocation 初始为系统推荐货位，待上架明细不为空；仅损坏、待送检明细为空，且不可上架。
 * TODO: 字段未确定，行号类型、接口 JSON 名称和时间格式需与后端确认。
 */
data class ReturnDetail(
    val orderNo: String,
    val lineNo: Int,
    val rfid: String,
    val itemCode: String,
    val description: String,
    val unit: String,
    val createdBy: String,
    val createdAt: String,
    val updatedBy: String,
    val updatedAt: String,
    /**
     * 实际货位：待上架明细初始为系统推荐货位；确认上架时需重新扫描或输入货位码，不直接采用推荐值。
     * 损坏、待送检明细保持为空。
     */
    val actualLocation: String = "",
    val status: ReturnDetailStatus = ReturnDetailStatus.PENDING
) {
    val isPutaway: Boolean get() = status == ReturnDetailStatus.COMPLETED

    /**
     * 是否可上架：仅待上架明细可上架；损坏、待送检明细保持无实际货位且不可确认。
     */
    val canPutaway: Boolean get() = status == ReturnDetailStatus.PENDING
}

/**
 * 单件上架请求；操作人由后端从登录信息获取。
 * TODO: 字段未确定，实际接口可能需要仓库和货位主键，需后端确认。
 */
data class ReturnConfirmRequest(
    val orderNo: String,
    val lineNo: Int,
    val rfid: String,
    val locationCode: String
)

/**
 * 按明细完成情况汇总单据状态：全部已上架为已上架，任意一条已上架为部分上架，否则待上架。
 */
internal fun List<ReturnDetail>.toReturnOrderStatus(): ReturnOrderStatus {
    val completedCount = count { it.isPutaway }
    return when {
        isEmpty() || completedCount == 0 -> ReturnOrderStatus.PENDING
        completedCount == size -> ReturnOrderStatus.COMPLETED
        else -> ReturnOrderStatus.PARTIALLY
    }
}
