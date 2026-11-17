package com.hxb.myandroidapp_1021.ui.pages.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.domain.session.SessionRepository
import com.hxb.myandroidapp_1021.utils.operationResult
import com.hxb.myandroidapp_1021.utils.userMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: SessionRepository) : ViewModel() {
    private val _uiState =
        MutableStateFlow(LoginUiState(username = repository.state.value.lastUsername))
    val uiState = _uiState.asStateFlow()

    fun onUsernameChange(value: String) {
        _uiState.update { it.copy(username = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun togglePasswordVisible() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun login(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.isLoading) return
        val error = when {
            state.username.isBlank() -> "请输入账号"
            state.password.isBlank() -> "请输入密码"
            repository.state.value.config.ipAddress.isBlank() -> "请先配置服务器地址"
            else -> null
        }
        if (error != null) {
            _uiState.update { it.copy(errorMessage = error) }; return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            operationResult {
                repository.login(
                    LoginRequest(
                        state.username.trim(),
                        state.password
                    )
                )
            }
                .onSuccess { response ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            password = "",
                            response = response
                        )
                    }
                    onSuccess()
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
}
