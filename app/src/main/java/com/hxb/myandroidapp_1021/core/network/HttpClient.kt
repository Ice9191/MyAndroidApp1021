package com.hxb.myandroidapp_1021.core.network

import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

object HttpClient {
    // 官方模拟器访问电脑的 localhost:8080；真机需改为电脑的局域网地址。
    private const val BASE_URL = "http://10.0.2.2:8080/"

    // Token 仅保存在当前进程内，重新启动进程后需要重新登录。
    var token: String? = null

    /**
     * 在 OkHttp 请求线程通知凭证失效；UI 接收方负责切换到主线程并管理回调生命周期。
     */
    var onTokenExpired: (() -> Unit)? = null

    private val json = Json { ignoreUnknownKeys = true }
    private val backendUrl = BASE_URL.toHttpUrl()

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .callTimeout(100, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request()
                val requestToken = token
                val isBackend = request.url.scheme == backendUrl.scheme &&
                    request.url.host == backendUrl.host && request.url.port == backendUrl.port
                val isLogin = request.method == "POST" &&
                    request.url.encodedPath == "${backendUrl.encodedPath}login"
                val needsToken = isBackend && !isLogin && !requestToken.isNullOrBlank()

                // 请求前：只给自己的业务接口添加 Token，登录接口直接放行。
                val outgoing = request.newBuilder()
                if (needsToken) {
                    outgoing.header("Authorization", "Bearer $requestToken")
                }
                val response = chain.proceed(outgoing.build())

                // 响应后：清除失效的 Token，再通知外部处理登录跳转。
                // shortcut: 基础版不对失效回调去重；接入多个业务接口时再完善。
                if (needsToken && response.code == 401 && token == requestToken) {
                    clearToken()
                    onTokenExpired?.invoke()
                }
                response
            }
            .build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(backendUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    fun clearToken() {
        token = null
    }
}
