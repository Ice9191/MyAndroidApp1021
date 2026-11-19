package com.hxb.myandroidapp_1021.feature.overduealert.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.feature.overduealert.domain.repository.OverdueAlertRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OverdueAlertListViewModel @Inject constructor(
    private val overdueAlertRepository: OverdueAlertRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(OverdueAlertListUiState())
    val uiState: StateFlow<OverdueAlertListUiState> = _uiState.asStateFlow()

    /**
     * 加载告警记录：首次进入由下拉刷新容器在页面完全展示后自动触发，手动下拉复用同一入口。
     * 告警由系统定时任务生成，PDA 不提供处理入口。
     */
    fun refresh() {
        if (_uiState.value.isRefreshing) return
        _uiState.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            val alerts = overdueAlertRepository.loadAlerts()
            _uiState.update {
                it.copy(isRefreshing = false, hasLoaded = true, alerts = alerts)
            }
        }
    }

    fun onSearchQueryChange(value: String) {
        _uiState.update { it.copy(searchQuery = value) }
    }

    fun toggleExpanded(alertId: String) {
        _uiState.update { state ->
            val expanded = state.expandedAlertIds
            state.copy(
                expandedAlertIds = if (alertId in expanded) expanded - alertId else expanded + alertId
            )
        }
    }
}
