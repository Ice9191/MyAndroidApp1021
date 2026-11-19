package com.hxb.myandroidapp_1021.feature.otherpicking.domain.model

/**
 * 其他拣选的上游业务类型，沿用需求中的五类来源。
 * TODO: 字段未确定：接口编码需后端确认，这里用枚举名作为应用内标识，不直接当作接口值。
 */
enum class SourceType(val label: String) {
    TEMPORARY("临时拣选"),
    INSPECTION("送检拣选"),
    REPAIR("送修拣选"),
    SCRAP("报废拣选"),
    RETURN("退货拣选")
}

/**
 * 其他拣选的单据状态，标签与页面展示保持一致。
 */
enum class PickingStatus(val label: String) {
    PENDING("待拣选"),
    PARTIALLY("部分拣选"),
    COMPLETED("已拣选"),
    CLOSED("已关闭")
}

/**
 * 拣选单明细的拣选状态。
 */
enum class PickingDetailStatus {
    UNPICKED,
    PICKED
}

/**
 * 其他拣选单。上游类型、上游单号来自单据来源，拣选单号为本系统内部单号。
 * TODO: 字段未确定：拣选单号、车间、工班、计划日期的接口字段名与来源需后端确认；
 * 文档只列出 sourceNo、sourceType、date 与明细的 rfid、name、location。
 */
data class OtherPickingOrder(
    val pickingOrderNo: String,
    val upstreamType: SourceType,
    val upstreamNo: String,
    val workshop: String = "",
    val workGroup: String = "",
    val plannedDate: String = "",
    val closed: Boolean = false,
    val details: List<OtherPickingDetail> = emptyList()
) {
    /** 明细总数。 */
    val totalCount: Int
        get() = details.size

    /** 已拣选明细数。 */
    val completedCount: Int
        get() = details.count { it.status == PickingDetailStatus.PICKED }

    /** 待拣选明细数。 */
    val pendingCount: Int
        get() = details.count { it.status == PickingDetailStatus.UNPICKED }

    /**
     * 单据状态：关闭优先，其次按已拣选明细占比推导。
     */
    val status: PickingStatus
        get() = when {
            closed -> PickingStatus.CLOSED
            details.isEmpty() -> PickingStatus.PENDING
            details.all { it.status == PickingDetailStatus.PICKED } -> PickingStatus.COMPLETED
            details.any { it.status == PickingDetailStatus.PICKED } -> PickingStatus.PARTIALLY
            else -> PickingStatus.PENDING
        }

    /**
     * 按 RFID 集合把未拣选明细置为已拣选；单据已关闭时不流转。
     */
    fun markPicked(rfids: Set<String>): OtherPickingOrder {
        if (closed) return this
        return copy(
            details = details.map { detail ->
                if (detail.status == PickingDetailStatus.UNPICKED && detail.rfid in rfids) {
                    detail.copy(status = PickingDetailStatus.PICKED)
                } else {
                    detail
                }
            }
        )
    }
}

/**
 * 拣选单明细。itemCode、description、unit、batchNo、warehouse、location 用于扫描页明细展示。
 * TODO: 字段未确定：明细字段名与拣选单接口的对应关系需后端确认。
 */
data class OtherPickingDetail(
    val lineNo: Int,
    val rfid: String,
    val itemCode: String,
    val description: String,
    val unit: String,
    val batchNo: String,
    val warehouse: String,
    val location: String,
    val status: PickingDetailStatus = PickingDetailStatus.UNPICKED
)

/**
 * 提交一批拣选结果的请求。
 * TODO: 字段未确定：确认接口当前只确认 sourceNo、sourceType，拣选单号与 RFID 列表的字段名待确认。
 */
data class OtherPickingConfirmRequest(
    val pickingOrderNo: String,
    val rfids: List<String>
)
