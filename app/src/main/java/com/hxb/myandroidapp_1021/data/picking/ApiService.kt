package com.hxb.myandroidapp_1021.data.picking

import com.hxb.myandroidapp_1021.domain.picking.*
import com.hxb.myandroidapp_1021.network.ApiResponse
import com.hxb.myandroidapp_1021.network.Page
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("picking-orders")
    suspend fun orders(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int = 100
    ): Response<ApiResponse<Page<PickingOrder>>>

    @GET("picking-orders/{orderNo}")
    suspend fun detail(@Path("orderNo") orderNo: String): Response<ApiResponse<PickingOrderDetail>>
    @POST("picking-orders/confirm")
    suspend fun confirm(@Body request: PickingConfirmRequest): Response<ApiResponse<PickingOrderDetail>>
    @POST("picking-orders/{orderNo}/close")
    suspend fun close(@Path("orderNo") orderNo: String): Response<ApiResponse<PickingOrderDetail>>
}
