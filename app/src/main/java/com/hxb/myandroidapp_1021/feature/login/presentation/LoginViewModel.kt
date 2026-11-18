package com.hxb.myandroidapp_1021.feature.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.feature.login.domain.repository.LoginRepository
import com.hxb.myandroidapp_1021.feature.serverconfig.domain.repository.ServerConfigRepository
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
    private val loginRepository: LoginRepository,
    private val serverConfigRepository: ServerConfigRepository
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
     * 使用输入快照认证；未保存服务器配置时提示并阻止登录，加载期间忽略重复登录。
     * 请求失败时结束加载，协程取消继续向上传递。
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
        if (!serverConfigRepository.isConfigured()) {
            _uiState.update { oldState ->
                oldState.copy(
                    errorMessage = null,
                    notificationMessage = "请先配置服务器",
                    notificationId = oldState.notificationId + 1
                )
            }
            return
        }
        _uiState.update {
            it.copy(isLoading = true, errorMessage = null, notificationMessage = null)
        }
        viewModelScope.launch {
            val user = try {
                loginRepository.login(username, state.password)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update { oldState ->
                    oldState.copy(
                        isLoading = false,
                        user = null,
                        errorMessage = "登录失败，请检查网络或稍后重试"
                    )
                }
                return@launch
            }
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

    fun dismissNotification() {
        _uiState.update { oldState ->
            oldState.copy(notificationMessage = null)
        }
    }
}
