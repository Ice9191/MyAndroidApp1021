package com.hxb.myandroidapp_1021.data.serverconfig

import com.hxb.myandroidapp_1021.domain.common.ServiceStatus
import com.hxb.myandroidapp_1021.network.ApiResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("health")
    suspend fun health(): Response<ApiResponse<ServiceStatus>>
}
