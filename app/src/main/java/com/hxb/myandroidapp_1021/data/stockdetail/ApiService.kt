package com.hxb.myandroidapp_1021.data.stockdetail

import com.hxb.myandroidapp_1021.domain.stockdetail.StockDetail
import com.hxb.myandroidapp_1021.network.ApiResponse
import com.hxb.myandroidapp_1021.network.Page
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

@Serializable
data class ConditionRequest(val condition: String)
interface ApiService {
    @GET("inventory")
    suspend fun records(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int = 100
    ): Response<ApiResponse<Page<StockDetail>>>

    @PATCH("inventory/{rfid}/condition")
    suspend fun changeCondition(
        @Path("rfid") rfid: String,
        @Body request: ConditionRequest
    ): Response<ApiResponse<StockDetail>>
}
