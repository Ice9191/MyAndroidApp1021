package com.hxb.myandroidapp_1021.feature.putaway.presentation

import com.hxb.myandroidapp_1021.feature.putaway.domain.model.PutawayDetail

/**
 * 上架作业页的展示状态；可见明细、扫描数量和抽屉内容由 ViewModel 预先计算。
 */
data class PutawayConfirmUiState(
    val hasOrder: Boolean = false,
    val scanQuery: String = "",
    val visibleDetails: List<PutawayDetailUi> = emptyList(),
    val selectedDetail: PutawayDetailUi? = null,
    val notificationMessage: String? = null,
    val notificationId: Int = 0
)

/**
 * 单张明细卡片的展示数据，quantity 为“已扫描/总数”。
 */
data class PutawayDetailUi(
    val detail: PutawayDetail,
    val quantity: String,
    val isScanned: Boolean
)
