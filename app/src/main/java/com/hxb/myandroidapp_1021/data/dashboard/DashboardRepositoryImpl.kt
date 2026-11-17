package com.hxb.myandroidapp_1021.data.dashboard

import com.hxb.myandroidapp_1021.domain.dashboard.*
import com.hxb.myandroidapp_1021.data.common.QueryCache
import com.hxb.myandroidapp_1021.data.network.HttpClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepositoryImpl @Inject constructor(
    private val http: HttpClient,
    private val cache: QueryCache
) : DashboardRepository {
    override val revision = cache.revision
    override suspend fun profile() = cache.load(
        "me",
        DashboardData.serializer()
    ) { http.execute { http.api(ApiService::class.java).profile() } }
}
