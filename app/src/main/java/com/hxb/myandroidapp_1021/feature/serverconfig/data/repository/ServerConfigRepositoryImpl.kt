package com.hxb.myandroidapp_1021.feature.serverconfig.data.repository

import com.hxb.myandroidapp_1021.core.network.HttpClient
import com.hxb.myandroidapp_1021.core.network.HttpClient.NetworkError
import com.hxb.myandroidapp_1021.core.network.HttpClient.NetworkException
import com.hxb.myandroidapp_1021.feature.serverconfig.data.remote.ServerConfigApi
import com.hxb.myandroidapp_1021.feature.serverconfig.domain.model.ServerConfig
import com.hxb.myandroidapp_1021.feature.serverconfig.domain.repository.ServerConfigRepository
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 读写已保存的请求地址，并测试配置页当前输入的服务器。
 */
@Singleton
class ServerConfigRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient
) : ServerConfigRepository {
    private val defaultConfig = ServerConfig(ipAddress = "192.168.8.87", port = "8080")

    // 配置页可在登录和保存之前检查草稿地址，探测独立于业务会话。
    private val healthCheckClient = OkHttpClient.Builder()
        .callTimeout(5, TimeUnit.SECONDS)
        .followRedirects(false)
        .build()

    override suspend fun loadConfig(): ServerConfig = httpClient.baseUrl?.let {
        ServerConfig(ipAddress = it.host, port = it.port.toString())
    } ?: defaultConfig

    override suspend fun testConnection(config: ServerConfig): String? {
        val baseUrl = config.toBaseUrl() ?: return "请输入有效的 IP 地址和端口号"
        val api = httpClient.createRetrofit(baseUrl, healthCheckClient)
            .create(ServerConfigApi::class.java)
        return try {
            httpClient.request(api.checkHealth(), expectedStatus = 200) { healthy ->
                if (!healthy) throw NetworkException(NetworkError.BUSINESS, "服务器暂时不可用")
            }
            null
        } catch (error: NetworkException) {
            error.userMessage
        }
    }

    override suspend fun saveConfig(config: ServerConfig): Boolean {
        val baseUrl = config.toBaseUrl() ?: return false
        httpClient.saveConfiguredBaseUrl(baseUrl)
        return httpClient.baseUrl == baseUrl
    }

    private fun ServerConfig.toBaseUrl(): HttpUrl? {
        val portNumber = port.trim().toIntOrNull()?.takeIf { it in 1..65535 } ?: return null
        val url = try {
            HttpUrl.Builder().scheme("http").host(ipAddress.trim()).port(portNumber).build()
        } catch (_: IllegalArgumentException) {
            return null
        }
        val octets = url.host.split('.')
        val isIpv4 = octets.size == 4 && octets.all {
            it.toIntOrNull() in 0..255 && it.all(Char::isDigit)
        }
        return url.takeIf { isIpv4 || ':' in it.host }
    }
}
