package com.hxb.myandroidapp_1021.feature.login.data.remote

import kotlinx.serialization.Serializable

/**
 * POST /login 的 JSON 请求，字段约定见 docs/login-api.md。
 */
@Serializable
data class LoginRequest(val username: String, val password: String)

/**
 * HTTP 200 的 JSON 响应，直接返回人员名称和角色，字段约定见 docs/login-api.md。
 * TODO: 字段未确定：认证凭证字段名、凭证格式及有效期尚未定义，token 暂为可空占位。
 */
@Serializable
data class LoginResponse(val userName: String, val userRole: String, val token: String? = null)
