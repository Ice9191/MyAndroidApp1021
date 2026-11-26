package com.hxb.myandroidapp_1021.feature.serverconfig.data.remote

import com.hxb.myandroidapp_1021.core.network.HttpClient.ApiResponse
import retrofit2.Call
import retrofit2.SkipCallbackExecutor
import retrofit2.http.GET

interface ServerConfigApi {
    @GET("health")
    @SkipCallbackExecutor
    fun checkHealth(): Call<ApiResponse<Boolean>>
}
