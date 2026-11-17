package com.hxb.myandroidapp_1021.domain.putaway

import com.hxb.myandroidapp_1021.domain.common.QueryData
import com.hxb.myandroidapp_1021.domain.common.Repository

interface PutawayRepository : Repository {
    suspend fun orders(): QueryData<List<PutawayOrder>>
    suspend fun detail(orderNo: String): QueryData<PutawayOrder>
    suspend fun confirm(request: PutawayRequest): PutawayOrder
    suspend fun validateLocation(warehouse: String, code: String): PutawayLocation
}
