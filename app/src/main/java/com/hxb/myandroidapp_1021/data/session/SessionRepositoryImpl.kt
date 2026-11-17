package com.hxb.myandroidapp_1021.data.session

import com.hxb.myandroidapp_1021.data.login.ApiService as LoginApiService
import com.hxb.myandroidapp_1021.data.serverconfig.ApiService as ServerConfigApiService


import com.hxb.myandroidapp_1021.domain.common.AppException
import com.hxb.myandroidapp_1021.domain.session.LoginRequest
import com.hxb.myandroidapp_1021.domain.session.LoginResponse
import com.hxb.myandroidapp_1021.domain.session.ServerConfig
import com.hxb.myandroidapp_1021.domain.session.SessionRepository
import com.hxb.myandroidapp_1021.local.QueryCacheDao
import com.hxb.myandroidapp_1021.local.UserPreferences
import com.hxb.myandroidapp_1021.network.HttpClient
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepositoryImpl @Inject constructor(
    private val preferences: UserPreferences,
    private val http: HttpClient,
    private val cache: QueryCacheDao
) : SessionRepository {
    override val state = preferences.state

    override suspend fun login(request: LoginRequest): LoginResponse {
        val config = state.value.config
        val response = http.execute { http.api(LoginApiService::class.java, config).login(request) }
        if (state.value.config != config) throw AppException(
            "服务器配置已变化，请重新登录",
            "SESSION_CHANGED"
        )
        if (response.token.isBlank()) throw AppException(
            "服务器未返回有效的登录凭证",
            "INVALID_RESPONSE"
        )
        preferences.saveSession(response, request.username, config)
        return response
    }

    override suspend fun logout() {
        val current = state.value
        try {
            if (current.user != null) http.execute {
                http.api(LoginApiService::class.java).logout()
            }
        } catch (_: AppException) {
            // 离线时仍清理本机凭证；服务端凭证按有效期失效。
        } finally {
            // 清理会话会触发页面销毁，凭证与缓存清理必须完成后才响应取消。
            withContext(NonCancellable) {
                preferences.clearSession()
                current.user?.let { cache.clear("${http.baseUrl(current.config)}|${it.userId}") }
            }
        }
    }

    override suspend fun saveConfig(config: ServerConfig) {
        http.baseUrl(config)
        preferences.saveConfig(config)
    }

    override suspend fun testConnection(config: ServerConfig) {
        val result = http.execute { http.api(ServerConfigApiService::class.java, config).health() }
        if (result.status != "ok") throw AppException("服务器未就绪", "SERVER_NOT_READY")
    }
}
