package com.hxb.myandroidapp_1021.feature.picking.domain.model

/**
 * 拣选单业务字段，依据《工器具库系统文档v2》“拣选单功能表关系”。
 * 借用单号和拣选单号共同构成主键；details 为同一单据下的拣选单明细。
 * 新建单据和明细的初始状态均为未拣选；status 依据同文档“单据状态变更”图（未拣选/部分拣选/已拣选/已关闭），ER 图未列出状态列，接口字段名与编码待确认。
 * 原实现使用工单号、未完成/总/已拣数量和合并的车间/工班字段，现按文档改为借用单号、车间与工班分列，
 * 数量由明细状态统计，不再作为单据字段。
 * TODO: 字段未确定：PDA 接口地址、JSON 字段名、状态编码和时间格式需后端确认。
 */
data class PickingOrder(
    val borrowOrderNo: String,
    val orderNo: String,
    val workshop: String,
    val workGroup: String,
    val borrower: String,
    val plannedDate: String,
    val createdBy: String,
    val createdAt: String,
    val updatedBy: String,
    val updatedAt: String,
    // TODO: 字段未确定：ER 图未列出单据状态，按文档状态图保留，接口字段名和编码需后端确认。
    val status: PickingStatus = PickingStatus.UNPICKED,
    val details: List<PickingDetail>
) {
    val totalCount: Int get() = details.size
    val pickedCount: Int get() = details.count { it.status == PickingDetailStatus.PICKED }
    val pendingCount: Int get() = details.count { it.status == PickingDetailStatus.UNPICKED }

    /**
     * 按本次确认的 RFID 把对应明细置为已拣选，并根据明细状态重算单据状态。
     * 已关闭单据不再流转。
     */
    fun markPicked(rfids: Set<String>): PickingOrder {
        if (status == PickingStatus.CLOSED) return this
        val updatedDetails = details.map { detail ->
            if (detail.rfid in rfids && detail.status == PickingDetailStatus.UNPICKED) {
                detail.copy(status = PickingDetailStatus.PICKED)
            } else {
                detail
            }
        }
        return copy(details = updatedDetails, status = updatedDetails.toPickingStatus())
    }
}

/**
 * 由明细状态汇总单据状态；空单据按未拣选处理，避免进度为 0 时显示已完成。
 */
private fun List<PickingDetail>.toPickingStatus(): PickingStatus = when {
    isEmpty() -> PickingStatus.UNPICKED
    all { it.status == PickingDetailStatus.PICKED } -> PickingStatus.PICKED
    any { it.status == PickingDetailStatus.PICKED } -> PickingStatus.PARTIALLY_PICKED
    else -> PickingStatus.UNPICKED
}

enum class PickingStatus(val label: String) {
    UNPICKED("未拣选"),
    PARTIALLY_PICKED("部分拣选"),
    PICKED("已拣选"),
    CLOSED("已关闭")
}

/**
 * 明细状态依据文档“明细状态变更”图；ER 图未列出状态列。
 * TODO: 字段未确定：接口字段名和状态编码需后端确认。
 */
enum class PickingDetailStatus(val label: String) {
    UNPICKED("未拣选"),
    PICKED("已拣选")
}

/**
 * 拣选单明细业务字段，依据《工器具库系统文档v2》“拣选单功能表关系”。
 * 拣选单行号为主键，拣选单号为所属单据的外键引用。
 * status 依据同文档“明细状态变更”图；ER 图未列出状态列，接口字段名与编码待确认。
 * TODO: 字段未确定：接口时间格式和更新人字段名需后端确认。
 */
data class PickingDetail(
    val orderNo: String,
    val lineNo: Int,
    val rfid: String,
    val itemCode: String,
    val description: String,
    val unit: String,
    val batchNo: String,
    val warehouse: String,
    val location: String,
    val createdBy: String,
    val createdAt: String,
    val updatedBy: String,
    val updatedAt: String,
    // TODO: 字段未确定：ER 图未列出明细状态，按文档状态图保留，接口字段名和编码需后端确认。
    val status: PickingDetailStatus = PickingDetailStatus.UNPICKED
)

/**
 * 本批次拣选提交字段：单据号与已核对 RFID 清单。
 * TODO: 字段未确定：提交接口字段名、操作人是否由登录凭证获取、RFID 与行主键的关联方式需后端确认。
 */
data class PickingConfirmRequest(
    val orderNo: String,
    val rfids: List<String>
)
