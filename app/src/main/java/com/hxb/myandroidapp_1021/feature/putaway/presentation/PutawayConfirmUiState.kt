package com.hxb.myandroidapp_1021.feature.putaway.presentation

import com.hxb.myandroidapp_1021.feature.putaway.domain.model.PutawayDetail

/**
 * 上架作业页的展示状态，可见明细和抽屉内容由 ViewModel 计算。
 */
data class PutawayConfirmUiState(
    val hasOrder: Boolean = false,
    val isScanning: Boolean = false,
    val scanQuery: String = "",
    val visibleDetails: List<PutawayDetailUi> = emptyList(),
    val selectedDetail: PutawayDetailUi? = null,
    val notificationMessage: String? = null,
    val notificationId: Int = 0
)

/**
 * 按《工器具库系统文档v2》的“上架单功能表间关系”，移除原先按物资汇总的数量展示。
 */
data class PutawayDetailUi(
    val detail: PutawayDetail,
    val isScanned: Boolean
)
