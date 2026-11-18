package com.hxb.myandroidapp_1021.feature.serverconfig.domain.model

/**
 * 服务器配置请求/保存字段，沿用现有页面的 IP 和端口。
 * TODO: 字段未确定：文档仅提供接口管理业务，未提供 PDA 配置接口；字段名及 port 是否为整数需后端确认。
 */
data class ServerConfig(val ipAddress: String, val port: String)
