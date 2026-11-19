package com.hxb.myandroidapp_1021.feature.returns.domain.repository

import com.hxb.myandroidapp_1021.feature.returns.domain.model.ReturnConfirmRequest
import com.hxb.myandroidapp_1021.feature.returns.domain.model.ReturnOrder

/**
 * 归还上架模块的数据入口；Mock 与正式接口实现同一契约。
 * TODO: 字段未确定，分页、错误码和货位校验接口需与后端确认。
 */
interface ReturnRepository {
    suspend fun loadOrders(): List<ReturnOrder>

    suspend fun loadScanOrder(): ReturnOrder

    suspend fun submitPutaway(request: ReturnConfirmRequest): Boolean
}
