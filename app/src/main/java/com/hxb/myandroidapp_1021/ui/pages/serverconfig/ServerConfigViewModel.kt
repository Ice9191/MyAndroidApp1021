package com.hxb.myandroidapp_1021.ui.pages.serverconfig

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.hxb.myandroidapp_1021.utils.operationResult
import com.hxb.myandroidapp_1021.utils.userMessage
import com.hxb.myandroidapp_1021.domain.session.SessionRepository

@HiltViewModel
class ServerConfigViewModel @Inject constructor(private val repository: SessionRepository) :
    ViewModel() {
    private val config = repository.state.value.config
    private val _uiState =
        MutableStateFlow(ServerConfigUiState(ipAddress = config.ipAddress, port = config.port))
    val uiState = _uiState.asStateFlow()

    fun onIpAddressChange(value: String) {
        _uiState.update {
            it.copy(
                ipAddress = value,
                connectionStatus = ConnectionStatus.UNTESTED,
                errorMessage = null
            )
        }
    }

    fun onPortChange(value: String) {
        _uiState.update {
            it.copy(
                port = value,
                connectionStatus = ConnectionStatus.UNTESTED,
                errorMessage = null
            )
        }
    }

    /**
     * 输入在请求期间变化时丢弃旧连接结果，连接成功必须来自真实 health 接口。
     */
    fun testConnection() {
        val state = _uiState.value
        if (state.isTesting || state.isSaving) return
        val snapshot = ServerConfig(state.ipAddress.trim(), state.port.trim())
        _uiState.update {
            it.copy(
                isTesting = true,
                connectionStatus = ConnectionStatus.TESTING,
                errorMessage = null
            )
        }
        viewModelScope.launch {
            val result = operationResult { repository.testConnection(snapshot) }
            _uiState.update { current ->
                val unchanged =
                    current.ipAddress.trim() == snapshot.ipAddress && current.port.trim() == snapshot.port
                current.copy(
                    isTesting = false,
                    connectionStatus = when {
                        !unchanged -> ConnectionStatus.UNTESTED
                        result.isSuccess -> ConnectionStatus.SUCCESS
                        else -> ConnectionStatus.FAILURE
                    },
                    errorMessage = if (unchanged) result.exceptionOrNull()?.userMessage() else null
                )
            }
        }
    }

    fun saveConfig(onSaveSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.isSaving || state.isTesting) return
        val snapshot = ServerConfig(state.ipAddress.trim(), state.port.trim())
        _uiState.update { it.copy(isSaving = true, errorMessage = null) }
        viewModelScope.launch {
            operationResult { repository.saveConfig(snapshot) }
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            savedConfig = snapshot
                        )
                    }; onSaveSuccess()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = error.userMessage()
                        )
                    }
                }
        }
    }
}
