package com.hxb.myandroidapp_1021.feature.serverconfig.domain.repository

import com.hxb.myandroidapp_1021.feature.serverconfig.domain.model.ServerConfig

interface ServerConfigRepository {
    /**
     * 读取当前服务器配置；尚未保存时返回本地联调默认值。
     */
    suspend fun loadConfig(): ServerConfig

    /**
     * 测试服务器连接，成功时返回 null，失败时返回可展示的说明。
     */
    suspend fun testConnection(config: ServerConfig): String?

    /**
     * 保存当前配置，指定的地址生效时返回 true。
     */
    suspend fun saveConfig(config: ServerConfig): Boolean
}
