package com.hxb.myandroidapp_1021.feature.overduealert.presentation

import com.hxb.myandroidapp_1021.feature.overduealert.domain.model.OverdueAlert

/**
 * 超期告警列表页的刷新、查询及展开状态，不承载告警业务字段。
 */
data class OverdueAlertListUiState(
    val isRefreshing: Boolean = false,
    val hasLoaded: Boolean = false,
    val searchQuery: String = "",
    val alerts: List<OverdueAlert> = emptyList(),
    val expandedAlertIds: Set<String> = emptySet()
) {
    /**
     * 在已加载告警中按告警号、借用人、工单号、借用单号、RFID 和器具名称组合匹配。
     * 派生值不写回状态，加载完成后仍保留查询条件。
     * TODO: 字段未确定：后端查询条件、分页及匹配规则；当前仅筛选本地已加载数据。
     */
    val filteredAlerts: List<OverdueAlert>
        get() {
            val query = searchQuery.trim()
            return alerts.filter { alert ->
                listOf(
                    alert.alertId,
                    alert.borrower,
                    alert.workOrderNo,
                    alert.borrowOrderNo,
                    alert.rfid,
                    alert.toolName
                ).any { it.contains(query, ignoreCase = true) }
            }
        }
}
