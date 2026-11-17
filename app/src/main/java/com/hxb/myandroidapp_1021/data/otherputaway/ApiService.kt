package com.hxb.myandroidapp_1021.data.otherputaway

import com.hxb.myandroidapp_1021.domain.otherputaway.OtherPutawayRequest
import com.hxb.myandroidapp_1021.domain.otherputaway.OtherPutawayTask
import com.hxb.myandroidapp_1021.network.ApiResponse
import com.hxb.myandroidapp_1021.network.Page
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("other-putaway-tasks")
    suspend fun tasks(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int = 100
    ): Response<ApiResponse<Page<OtherPutawayTask>>>

    @POST("other-putaway-tasks/confirm")
    suspend fun confirm(@Body request: OtherPutawayRequest): Response<ApiResponse<OtherPutawayTask>>
}
