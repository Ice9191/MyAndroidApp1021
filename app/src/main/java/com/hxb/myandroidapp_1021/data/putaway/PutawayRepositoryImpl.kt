package com.hxb.myandroidapp_1021.data.putaway

import com.hxb.myandroidapp_1021.domain.putaway.*
import com.hxb.myandroidapp_1021.data.common.QueryCache
import com.hxb.myandroidapp_1021.data.network.HttpClient
import com.hxb.myandroidapp_1021.data.network.loadAll
import kotlinx.serialization.builtins.ListSerializer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PutawayRepositoryImpl @Inject constructor(
    private val http: HttpClient,
    private val cache: QueryCache
) : PutawayRepository {
    override val revision = cache.revision
    private fun api() = http.api(ApiService::class.java)
    override suspend fun orders() = cache.load(
        "putaway-orders",
        ListSerializer(PutawayOrder.serializer())
    ) { http.loadAll { api().orders(it) } }

    override suspend fun detail(orderNo: String) = cache.load(
        "putaway-orders/$orderNo",
        PutawayOrder.serializer()
    ) { http.execute { api().detail(orderNo) } }

    override suspend fun confirm(request: PutawayRequest): PutawayOrder {
        val result = http.mutate { api().confirm(request) }
        cache.invalidate()
        return result
    }

    override suspend fun validateLocation(warehouse: String, code: String) =
        http.execute { api().location(code, warehouse) }
}
