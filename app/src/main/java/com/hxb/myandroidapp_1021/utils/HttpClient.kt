package com.hxb.myandroidapp_1021.utils

import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object HttpClient {
    // 基础地址
    private const val BASE_URL = "https://api.example.com/"
    // 全局存储token，token是登录后保存的凭证，每次请求都要携带上
    var token = ""
    // token过期的回调函数，问号表示可传可不传
    var onTokenExpired: (()-> Unit)? = null
    // 全局配置JSON解析
    private val json = Json {
        ignoreUnknownKeys = true // 忽略多余字段
        coerceInputValues = true // 空值用默认值
        isLenient = true         // 宽松解析
    }
    // 创建okHttpClient并配置拦截器
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(50, TimeUnit.SECONDS) // 连接超时50s
        .readTimeout(50, TimeUnit.SECONDS) // 读取超时50s
        // 添加网络拦截器(包含请求、响应)
        .addInterceptor { chain ->
            // 获取请求对象，携带token(在请求发出之前携带数据)
            val request = chain.request().newBuilder() // 拿到这个请求
            if(token.isNotEmpty()){
                request.addHeader("Authorization", "Bearer $token")
            }
            // 让网络链条携带请求继续往后执行
            val response = chain.proceed(request.build())
            // 处理401和返回响应结果
            if(response.code == 401){
                token = "" // 清除本地token
                onTokenExpired?.invoke() // 安全执行函数
            }
            response
        }
        // 还可以再添加一个拦截器
        // .addInterceptor {  }
        .build()
}