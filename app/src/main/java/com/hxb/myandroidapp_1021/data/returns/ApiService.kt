package com.hxb.myandroidapp_1021.data.returns

import com.hxb.myandroidapp_1021.domain.returns.ReturnConfirmRequest
import com.hxb.myandroidapp_1021.domain.returns.ReturnOrder
import com.hxb.myandroidapp_1021.domain.returns.ReturnOrderDetail
import com.hxb.myandroidapp_1021.network.ApiResponse
import com.hxb.myandroidapp_1021.network.Page
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("return-putaway-orders")
    suspend fun orders(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int = 100
    ): Response<ApiResponse<Page<ReturnOrder>>>

    @GET("return-putaway-orders/{orderNo}")
    suspend fun detail(@Path("orderNo") orderNo: String): Response<ApiResponse<ReturnOrderDetail>>
    @POST("return-putaway-orders/confirm")
    suspend fun confirm(@Body request: ReturnConfirmRequest): Response<ApiResponse<ReturnOrderDetail>>
}
