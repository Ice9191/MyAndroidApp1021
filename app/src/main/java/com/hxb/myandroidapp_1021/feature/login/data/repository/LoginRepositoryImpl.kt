package com.hxb.myandroidapp_1021.feature.login.data.repository

import com.hxb.myandroidapp_1021.feature.login.data.remote.LoginApi
import com.hxb.myandroidapp_1021.feature.login.data.remote.LoginRequest
import com.hxb.myandroidapp_1021.feature.login.domain.model.LoginUser
import com.hxb.myandroidapp_1021.feature.login.domain.repository.LoginRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val loginApi: LoginApi
) : LoginRepository {
    /**
     * 按登录接口约定将 HTTP 401 转为认证失败，其他请求异常由页面状态层处理。
     */
    override suspend fun login(username: String, password: String): LoginUser? {
        val request = LoginRequest(username = username, password = password)
        val response = loginApi.login(request)
        if (response.code() == 401) return null
        if (!response.isSuccessful) throw HttpException(response)

        val body = response.body() ?: throw IOException("Empty login response")
        return LoginUser(userName = body.userName, userRole = body.userRole)
    }
}
