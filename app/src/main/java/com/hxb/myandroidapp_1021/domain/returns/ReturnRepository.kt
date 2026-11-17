package com.hxb.myandroidapp_1021.domain.returns

import com.hxb.myandroidapp_1021.domain.common.QueryData
import com.hxb.myandroidapp_1021.domain.common.Repository

interface ReturnRepository : Repository {
    suspend fun orders(): QueryData<List<ReturnOrder>>
    suspend fun detail(orderNo: String): QueryData<ReturnOrderDetail>
    suspend fun confirm(request: ReturnConfirmRequest): ReturnOrderDetail
}
