package com.hxb.myandroidapp_1021.feature.tempreturn.presentation

import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnOrder

/**
 * 临时归还确认页状态；单据数据由归还列表页绑定，归还信息草稿与弹窗状态也在此保存。
 */
data class TempReturnConfirmUiState(
    val order: TempReturnOrder? = null,
    val returner: String = "",
    val workOrderNo: String = "",
    val returnLocation: String = "",
    val remark: String = "",
    val infoDialogVisible: Boolean = false,
    val draftReturner: String = "",
    val draftWorkOrderNo: String = "",
    val draftReturnLocation: String = "",
    val draftRemark: String = "",
    val infoError: String? = null,
    val isSubmitting: Boolean = false,
    val notificationMessage: String? = null,
    val notificationId: Int = 0
)
