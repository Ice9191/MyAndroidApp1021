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
    private val _uiState = MutableStateFlow(ServerConfigUiState())
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

    fun testConnection() {
        if (_uiState.value.isTesting) return

        viewModelScope.launch {
            // 进入测试中状态
            _uiState.update { oldState ->
                oldState.copy(isTesting = true, connectionStatus = ConnectionStatus.TESTING)
            }

            // 模拟网络请求延迟
            delay(1500.milliseconds)

            // 校验并返回结果
            val success = _uiState.value.ipAddress.isNotBlank() && _uiState.value.port.isNotBlank()
            _uiState.update { oldState ->
                oldState.copy(
                    isTesting = false,
                    connectionStatus = if (success) ConnectionStatus.SUCCESS else ConnectionStatus.FAILURE
                )
            }
        }
    }

    fun saveConfig(onSaveSuccess: () -> Unit) {
        // TODO: 这里可以将配置写入 SharedPreferences 或 DataStore
        onSaveSuccess()
    }
}