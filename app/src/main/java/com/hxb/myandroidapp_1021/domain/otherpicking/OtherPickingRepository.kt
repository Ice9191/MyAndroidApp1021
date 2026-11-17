package com.hxb.myandroidapp_1021.domain.otherpicking

import com.hxb.myandroidapp_1021.domain.common.QueryData
import com.hxb.myandroidapp_1021.domain.common.Repository

interface OtherPickingRepository : Repository {
    suspend fun tasks(): QueryData<List<OtherPickingTask>>
    suspend fun confirm(request: OtherPickingRequest): OtherPickingTask
}
