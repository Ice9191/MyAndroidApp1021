package com.hxb.myandroidapp_1021.feature.putaway.presentation

/**
 * 上架作业页的输入、提交和提示状态。
 */
data class PutawayConfirmUiState(
    val isLoading: Boolean = true,
    val operator: String = "",
    val rfid: String = "",
    val locationCode: String = "",
    val boundLocation: String = "",
    val selectedLineNo: String? = null,
    val selectedTab: Int = 0,
    val isSubmitting: Boolean = false,
    val notificationMessage: String? = null,
    val notificationId: Int = 0
)
