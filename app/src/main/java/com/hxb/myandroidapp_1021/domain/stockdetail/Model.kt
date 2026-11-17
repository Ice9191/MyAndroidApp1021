package com.hxb.myandroidapp_1021.domain.stockdetail

import kotlinx.serialization.Serializable

@Serializable
data class StockDetail(
    val rfid: String,
    val itemCode: String,
    val description: String,
    val specification: String?,
    val unit: String,
    val category: String?,
    val inventoryStatus: String,
    val condition: String,
    val faultDescription: String?,
    val warehouse: String,
    val location: String?,
    val requiresInspection: Boolean?,
    val inspectionCycle: String?,
    val nextInspectionDate: String? = null
)
