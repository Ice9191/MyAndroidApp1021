package com.hxb.myandroidapp_1021.feature.tempreturn.presentation

import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnOrder

/**
 * 临时归还确认页状态；单据数据由归还列表页绑定，页面只读取并通过事件回调修改。
 */
data class TempReturnConfirmUiState(
    val order: TempReturnOrder? = null,
    val isSubmitting: Boolean = false,
    val notificationMessage: String? = null,
    val notificationId: Int = 0
)
