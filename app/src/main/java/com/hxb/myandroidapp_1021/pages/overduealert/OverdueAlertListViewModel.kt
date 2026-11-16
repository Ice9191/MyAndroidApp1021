package com.hxb.myandroidapp_1021.pages.overduealert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class OverdueAlertListViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OverdueAlertListUiState(isLoading = true))
    val uiState: StateFlow<OverdueAlertListUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            delay(500.milliseconds)
            // TODO: 字段未确定：接入告警查询接口；生成、提醒及闭环由系统业务驱动，PDA 仅展示。
            val mockAlerts = listOf(
                OverdueAlert(
                    alertId = "ALM20261113001", alertType = "超期未还",
                    workOrderNo = "GD20261109001", borrowOrderNo = "JY20261109001",
                    borrower = "张伟", toolName = "活动扳手 12 寸",
                    rfid = "RFID-10001", itemCode = "270000000001",
                    borrowedAt = "2026-11-09 09:30", dueAt = "2026-11-11 18:00",
                    overdueDays = 2, status = "已催还", handlingStatus = null
                ),
                OverdueAlert(
                    alertId = "ALM20261113002", alertType = "超期未还",
                    workOrderNo = "GD20261110003", borrowOrderNo = "JY20261110002",
                    borrower = "李娜", toolName = "扭矩扳手 5–50 N·m",
                    rfid = "RFID-10002", itemCode = "280000000003",
                    borrowedAt = "2026-11-10 14:10", dueAt = "2026-11-12 18:00",
                    overdueDays = 1, status = "提醒中", handlingStatus = null
                ),
                OverdueAlert(
                    alertId = "ALM20261113003", alertType = "超期未还",
                    workOrderNo = "GD20261106007", borrowOrderNo = "JY20261106005",
                    borrower = "王强", toolName = "绝缘防护工具套装（含绝缘手套及验电器）",
                    rfid = "RFID-10003", itemCode = "270000000011",
                    borrowedAt = "2026-11-06 10:00", dueAt = "2026-11-08 18:00",
                    overdueDays = 5, status = "已催还", handlingStatus = null
                )
            )
            _uiState.update { it.copy(isLoading = false, alerts = mockAlerts).filterAlerts() }
        }
    }

    fun onSearchQueryChange(value: String) {
        _uiState.update { it.copy(searchQuery = value).filterAlerts() }
    }

    fun toggleExpanded(alertId: String) {
        _uiState.update { state ->
            val expanded = state.expandedAlertIds
            state.copy(expandedAlertIds = if (alertId in expanded) expanded - alertId else expanded + alertId)
        }
    }

    /**
     * 在已加载告警中组合匹配关键词，不改变业务状态，加载完成后保留查询条件。
     */
    private fun OverdueAlertListUiState.filterAlerts(): OverdueAlertListUiState {
        // TODO: 字段未确定：后端查询条件、分页及匹配规则；当前仅筛选本地已加载数据。
        val query = searchQuery.trim()
        return copy(filteredAlerts = alerts.filter { alert ->
            listOf(
                alert.alertId,
                alert.borrower,
                alert.workOrderNo,
                alert.borrowOrderNo,
                alert.rfid,
                alert.toolName
            )
                .any { it.contains(query, ignoreCase = true) }
        })
    }
}
