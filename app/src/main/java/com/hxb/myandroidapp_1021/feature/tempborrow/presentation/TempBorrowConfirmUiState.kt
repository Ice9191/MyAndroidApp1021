package com.hxb.myandroidapp_1021.feature.tempborrow.presentation

import com.hxb.myandroidapp_1021.feature.tempborrow.domain.model.TempBorrowOrder

/**
 * 临时借出确认页状态；单据数据由借出列表页绑定，页面只读取并通过事件回调修改。
 */
data class TempBorrowConfirmUiState(
    val order: TempBorrowOrder? = null,
    val pickedRfids: Set<String> = emptySet(),
    val isSubmitting: Boolean = false,
    val notificationMessage: String? = null,
    val notificationId: Int = 0
)
