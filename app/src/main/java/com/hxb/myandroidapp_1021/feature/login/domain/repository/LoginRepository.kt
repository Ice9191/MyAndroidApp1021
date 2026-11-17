package com.hxb.myandroidapp_1021.feature.login.domain.repository

import com.hxb.myandroidapp_1021.feature.login.domain.model.LoginUser

interface LoginRepository {
    /**
     * 返回登录成功的人员信息；账号或密码错误时返回 null。
     */
    suspend fun login(username: String, password: String): LoginUser?
}
