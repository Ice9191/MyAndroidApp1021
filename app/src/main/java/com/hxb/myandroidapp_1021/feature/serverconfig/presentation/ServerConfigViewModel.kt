package com.hxb.myandroidapp_1021.feature.serverconfig.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.core.network.HttpClient.NetworkException
import com.hxb.myandroidapp_1021.feature.serverconfig.domain.model.ServerConfig
import com.hxb.myandroidapp_1021.feature.serverconfig.domain.repository.ServerConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServerConfigViewModel @Inject constructor(
    private val serverConfigRepository: ServerConfigRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ServerConfigUiState())
    val uiState: StateFlow<ServerConfigUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val config = serverConfigRepository.loadConfig()
            _uiState.update { oldState ->
                oldState.copy(ipAddress = config.ipAddress, port = config.port)
            }
        }
    }

    fun onIpAddressChange(newIp: String) {
        _uiState.update { oldState ->
            oldState.copy(
                ipAddress = newIp,
                connectionStatus = ConnectionStatus.UNTESTED,
                notificationMessage = null,
                saveErrorMessage = null
            )
        }
    }

    fun onPortChange(newPort: String) {
        _uiState.update { oldState ->
            oldState.copy(
                port = newPort,
                connectionStatus = ConnectionStatus.UNTESTED,
                notificationMessage = null,
                saveErrorMessage = null
            )
        }
    }

    /**
     * 使用点击时的配置快照；请求期间修改了地址，则不能把旧结果当作新配置的连接结果。
     */
    fun testConnection() {
        val state = _uiState.value
        if (state.isTesting || state.isSaving) return
        val request = ServerConfig(state.ipAddress, state.port)
        _uiState.update {
            it.copy(
                isTesting = true,
                connectionStatus = ConnectionStatus.TESTING,
                notificationMessage = null
            )
        }
        viewModelScope.launch {
            val errorMessage = serverConfigRepository.testConnection(request)
            _uiState.update { current ->
                val resultIsCurrent = current.connectionStatus == ConnectionStatus.TESTING
                val hasNewNotification = resultIsCurrent && errorMessage != null
                current.copy(
                    isTesting = false,
                    connectionStatus = when {
                        !resultIsCurrent -> ConnectionStatus.UNTESTED
                        errorMessage == null -> ConnectionStatus.SUCCESS
                        else -> ConnectionStatus.FAILURE
                    },
                    notificationMessage = if (hasNewNotification) errorMessage else current.notificationMessage,
                    notificationId = current.notificationId + if (hasNewNotification) 1 else 0
                )
            }
        }
    }

    fun dismissNotification(notificationId: Int) {
        _uiState.update { current ->
            if (current.notificationId == notificationId) {
                current.copy(notificationMessage = null)
            } else {
                current
            }
        }
    }

    fun saveConfig(onSaveSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.isTesting || state.isSaving) return
        val config = ServerConfig(state.ipAddress, state.port)
        _uiState.update { it.copy(isSaving = true, saveErrorMessage = null, notificationMessage = null) }
        viewModelScope.launch {
            try {
                if (!serverConfigRepository.saveConfig(config)) {
                    _uiState.update { it.copy(saveErrorMessage = "请输入有效的 IP 地址和端口号") }
                    return@launch
                }
                _uiState.update { it.copy(savedConfig = config) }
                onSaveSuccess()
            } catch (error: NetworkException) {
                _uiState.update {
                    it.copy(notificationMessage = error.userMessage, notificationId = it.notificationId + 1)
                }
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }
}
