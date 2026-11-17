package com.hxb.myandroidapp_1021.data.otherputaway

import com.hxb.myandroidapp_1021.domain.otherputaway.*
import com.hxb.myandroidapp_1021.data.common.QueryCache
import com.hxb.myandroidapp_1021.network.HttpClient
import com.hxb.myandroidapp_1021.network.loadAll
import kotlinx.serialization.builtins.ListSerializer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OtherPutawayRepositoryImpl @Inject constructor(
    private val http: HttpClient,
    private val cache: QueryCache
) : OtherPutawayRepository {
    override val revision = cache.revision
    private fun api() = http.api(ApiService::class.java)
    override suspend fun tasks() = cache.load(
        "other-putaway-tasks",
        ListSerializer(OtherPutawayTask.serializer())
    ) { http.loadAll { api().tasks(it) } }

    override suspend fun confirm(request: OtherPutawayRequest): OtherPutawayTask {
        val result = http.mutate { api().confirm(request) }
        cache.invalidate()
        return result
    }
}
