package com.hxb.myandroidapp_1021.pages.serverconfig

import com.hxb.myandroidapp_1021.domain.session.ServerConfig as DomainServerConfig


/**
 * 服务器配置请求/保存字段，沿用现有页面的 IP 和端口。
 */
typealias ServerConfig = DomainServerConfig


enum class ConnectionStatus {
    UNTESTED, // 尚未测试
    TESTING,  // 正在测试
    SUCCESS,  // 连接成功
    FAILURE   // 连接失败
}

/**
 * 前端表单和连接测试状态，连接结果不作为数据库列。
 */
data class ServerConfigUiState(
    val ipAddress: String = "",
    val port: String = "",
    val connectionStatus: ConnectionStatus = ConnectionStatus.UNTESTED,
    val isTesting: Boolean = false,
    val savedConfig: ServerConfig? = null,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)
