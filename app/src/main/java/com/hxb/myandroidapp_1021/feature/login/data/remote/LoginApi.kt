package com.hxb.myandroidapp_1021.feature.login.data.remote

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response

interface LoginApi {
    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>
}
