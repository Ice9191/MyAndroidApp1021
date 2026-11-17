package com.hxb.myandroidapp_1021.domain.picking

import com.hxb.myandroidapp_1021.domain.common.QueryData
import com.hxb.myandroidapp_1021.domain.common.Repository

interface PickingRepository : Repository {
    suspend fun orders(): QueryData<List<PickingOrder>>
    suspend fun detail(orderNo: String): QueryData<PickingOrderDetail>
    suspend fun confirm(request: PickingConfirmRequest): PickingOrderDetail
    suspend fun close(orderNo: String): PickingOrderDetail
}
