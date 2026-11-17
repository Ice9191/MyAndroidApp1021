package com.hxb.myandroidapp_1021.data.putaway

import com.hxb.myandroidapp_1021.domain.putaway.PutawayLocation
import com.hxb.myandroidapp_1021.domain.putaway.PutawayOrder
import com.hxb.myandroidapp_1021.domain.putaway.PutawayRequest
import com.hxb.myandroidapp_1021.data.network.ApiResponse
import com.hxb.myandroidapp_1021.data.network.Page
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("putaway-orders")
    suspend fun orders(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int = 100
    ): Response<ApiResponse<Page<PutawayOrder>>>

    @GET("putaway-orders/{orderNo}")
    suspend fun detail(@Path("orderNo") orderNo: String): Response<ApiResponse<PutawayOrder>>
    @POST("putaway-orders/confirm")
    suspend fun confirm(@Body request: PutawayRequest): Response<ApiResponse<PutawayOrder>>
    @GET("locations/{code}")
    suspend fun location(
        @Path("code") code: String,
        @Query("warehouse") warehouse: String
    ): Response<ApiResponse<PutawayLocation>>
}
