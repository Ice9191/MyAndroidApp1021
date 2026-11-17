package com.hxb.myandroidapp_1021.data.overduealert

import com.hxb.myandroidapp_1021.domain.overduealert.*
import com.hxb.myandroidapp_1021.data.common.QueryCache
import com.hxb.myandroidapp_1021.network.HttpClient
import com.hxb.myandroidapp_1021.network.loadAll
import kotlinx.serialization.builtins.ListSerializer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OverdueAlertRepositoryImpl @Inject constructor(
    private val http: HttpClient,
    private val cache: QueryCache
) : OverdueAlertRepository {
    override val revision = cache.revision
    private fun api() = http.api(ApiService::class.java)
    override suspend fun alerts() = cache.load(
        "overdue-alerts",
        ListSerializer(OverdueAlert.serializer())
    ) { http.loadAll { api().alerts(it) } }
}
