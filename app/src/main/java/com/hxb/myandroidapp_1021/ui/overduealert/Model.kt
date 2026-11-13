package com.hxb.myandroidapp_1021.ui.overduealert

import kotlinx.serialization.Serializable

/**
 * 超期告警业务数据，依据文档“超期告警”正文、字段关系图及 IF-005 示例。
 * TODO: 字段未确定：以下 Kotlin 命名仅为对接草案，查询接口、JSON 字段名、可空性及时间格式需后端确认。
 * TODO: 字段未确定：正文告警状态与 IF-005 处理状态的对应关系；保留原始值，不自行映射。
 */
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
    // TODO: 字段未确定：超期天数的取整及截止时间口径，由后端提供，不在 PDA 计算。
    val overdueDays: Int?,
    val status: String?,
    val handlingStatus: String?
)

/**
 * 前端查询及展开状态，与告警业务字段分开保存。
 */
data class OverdueAlertListUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val alerts: List<OverdueAlert> = emptyList(),
    val filteredAlerts: List<OverdueAlert> = emptyList(),
    val expandedAlertIds: Set<String> = emptySet()
)

