package com.hxb.myandroidapp_1021.feature.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.feature.login.domain.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onUsernameChange(value: String) {
        _uiState.update { oldState ->
            oldState.copy(username = value, errorMessage = null)
        }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { oldState ->
            oldState.copy(password = value, errorMessage = null)
        }
    }

    fun togglePasswordVisible() {
        _uiState.update { oldState ->
            oldState.copy(passwordVisible = !oldState.passwordVisible)
        }
    }

    /**
     * 先校验本次输入快照，再由仓库认证并更新页面状态；加载期间忽略重复登录。
     */
    fun login(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.isLoading) return
        val username = state.username.trim()
        if (username.isBlank()) {
            _uiState.update { oldState ->
                oldState.copy(errorMessage = "请输入账号")
            }
            return
        }
        if (state.password.isBlank()) {
            _uiState.update { oldState ->
                oldState.copy(errorMessage = "请输入密码")
            }
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val user = loginRepository.login(username, state.password)
            _uiState.update { oldState ->
                oldState.copy(
                    isLoading = false,
                    user = user,
                    errorMessage = if (user == null) "账号或密码错误" else null
                )
            }
            if (user != null) onSuccess()
        }
    }
}
