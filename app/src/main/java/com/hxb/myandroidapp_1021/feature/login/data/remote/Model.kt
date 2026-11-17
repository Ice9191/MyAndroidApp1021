package com.hxb.myandroidapp_1021.feature.login.data.remote

import kotlinx.serialization.Serializable

/**
 * POST /login 的 JSON 请求，字段约定见 docs/login-api.md。
 */
@Serializable
data class LoginRequest(val username: String, val password: String)

/**
 * HTTP 200 的 JSON 响应，人员信息和 Bearer Token 均为必填，字段约定见 docs/login-api.md。
 */
@Serializable
data class LoginResponse(val userName: String, val userRole: String, val token: String)
