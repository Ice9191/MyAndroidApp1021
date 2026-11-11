package com.hxb.myandroidapp_1021.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class LoginViewModel : ViewModel() {
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

    /** 先校验本次输入快照，再模拟认证；加载期间忽略重复登录。 */
    fun login(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.isLoading) return
        val request = LoginRequest(state.username.trim(), state.password)
        if (request.username.isBlank()) {
            _uiState.update { oldState ->
                oldState.copy(errorMessage = "请输入账号")
            }
            return
        }
        if (request.password.isBlank()) {
            _uiState.update { oldState ->
                oldState.copy(errorMessage = "请输入密码")
            }
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            delay(2000.milliseconds)
            val mockResponse = LoginResponse(userName = request.username, userRole = "材料员")
            _uiState.update { oldState ->
                oldState.copy(isLoading = false, response = mockResponse)
            }
            onSuccess()
        }
    }
}