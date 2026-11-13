package com.hxb.myandroidapp_1021.ui.repair

/**
 * 送修申请单
 */
data class RepairRecord(
    val orderNo: String,
    val team: String,
    val person: String,
    val toolCount: Int,
    val applyDate: String,
    val status: String   // 已审批 / 未审批
)
