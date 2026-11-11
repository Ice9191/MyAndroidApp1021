package com.hxb.myandroidapp_1021.ui.serverconfig

import kotlinx.serialization.Serializable

/**
 * 服务器配置请求/保存字段，沿用现有页面的 IP 和端口。
 * TODO: 字段未确定：文档仅提供接口管理业务，未提供 PDA 配置接口；字段名及 port 是否为整数需后端确认。
 */
@Serializable
data class ServerConfig(val ipAddress: String, val port: String)


enum class ConnectionStatus{
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
    val savedConfig: ServerConfig? = null
)