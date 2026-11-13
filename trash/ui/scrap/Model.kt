package com.hxb.myandroidapp_1021.ui.scrap

/**
 * 报废申请单
 */
data class ScrapRecord(
    val orderNo: String,
    val team: String,
    val person: String,
    val toolCount: Int,
    val applyDate: String,
    val status: String   // 已审批 / 未审批
)
