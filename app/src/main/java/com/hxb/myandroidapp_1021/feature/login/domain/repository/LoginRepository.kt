package com.hxb.myandroidapp_1021.feature.login.domain.repository

import com.hxb.myandroidapp_1021.feature.login.domain.model.LoginUser

interface LoginRepository {
    /**
     * 返回登录成功的人员信息。服务端返回的认证失败和其他请求异常向调用方抛出。
     */
    suspend fun login(username: String, password: String): LoginUser?

    suspend fun logout()
}
