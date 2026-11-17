package com.hxb.myandroidapp_1021.data.returns

import com.hxb.myandroidapp_1021.domain.returns.*
import com.hxb.myandroidapp_1021.data.common.QueryCache
import com.hxb.myandroidapp_1021.network.HttpClient
import com.hxb.myandroidapp_1021.network.loadAll
import kotlinx.serialization.builtins.ListSerializer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReturnRepositoryImpl @Inject constructor(
    private val http: HttpClient,
    private val cache: QueryCache
) : ReturnRepository {
    override val revision = cache.revision
    private fun api() = http.api(ApiService::class.java)
    override suspend fun orders() = cache.load(
        "return-putaway-orders",
        ListSerializer(ReturnOrder.serializer())
    ) { http.loadAll { api().orders(it) } }

    override suspend fun detail(orderNo: String) = cache.load(
        "return-putaway-orders/$orderNo",
        ReturnOrderDetail.serializer()
    ) { http.execute { api().detail(orderNo) } }

    override suspend fun confirm(request: ReturnConfirmRequest): ReturnOrderDetail {
        val result = http.mutate { api().confirm(request) }
        cache.invalidate()
        return result
    }
}
