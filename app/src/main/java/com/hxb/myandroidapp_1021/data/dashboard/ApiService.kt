package com.hxb.myandroidapp_1021.data.dashboard

import com.hxb.myandroidapp_1021.domain.dashboard.DashboardData
import com.hxb.myandroidapp_1021.network.ApiResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("me")
    suspend fun profile(): Response<ApiResponse<DashboardData>>
}
