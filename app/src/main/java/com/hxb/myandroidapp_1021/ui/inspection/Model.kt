package com.hxb.myandroidapp_1021.ui.inspection

/**
 * 送检申请单（列表展示 + 审批状态）
 */
data class InspectionRecord(
    val orderNo: String,
    val team: String,
    val person: String,
    val toolCount: Int,
    val applyDate: String,
    val status: String   // 已审批 / 未审批
)
