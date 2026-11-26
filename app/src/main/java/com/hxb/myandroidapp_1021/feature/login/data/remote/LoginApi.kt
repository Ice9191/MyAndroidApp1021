package com.hxb.myandroidapp_1021.feature.login.data.remote

import com.hxb.myandroidapp_1021.core.network.HttpClient.ApiResponse
import retrofit2.Call
import retrofit2.SkipCallbackExecutor
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {
    @POST("login")
    @SkipCallbackExecutor
    fun login(
        @Body request: LoginParams
    ): Call<ApiResponse<LoginResponse>>
}
