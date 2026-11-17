package com.hxb.myandroidapp_1021.data.common

import com.hxb.myandroidapp_1021.domain.common.AppException
import com.hxb.myandroidapp_1021.domain.common.QueryData
import com.hxb.myandroidapp_1021.local.QueryCacheDao
import com.hxb.myandroidapp_1021.local.QueryCacheEntry
import com.hxb.myandroidapp_1021.local.UserPreferences
import com.hxb.myandroidapp_1021.network.HttpClient
import kotlinx.serialization.KSerializer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QueryCache @Inject constructor(
    private val dao: QueryCacheDao,
    private val preferences: UserPreferences,
    private val http: HttpClient
) {
    private val generation = AtomicLong()
    private val _revision = MutableStateFlow(0L)
    val revision = _revision.asStateFlow()
    fun scope(): String {
        val state = preferences.state.value
        val user = state.user ?: throw AppException("请先登录", "UNAUTHENTICATED")
        return "${http.baseUrl(state.config)}|${user.userId}"
    }

    /**
     * 仅网络或服务器故障允许读缓存；认证失败、校验失败不能用旧数据掩盖。
     */
    suspend fun <T> load(
        resource: String,
        serializer: KSerializer<T>,
        request: suspend () -> T
    ): QueryData<T> {
        val version = generation.get()
        val scope = scope()
        val token = preferences.state.value.user?.token
        return try {
            val data = request()
            if (generation.get() != version) throw AppException(
                "数据已变化，请重新加载",
                "DATA_CHANGED"
            )
            if (preferences.state.value.user?.token != token || scope() != scope) throw AppException(
                "登录状态已变化，请重新加载",
                "SESSION_CHANGED"
            )
            dao.put(
                QueryCacheEntry(
                    scope,
                    resource,
                    http.json.encodeToString(serializer, data),
                    System.currentTimeMillis()
                )
            )
            QueryData(data)
        } catch (error: AppException) {
            if (!error.cacheAllowed || preferences.state.value.user?.token != token) throw error
            val cached = dao.get(scope, resource) ?: throw error
            QueryData(http.json.decodeFromString(serializer, cached.payload), fromCache = true)
        }
    }

    suspend fun invalidate() {
        // 先使在途旧查询失效，避免提交后又把旧响应写回缓存。
        generation.incrementAndGet()
        dao.clear(scope())
        _revision.update { it + 1 }
    }
}
