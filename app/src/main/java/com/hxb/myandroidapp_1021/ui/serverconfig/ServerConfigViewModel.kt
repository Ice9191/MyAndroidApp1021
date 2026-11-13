package com.hxb.myandroidapp_1021.ui.serverconfig

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class ServerConfigViewModel : ViewModel() {
    private val mockConfig = ServerConfig(ipAddress = "192.168.1.100", port = "8080")
    private val _uiState = MutableStateFlow(
        ServerConfigUiState(
            ipAddress = mockConfig.ipAddress,
            port = mockConfig.port
        )
    )
    val uiState: StateFlow<ServerConfigUiState> = _uiState.asStateFlow()

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
            delay(1500.milliseconds)
            val mockConnectionSucceeded =
                request.ipAddress.isNotBlank() && request.port.isNotBlank()
            _uiState.update { current ->
                val configUnchanged =
                    current.ipAddress == request.ipAddress && current.port == request.port
                current.copy(
                    isTesting = false,
                    connectionStatus = when {
                        !configUnchanged -> ConnectionStatus.UNTESTED
                        mockConnectionSucceeded -> ConnectionStatus.SUCCESS
                        else -> ConnectionStatus.FAILURE
                    }
                )
            }
        }
    }

    fun saveConfig(onSaveSuccess: () -> Unit) {
        val state = _uiState.value
        val config = ServerConfig(state.ipAddress, state.port)
        // TODO: 字段未确定，确认保存接口后持久化 config；当前仅保留本次表单快照。
        _uiState.update { it.copy(savedConfig = config) }
        onSaveSuccess()
    }
}
