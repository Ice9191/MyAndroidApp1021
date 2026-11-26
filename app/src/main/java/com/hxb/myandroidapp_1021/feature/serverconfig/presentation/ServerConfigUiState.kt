package com.hxb.myandroidapp_1021.feature.serverconfig.presentation

import com.hxb.myandroidapp_1021.feature.serverconfig.domain.model.ServerConfig

enum class ConnectionStatus {
    UNTESTED, // 尚未测试
    TESTING, // 正在测试
    SUCCESS, // 连接成功
    FAILURE // 连接失败
}

/**
 * 前端表单和连接测试状态，连接结果不作为数据库列。
 */
data class ServerConfigUiState(
    val ipAddress: String = "",
    val port: String = "",
    val connectionStatus: ConnectionStatus = ConnectionStatus.UNTESTED,
    val isTesting: Boolean = false,
    val isSaving: Boolean = false,
    val savedConfig: ServerConfig? = null,
    val saveErrorMessage: String? = null,
    val notificationMessage: String? = null,
    val notificationId: Int = 0
)
