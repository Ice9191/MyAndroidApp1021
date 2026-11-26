package com.hxb.myandroidapp_1021.feature.dashboard.data.repository

import com.hxb.myandroidapp_1021.core.network.HttpClient
import com.hxb.myandroidapp_1021.feature.dashboard.data.remote.DashboardApi
import com.hxb.myandroidapp_1021.feature.dashboard.domain.model.DashboardData
import com.hxb.myandroidapp_1021.feature.dashboard.domain.repository.DashboardRepository
import javax.inject.Inject

class DashboardRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient
) : DashboardRepository {
    override suspend fun loadDashboard(): DashboardData {
        val api = httpClient.retrofit.create(DashboardApi::class.java)
        return httpClient.request(api.getUserInfo(), expectedStatus = 200)
    }
}
