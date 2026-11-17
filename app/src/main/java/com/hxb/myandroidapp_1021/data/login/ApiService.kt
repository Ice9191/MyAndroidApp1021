package com.hxb.myandroidapp_1021.data.login

import com.hxb.myandroidapp_1021.domain.common.ServiceStatus
import com.hxb.myandroidapp_1021.domain.session.LoginRequest
import com.hxb.myandroidapp_1021.domain.session.LoginResponse
import com.hxb.myandroidapp_1021.network.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginResponse>>
    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse<ServiceStatus>>
}
