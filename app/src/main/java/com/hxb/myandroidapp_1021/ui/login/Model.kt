package com.hxb.myandroidapp_1021.ui.login

import kotlinx.serialization.Serializable

/**
 * 登录请求：用户账号、密码，沿用现有页面输入。
 * TODO: 字段未确定：文档未给登录接口，需后端确认账号字段名及密码传输协议。
 */
@Serializable
data class LoginRequest(val username: String, val password: String)

/**
 * 登录响应：人员名称、角色；字段含义依据文档“人员”“权限管理”。
 * TODO: 字段未确定：认证凭证字段名、凭证格式及有效期尚未定义，token 暂为可空占位。
 */
@Serializable
data class LoginResponse(val userName: String, val userRole: String, val token: String? = null)

/** 前端表单状态，密码可见性、加载及错误信息不由后端提供。 */

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val response: LoginResponse? = null
)