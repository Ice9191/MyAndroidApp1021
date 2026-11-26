package com.hxb.myandroidapp_1021.feature.dashboard.data.remote

import com.hxb.myandroidapp_1021.core.network.HttpClient.ApiResponse
import com.hxb.myandroidapp_1021.feature.dashboard.domain.model.DashboardData
import retrofit2.Call
import retrofit2.SkipCallbackExecutor
import retrofit2.http.GET

interface DashboardApi {
    @GET("user-info")
    @SkipCallbackExecutor
    fun getUserInfo(): Call<ApiResponse<DashboardData>>
}
