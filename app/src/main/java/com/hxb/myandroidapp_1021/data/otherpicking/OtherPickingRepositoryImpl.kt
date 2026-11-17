package com.hxb.myandroidapp_1021.data.otherpicking

import com.hxb.myandroidapp_1021.domain.otherpicking.*
import com.hxb.myandroidapp_1021.data.common.QueryCache
import com.hxb.myandroidapp_1021.network.HttpClient
import com.hxb.myandroidapp_1021.network.loadAll
import kotlinx.serialization.builtins.ListSerializer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OtherPickingRepositoryImpl @Inject constructor(
    private val http: HttpClient,
    private val cache: QueryCache
) : OtherPickingRepository {
    override val revision = cache.revision
    private fun api() = http.api(ApiService::class.java)
    override suspend fun tasks() = cache.load(
        "other-picking-tasks",
        ListSerializer(OtherPickingTask.serializer())
    ) { http.loadAll { api().tasks(it) } }

    override suspend fun confirm(request: OtherPickingRequest): OtherPickingTask {
        val result = http.mutate { api().confirm(request) }
        cache.invalidate()
        return result
    }
}
