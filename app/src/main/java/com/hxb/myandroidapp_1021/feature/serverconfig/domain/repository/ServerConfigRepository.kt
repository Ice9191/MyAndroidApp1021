package com.hxb.myandroidapp_1021.feature.serverconfig.domain.repository

import com.hxb.myandroidapp_1021.feature.serverconfig.domain.model.ServerConfig

interface ServerConfigRepository {
    /**
     * 读取当前服务器配置；尚未保存时返回演示默认值。
     */
    suspend fun loadConfig(): ServerConfig

    /**
     * 返回是否已保存可用的服务器配置，登录前用于判断能否登录。
     */
    fun isConfigured(): Boolean

    /**
     * 测试服务器连接，返回是否连接成功。
     */
    suspend fun testConnection(config: ServerConfig): Boolean

    /**
     * 保存服务器配置；保存接口和持久化方式待后端确认。
     */
    suspend fun saveConfig(config: ServerConfig)
}
