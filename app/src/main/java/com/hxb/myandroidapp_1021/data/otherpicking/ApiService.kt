package com.hxb.myandroidapp_1021.data.otherpicking

import com.hxb.myandroidapp_1021.domain.otherpicking.OtherPickingRequest
import com.hxb.myandroidapp_1021.domain.otherpicking.OtherPickingTask
import com.hxb.myandroidapp_1021.network.ApiResponse
import com.hxb.myandroidapp_1021.network.Page
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("other-picking-tasks")
    suspend fun tasks(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int = 100
    ): Response<ApiResponse<Page<OtherPickingTask>>>

    @POST("other-picking-tasks/confirm")
    suspend fun confirm(@Body request: OtherPickingRequest): Response<ApiResponse<OtherPickingTask>>
}
