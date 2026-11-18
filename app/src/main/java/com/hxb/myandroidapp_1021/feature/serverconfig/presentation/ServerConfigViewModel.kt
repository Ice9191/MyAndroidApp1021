package com.hxb.myandroidapp_1021.feature.serverconfig.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            oldState.copy(ipAddress = newIp, connectionStatus = ConnectionStatus.UNTESTED)
        }
    }

    fun onPortChange(newPort: String) {
        _uiState.update { oldState ->
            oldState.copy(port = newPort, connectionStatus = ConnectionStatus.UNTESTED)
        }
    }

    /**
     * 使用点击时的配置快照；请求期间修改了地址，则不能把旧结果当作新配置的连接结果。
     */
    fun testConnection() {
        val state = _uiState.value
        if (state.isTesting) return
        val request = ServerConfig(state.ipAddress, state.port)
        _uiState.update { it.copy(isTesting = true, connectionStatus = ConnectionStatus.TESTING) }
        viewModelScope.launch {
            val connectionSucceeded = serverConfigRepository.testConnection(request)
            _uiState.update { current ->
                val configUnchanged =
                    current.ipAddress == request.ipAddress && current.port == request.port
                current.copy(
                    isTesting = false,
                    connectionStatus = when {
                        !configUnchanged -> ConnectionStatus.UNTESTED
                        connectionSucceeded -> ConnectionStatus.SUCCESS
                        else -> ConnectionStatus.FAILURE
                    }
                )
            }
        }
    }

    fun saveConfig(onSaveSuccess: () -> Unit) {
        val state = _uiState.value
        val config = ServerConfig(state.ipAddress, state.port)
        viewModelScope.launch {
            serverConfigRepository.saveConfig(config)
            _uiState.update { it.copy(savedConfig = config) }
            onSaveSuccess()
        }
    }
}
