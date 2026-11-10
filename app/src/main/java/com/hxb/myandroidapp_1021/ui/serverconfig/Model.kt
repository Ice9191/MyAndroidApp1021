package com.hxb.myandroidapp_1021.ui.serverconfig

enum class ConnectionStatus{
    UNTESTED, // 尚未测试
    TESTING,  // 正在测试
    SUCCESS,  // 连接成功
    FAILURE   // 连接失败
}

data class ServerConfigUiState(
    val ipAddress: String = "192.168.1.100",
    val port: String = "8080",
    val connectionStatus: ConnectionStatus = ConnectionStatus.UNTESTED,
    val isTesting: Boolean = false
)