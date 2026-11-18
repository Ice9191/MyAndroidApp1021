package com.hxb.myandroidapp_1021.feature.login.presentation

import com.hxb.myandroidapp_1021.feature.login.domain.model.LoginUser

/**
 * 前端表单状态，密码可见性、加载及错误信息不由后端提供。
 */
data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val notificationMessage: String? = null,
    val notificationId: Int = 0,
    val user: LoginUser? = null
)
