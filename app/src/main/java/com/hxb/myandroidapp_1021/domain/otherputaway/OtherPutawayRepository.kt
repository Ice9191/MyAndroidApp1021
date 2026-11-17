package com.hxb.myandroidapp_1021.domain.otherputaway

import com.hxb.myandroidapp_1021.domain.common.QueryData
import com.hxb.myandroidapp_1021.domain.common.Repository

interface OtherPutawayRepository : Repository {
    suspend fun tasks(): QueryData<List<OtherPutawayTask>>
    suspend fun confirm(request: OtherPutawayRequest): OtherPutawayTask
}
