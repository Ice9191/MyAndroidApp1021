package com.hxb.myandroidapp_1021.feature.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.feature.dashboard.domain.repository.DashboardRepository
import com.hxb.myandroidapp_1021.feature.login.domain.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    /**
     * 加载人员信息和服务器状态；菜单是本地展示配置，随加载完成一起显示。
     */
    private fun loadDashboard() {
        viewModelScope.launch {
            val user = dashboardRepository.loadDashboard()
            val serverConnected = dashboardRepository.checkServerConnection()
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    user = user,
                    serverConnected = serverConnected,
                    menuSections = dashboardMenuSections
                )
            }
        }
    }

    fun onLogout(onSuccess: () -> Unit) {
        loginRepository.logout()
        _uiState.update {
            it.copy(user = null, menuSections = emptyList())
        }
        onSuccess()
    }
}
