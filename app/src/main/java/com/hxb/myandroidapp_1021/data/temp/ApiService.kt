package com.hxb.myandroidapp_1021.data.temp

import com.hxb.myandroidapp_1021.domain.temp.TempBorrowConfirmRequest
import com.hxb.myandroidapp_1021.domain.temp.TempBorrowOrder
import com.hxb.myandroidapp_1021.domain.temp.TempBorrowRequest
import com.hxb.myandroidapp_1021.domain.temp.TempReturnConfirmRequest
import com.hxb.myandroidapp_1021.domain.temp.TempReturnOrder
import com.hxb.myandroidapp_1021.domain.temp.TempTool
import com.hxb.myandroidapp_1021.network.ApiResponse
import com.hxb.myandroidapp_1021.network.Page
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("temp/borrow-orders")
    suspend fun borrowOrders(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int = 100
    ): Response<ApiResponse<Page<TempBorrowOrder>>>

    @GET("temp/return-orders")
    suspend fun returnOrders(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int = 100
    ): Response<ApiResponse<Page<TempReturnOrder>>>

    @GET("temp/available-tools")
    suspend fun availableTools(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int = 100
    ): Response<ApiResponse<Page<TempTool>>>

    @POST("temp/borrow-orders")
    suspend fun create(@Body request: TempBorrowRequest): Response<ApiResponse<TempBorrowOrder>>

    @POST("temp/borrow-orders/confirm")
    suspend fun borrow(@Body request: TempBorrowConfirmRequest): Response<ApiResponse<TempBorrowOrder>>

    @POST("temp/return-orders/confirm")
    suspend fun returnTool(@Body request: TempReturnConfirmRequest): Response<ApiResponse<TempReturnOrder>>
}
