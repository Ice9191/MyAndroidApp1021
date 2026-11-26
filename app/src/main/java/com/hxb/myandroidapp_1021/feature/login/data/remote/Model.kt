package com.hxb.myandroidapp_1021.feature.login.data.remote

import kotlinx.serialization.Serializable

/**
 * POST /login 的 JSON 请求，字段约定见 docs/login-api.md。
 */
@Serializable
data class LoginParams(val username: String, val password: String)

/**
 * 登录成功响应的 data，expiresAt 是服务端返回的 ISO 8601 UTC 到期时间。
 */
@Serializable
data class LoginResponse(
    val userName: String,
    val userRole: String,
    val token: String,
    val expiresAt: String
)
