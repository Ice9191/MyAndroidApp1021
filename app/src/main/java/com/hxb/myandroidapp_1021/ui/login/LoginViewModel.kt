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

    fun login(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.username.isBlank()) {
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
        if (state.isLoading) return

        viewModelScope.launch {
            _uiState.update { oldState ->
                oldState.copy(isLoading = true, errorMessage = null)
            }

            // TODO：目前用 delay 模拟网络请求
            delay(2000.milliseconds)

            _uiState.update { oldState ->
                oldState.copy(isLoading = false)
            }
            onSuccess()
        }
    }
}