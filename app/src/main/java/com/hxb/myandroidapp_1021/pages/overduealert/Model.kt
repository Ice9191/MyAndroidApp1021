package com.hxb.myandroidapp_1021.pages.overduealert

import com.hxb.myandroidapp_1021.domain.overduealert.OverdueAlert as DomainOverdueAlert


/**
 * 超期告警业务数据，依据文档“超期告警”正文、字段关系图及 IF-005 示例。
 */
typealias OverdueAlert = DomainOverdueAlert

/**
 * 前端查询及展开状态，与告警业务字段分开保存。
 */
data class OverdueAlertListUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val alerts: List<OverdueAlert> = emptyList(),
    val filteredAlerts: List<OverdueAlert> = emptyList(),
    val expandedAlertIds: Set<String> = emptySet(),
    val isSubmitting: Boolean = false,
    val fromCache: Boolean = false,
    val errorMessage: String? = null
)
