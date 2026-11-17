package com.hxb.myandroidapp_1021.ui.pages.overduealert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.domain.overduealert.OverdueAlertRepository
import com.hxb.myandroidapp_1021.utils.operationResult
import com.hxb.myandroidapp_1021.utils.userMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@HiltViewModel
class OverdueAlertListViewModel @Inject constructor(private val repository: OverdueAlertRepository) :
    ViewModel() {
    private val _uiState = MutableStateFlow(OverdueAlertListUiState(isLoading = true))
    val uiState: StateFlow<OverdueAlertListUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        viewModelScope.launch { repository.revision.collect { refresh() } }
    }

    fun refresh() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            operationResult { repository.alerts() }
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            alerts = result.data,
                            fromCache = result.fromCache,
                            errorMessage = null
                        ).filterAlerts()
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.userMessage()
                        )
                    }
                }
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
