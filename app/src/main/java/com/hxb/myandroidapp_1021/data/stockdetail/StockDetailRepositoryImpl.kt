package com.hxb.myandroidapp_1021.data.stockdetail

import com.hxb.myandroidapp_1021.domain.stockdetail.*
import com.hxb.myandroidapp_1021.data.common.QueryCache
import com.hxb.myandroidapp_1021.data.network.HttpClient
import com.hxb.myandroidapp_1021.data.network.loadAll
import kotlinx.serialization.builtins.ListSerializer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockDetailRepositoryImpl @Inject constructor(
    private val http: HttpClient,
    private val cache: QueryCache
) : StockDetailRepository {
    override val revision = cache.revision
    private fun api() = http.api(ApiService::class.java)
    override suspend fun records() = cache.load(
        "inventory",
        ListSerializer(StockDetail.serializer())
    ) { http.loadAll { api().records(it) } }

    override suspend fun changeCondition(rfid: String, condition: String): StockDetail {
        val result = http.mutate { api().changeCondition(rfid, ConditionRequest(condition)) }
        cache.invalidate()
        return result
    }
}
