package com.hxb.myandroidapp_1021.feature.otherputaway.domain.model

/**
 * 其他上架任务的来源类型，对应文档“临时归还、送检归还、送修归还、报废归还、退货归还”。
 * TODO: 字段未确定：后端来源类型编码未提供，正式枚举值及映射需与后端确认。
 */
enum class SourceType(val label: String) {
    TEMPORARY("临时归还"),
    INSPECTION("送检归还"),
    REPAIR("送修归还"),
    SCRAP("报废归还"),
    RETURN("退货归还")
}

/**
 * 上架单状态：按明细完成情况汇总为待上架、部分上架、已上架，关闭状态由后端标记。
 */
enum class PutawayStatus(val label: String) {
    PENDING("待上架"),
    PARTIALLY("部分上架"),
    COMPLETED("已上架"),
    CLOSED("已关闭")
}

/**
 * 上架单明细状态；新建明细默认待上架，确认上架后转为已上架。
 * TODO: 字段未确定：后端明细状态编码未提供，正式枚举值及映射需与后端确认。
 */
enum class PutawayDetailStatus(val label: String) {
    PENDING("待上架"),
    COMPLETED("已上架")
}

/**
 * 其他上架单，字段按文档“其他上架”需求组织，details 是上架单明细数组。
 * 上架单号、上游单号与创建时间用于列表卡片展示，文档未列出这些字段。
 * TODO: 字段未确定：上架单号、上游单号、创建时间的接口字段名及时间格式需与后端确认。
 */
data class OtherPutawayOrder(
    val putawayOrderNo: String,
    val upstreamType: SourceType,
    val upstreamNo: String,
    val createdAt: String,
    val closed: Boolean = false,
    val details: List<OtherPutawayDetail>
) {
    val totalCount: Int get() = details.size
    val completedCount: Int get() = details.count { it.status == PutawayDetailStatus.COMPLETED }
    val pendingCount: Int get() = details.count { it.status == PutawayDetailStatus.PENDING }

    /**
     * 按明细完成情况汇总单据状态：关闭优先，全部完成已上架，部分完成部分上架，否则待上架。
     */
    val status: PutawayStatus
        get() = when {
            closed -> PutawayStatus.CLOSED
            details.isNotEmpty() && completedCount == details.size -> PutawayStatus.COMPLETED
            completedCount > 0 -> PutawayStatus.PARTIALLY
            else -> PutawayStatus.PENDING
        }

    /**
     * 确认单条明细上架：写入实际货位并转为已上架，再按明细完成情况重算单据状态。
     */
    fun markPutaway(lineNo: Int, locationCode: String): OtherPutawayOrder {
        val updatedDetails = details.map { detail ->
            if (detail.lineNo == lineNo && detail.status == PutawayDetailStatus.PENDING) {
                detail.copy(
                    actualLocation = locationCode,
                    status = PutawayDetailStatus.COMPLETED
                )
            } else {
                detail
            }
        }
        return copy(details = updatedDetails)
    }
}

/**
 * 上架单明细，字段对应文档“明细[].rfid/name/location”，并补充物资编码与单位用于页面展示。
 * location 是系统推荐的上架货位，actualLocation 在确认上架时写入。
 * TODO: 字段未确定：物资编码、单位、推荐货位的接口字段名需与后端确认。
 */
data class OtherPutawayDetail(
    val lineNo: Int,
    val rfid: String,
    val itemCode: String,
    val description: String,
    val unit: String,
    val location: String,
    val actualLocation: String = "",
    val status: PutawayDetailStatus = PutawayDetailStatus.PENDING
)

/**
 * 单件上架请求；操作人由后端从登录信息获取。
 * TODO: 字段未确定：上架单标识与货位主键等接口字段需与后端确认。
 */
data class OtherPutawayConfirmRequest(
    val putawayOrderNo: String,
    val lineNo: Int,
    val rfid: String,
    val locationCode: String
)
