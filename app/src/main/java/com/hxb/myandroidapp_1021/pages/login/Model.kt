package com.hxb.myandroidapp_1021.pages.login

import com.hxb.myandroidapp_1021.domain.session.LoginRequest as DomainLoginRequest
import com.hxb.myandroidapp_1021.domain.session.LoginResponse as DomainLoginResponse


/**
 * 登录请求：用户账号、密码，沿用现有页面输入。
 */
typealias LoginRequest = DomainLoginRequest

/**
 * 登录响应：人员名称、角色；字段含义依据文档“人员”“权限管理”。
 */
typealias LoginResponse = DomainLoginResponse

/**
 * 前端表单状态，密码可见性、加载及错误信息不由后端提供。
 */
data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val response: LoginResponse? = null
)
