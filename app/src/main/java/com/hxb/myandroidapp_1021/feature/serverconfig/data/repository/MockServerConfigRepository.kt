package com.hxb.myandroidapp_1021.feature.serverconfig.data.repository

import com.hxb.myandroidapp_1021.feature.serverconfig.domain.model.ServerConfig
import com.hxb.myandroidapp_1021.feature.serverconfig.domain.repository.ServerConfigRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

/**
 * 保存的服务器配置需要在登录页和配置页之间共享，因此使用应用级作用域；当前只保存在内存中。
 */
@Singleton
class MockServerConfigRepository @Inject constructor() : ServerConfigRepository {
    private val mockConfig = ServerConfig(ipAddress = "192.168.1.100", port = "8080")
    private var mockSavedConfig: ServerConfig? = null

    override suspend fun loadConfig(): ServerConfig = mockSavedConfig ?: mockConfig

    override fun isConfigured(): Boolean {
        val config = mockSavedConfig ?: return false
        return config.ipAddress.isNotBlank() && config.port.isNotBlank()
    }

    /**
     * 模拟一次 1.5 秒连接测试：IP 和端口都非空时成功，否则失败。
     */
    override suspend fun testConnection(config: ServerConfig): Boolean {
        delay(1500.milliseconds)
        return config.ipAddress.isNotBlank() && config.port.isNotBlank()
    }

    // TODO: 字段未确定，保存接口尚未提供；当前仅保存在应用进程内，重启后需要重新配置。
    override suspend fun saveConfig(config: ServerConfig) {
        mockSavedConfig = config
    }
}
