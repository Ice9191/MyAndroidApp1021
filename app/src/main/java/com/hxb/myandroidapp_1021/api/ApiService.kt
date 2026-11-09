package com.hxb.myandroidapp_1021.api

import com.hxb.myandroidapp_1021.models.ApiResponse
import com.hxb.myandroidapp_1021.models.LoginParams
import com.hxb.myandroidapp_1021.models.PageData
import com.hxb.myandroidapp_1021.models.PageQuestionItem
import com.hxb.myandroidapp_1021.models.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    /**
     * 登录POST请求
     */
    @POST("login")
    suspend fun login(@Body params: LoginParams): ApiResponse<User>

    /**
     * 获取题目列表
     */
    @GET("question/list")
    suspend fun getQuestionList(
        @Query("type") type: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("questionBankType") questionBankType: String?
    ): ApiResponse<PageData<PageQuestionItem>>
}