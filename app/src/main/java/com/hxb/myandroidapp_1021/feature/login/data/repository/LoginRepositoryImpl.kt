package com.hxb.myandroidapp_1021.feature.login.data.repository

import com.hxb.myandroidapp_1021.core.network.HttpClient
import com.hxb.myandroidapp_1021.core.network.HttpClient.NetworkError
import com.hxb.myandroidapp_1021.core.network.HttpClient.NetworkException
import com.hxb.myandroidapp_1021.feature.login.data.remote.LoginApi
import com.hxb.myandroidapp_1021.feature.login.data.remote.LoginParams
import com.hxb.myandroidapp_1021.feature.login.domain.model.LoginUser
import com.hxb.myandroidapp_1021.feature.login.domain.repository.LoginRepository
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.Instant

class LoginRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient
) : LoginRepository {
    override suspend fun login(username: String, password: String): LoginUser {
        val request = LoginParams(username = username, password = password)
        val retrofit = httpClient.retrofit
        val loginApi = retrofit.create(LoginApi::class.java)
        val res = httpClient.request(loginApi.login(request), expectedStatus = 200) {
            val expiresAt = Instant.parseOrNull(it.expiresAt)
            if (it.token.isBlank() || expiresAt == null || expiresAt <= Clock.System.now()) {
                throw NetworkException(NetworkError.INVALID_RESPONSE)
            }
        }
        httpClient.saveSession(retrofit, res.token, res.expiresAt)
        return LoginUser(userName = res.userName, userRole = res.userRole)
    }

    override suspend fun logout() {
        httpClient.clearToken()
    }
}
