package com.hxb.myandroidapp_1021.feature.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.core.network.HttpClient.NetworkException
import com.hxb.myandroidapp_1021.feature.login.domain.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
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
            oldState.copy(username = value, usernameError = false)
        }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { oldState ->
            oldState.copy(password = value, passwordError = false)
        }
    }

    fun togglePasswordVisible() {
        _uiState.update { oldState ->
            oldState.copy(passwordVisible = !oldState.passwordVisible)
        }
    }

    /**
     * 使用输入快照认证；未保存服务器配置时提示并阻止登录，加载期间忽略重复登录。
     * 请求失败时结束加载，协程取消继续向上传递。
     */
    fun login() {
        val state = _uiState.value
        if (state.isLoading) return
        val username = state.username.trim()
        val usernameMissing = username.isBlank()
        val passwordMissing = state.password.isBlank()
        if (usernameMissing || passwordMissing) {
            _uiState.update {
                it.copy(usernameError = usernameMissing, passwordError = passwordMissing)
            }
            return
        }
        _uiState.update {
            it.copy(
                isLoading = true,
                usernameError = false,
                passwordError = false,
                notificationMessage = null
            )
        }
        viewModelScope.launch {
            val user = try {
                loginRepository.login(username, state.password)
            } catch (e: CancellationException) {
                throw e
            } catch (error: Exception) {
                _uiState.update { it.copy(isLoading = false, user = null) }
                showNotification(
                    if (error is NetworkException) error.userMessage
                    else "登录失败，请检查网络或稍后重试"
                )
                return@launch
            }
            _uiState.update { oldState ->
                oldState.copy(isLoading = false, user = user)
            }
            if (user == null) showNotification("账号或密码错误")
        }
    }

    internal fun showNotification(message: String) {
        _uiState.update { oldState ->
            oldState.copy(
                notificationMessage = message,
                notificationId = oldState.notificationId + 1
            )
        }
    }

    fun dismissNotification() {
        _uiState.update { oldState ->
            oldState.copy(notificationMessage = null)
        }
    }
}
