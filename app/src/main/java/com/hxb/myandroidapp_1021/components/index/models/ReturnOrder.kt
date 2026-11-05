package com.hxb.myandroidapp_1021.components.index.models

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
