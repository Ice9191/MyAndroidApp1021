package com.hxb.myandroidapp_1021.feature.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.core.network.HttpClient.NetworkError
import com.hxb.myandroidapp_1021.core.network.HttpClient.NetworkException
import com.hxb.myandroidapp_1021.feature.dashboard.domain.repository.DashboardRepository
import com.hxb.myandroidapp_1021.feature.login.domain.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val dashboardRepository: DashboardRepository,
    private val loginRepository: LoginRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    private var loadJob: Job? = null

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        if (_uiState.value.isLoading || _uiState.value.isLoggingOut) return
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        loadJob = viewModelScope.launch {
            try {
                val res = dashboardRepository.loadDashboard()
                val menuSections = res.menuPermissions.toMenuSections()
                _uiState.update {
                    it.copy(
                        user = res,
                        serverConnected = true,
                        menuSections = menuSections
                    )
                }
            } catch (error: NetworkException) {
                _uiState.update {
                    it.copy(
                        serverConnected = error is NetworkException.Http ||
                                error is NetworkException.Business ||
                                error.type == NetworkError.INVALID_RESPONSE,
                        errorMessage = error.userMessage
                    )
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onLogout() {
        if (_uiState.value.isLoggingOut) return
        _uiState.update { it.copy(isLoggingOut = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                loginRepository.logout()
                loadJob?.cancel()
                _uiState.update { it.copy(user = null, menuSections = emptyList()) }
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                _uiState.update { it.copy(errorMessage = "退出失败，无法清除本地登录会话") }
            } finally {
                _uiState.update { it.copy(isLoggingOut = false) }
            }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
