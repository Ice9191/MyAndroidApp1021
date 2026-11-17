package com.hxb.myandroidapp_1021.data.overduealert

import com.hxb.myandroidapp_1021.domain.overduealert.OverdueAlert
import com.hxb.myandroidapp_1021.data.network.ApiResponse
import com.hxb.myandroidapp_1021.data.network.Page
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {
    @GET("overdue-alerts")
    suspend fun alerts(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int = 100
    ): Response<ApiResponse<Page<OverdueAlert>>>
}
