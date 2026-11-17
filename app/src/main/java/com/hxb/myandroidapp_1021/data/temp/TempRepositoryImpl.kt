package com.hxb.myandroidapp_1021.data.temp

import com.hxb.myandroidapp_1021.domain.temp.*
import com.hxb.myandroidapp_1021.data.common.QueryCache
import com.hxb.myandroidapp_1021.network.HttpClient
import com.hxb.myandroidapp_1021.network.loadAll
import kotlinx.serialization.builtins.ListSerializer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TempRepositoryImpl @Inject constructor(
    private val http: HttpClient,
    private val cache: QueryCache
) : TempRepository {
    override val revision = cache.revision
    private fun api() = http.api(ApiService::class.java)
    override suspend fun borrowOrders() = cache.load(
        "temp/borrow-orders",
        ListSerializer(TempBorrowOrder.serializer())
    ) { http.loadAll { api().borrowOrders(it) } }

    override suspend fun returnOrders() = cache.load(
        "temp/return-orders",
        ListSerializer(TempReturnOrder.serializer())
    ) { http.loadAll { api().returnOrders(it) } }

    override suspend fun availableTools() = cache.load(
        "temp/available-tools",
        ListSerializer(TempTool.serializer())
    ) { http.loadAll { api().availableTools(it) } }

    override suspend fun create(request: TempBorrowRequest): TempBorrowOrder {
        val result = http.mutate { api().create(request) }; cache.invalidate(); return result
    }

    override suspend fun borrow(request: TempBorrowConfirmRequest): TempBorrowOrder {
        val result = http.mutate { api().borrow(request) }; cache.invalidate(); return result
    }

    override suspend fun returnTool(request: TempReturnConfirmRequest): TempReturnOrder {
        val result = http.mutate { api().returnTool(request) }; cache.invalidate(); return result
    }
}
