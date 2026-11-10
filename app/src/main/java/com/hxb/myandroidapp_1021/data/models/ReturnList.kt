package com.hxb.myandroidapp_1021.data.models

/**
 * 归还单
 */
data class ReturnOrder(
    val orderNo: String,
    val team: String,
    val person: String,
    val totalCount: Int,
    val returnedCount: Int,
    val unreturnedCount: Int,
    val status: ReturnStatus
)

enum class FilterType(val label: String) {
    ALL("全部"),
    PARTIAL("部分归还"),
    UNRETURNED("未归还")
}
