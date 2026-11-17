package com.hxb.myandroidapp_1021.data.picking

import com.hxb.myandroidapp_1021.domain.picking.*
import com.hxb.myandroidapp_1021.data.common.QueryCache
import com.hxb.myandroidapp_1021.network.HttpClient
import com.hxb.myandroidapp_1021.network.loadAll
import kotlinx.serialization.builtins.ListSerializer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PickingRepositoryImpl @Inject constructor(
    private val http: HttpClient,
    private val cache: QueryCache
) : PickingRepository {
    override val revision = cache.revision
    private fun api() = http.api(ApiService::class.java)
    override suspend fun orders() = cache.load(
        "picking-orders",
        ListSerializer(PickingOrder.serializer())
    ) { http.loadAll { api().orders(it) } }

    override suspend fun detail(orderNo: String) = cache.load(
        "picking-orders/$orderNo",
        PickingOrderDetail.serializer()
    ) { http.execute { api().detail(orderNo) } }

    override suspend fun confirm(request: PickingConfirmRequest): PickingOrderDetail {
        val result = http.mutate { api().confirm(request) }
        cache.invalidate()
        return result
    }

    override suspend fun close(orderNo: String): PickingOrderDetail {
        val result = http.mutate { api().close(orderNo) }
        cache.invalidate()
        return result
    }
}
