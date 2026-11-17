package com.hxb.myandroidapp_1021.network

import com.hxb.myandroidapp_1021.domain.common.AppException
import com.hxb.myandroidapp_1021.domain.session.ServerConfig
import com.hxb.myandroidapp_1021.local.UserPreferences
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class ApiResponse<T>(val data: T)

@Serializable
data class ErrorResponse(val error: ApiError)

@Serializable
data class ApiError(val code: String, val message: String)

@Singleton
class HttpClient @Inject constructor(
    private val preferences: UserPreferences,
    val json: Json,
    private val client: OkHttpClient
) {
    fun baseUrl(config: ServerConfig): HttpUrl {
        val port = config.port.toIntOrNull()
        if (port == null || port !in 1..65535) throw AppException(
            "端口必须为 1 至 65535",
            "CONFIG_INVALID"
        )
        val input = config.ipAddress.trim()
        val url = (if ("://" in input) input else "http://$input").toHttpUrlOrNull()
            ?: throw AppException("请输入有效的服务器 IP 或域名", "CONFIG_INVALID")
        if (url.username.isNotEmpty() || url.password.isNotEmpty() || url.encodedPath != "/" || url.query != null || url.fragment != null) {
            throw AppException("服务器地址只能包含协议与主机，端口请单独填写", "CONFIG_INVALID")
        }
        return url.newBuilder().port(port).encodedPath("/api/v1/").build()
    }

    fun <T> api(type: Class<T>, config: ServerConfig? = null): T {
        val snapshot = preferences.state.value
        val targetConfig = config ?: snapshot.config
        val token = snapshot.user?.token
        // 每次捕获当前地址与凭证，禁止重定向把认证信息发送给其他服务器。
        val scopedClient = client.newBuilder().addInterceptor { chain ->
            val builder = chain.request().newBuilder().header("Accept", "application/json")
            if (token != null && chain.request().url.encodedPath !in listOf(
                    "/api/v1/auth/login",
                    "/api/v1/health"
                )
            ) {
                builder.header("Authorization", "Bearer $token")
            }
            chain.proceed(builder.build())
        }.build()
        return Retrofit.Builder().baseUrl(baseUrl(targetConfig)).client(scopedClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build().create(type)
    }

    suspend fun <T> execute(call: suspend () -> Response<ApiResponse<T>>): T {
        val session = preferences.state.value.user
        try {
            val response = call()
            if (response.isSuccessful) return response.body()?.data
                ?: throw AppException("服务器返回了空响应", "INVALID_RESPONSE")
            val error = response.errorBody()?.string()?.let { body ->
                try {
                    json.decodeFromString<ErrorResponse>(body).error
                } catch (_: SerializationException) {
                    null
                } catch (_: IllegalArgumentException) {
                    null
                }
            }
            if (response.code() == 401 && session != null && response.raw().request.header("Authorization") == "Bearer ${session.token}" && preferences.state.value.user?.token == session.token) {
                preferences.clearSession()
            }
            throw AppException(
                error?.message ?: when (response.code()) {
                    401 -> "登录已失效，请重新登录"
                    403 -> "没有执行此操作的权限"
                    409 -> "数据已变化，请刷新后重试"
                    else -> "服务器请求失败（${response.code()}）"
                },
                error?.code ?: "HTTP_${response.code()}",
                cacheAllowed = response.code() in 500..599
            )
        } catch (error: CancellationException) {
            throw error
        } catch (error: SerializationException) {
            throw AppException("服务器响应格式与接口契约不一致", "INVALID_RESPONSE", cause = error)
        } catch (error: IOException) {
            throw AppException(
                "无法连接服务器，请检查网络和服务器配置",
                "NETWORK",
                cacheAllowed = true,
                cause = error
            )
        }
    }

    suspend fun <T> mutate(call: suspend () -> Response<ApiResponse<T>>): T {
        try {
            return execute(call)
        } catch (error: AppException) {
            if (!error.cacheAllowed) throw error
            throw AppException(
                "未收到提交确认，请联网刷新核对结果后再重试",
                error.code,
                cause = error
            )
        }
    }
}
