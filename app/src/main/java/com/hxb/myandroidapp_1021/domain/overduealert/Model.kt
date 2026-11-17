package com.hxb.myandroidapp_1021.domain.overduealert

import kotlinx.serialization.Serializable

@Serializable
data class OverdueAlert(
    val alertId: String,
    val alertType: String,
    val workOrderNo: String,
    val borrowOrderNo: String,
    val borrower: String,
    val toolName: String,
    val rfid: String,
    val itemCode: String,
    val borrowedAt: String,
    val dueAt: String,
    val overdueDays: Int?,
    val status: String?,
    val handlingStatus: String?
)
