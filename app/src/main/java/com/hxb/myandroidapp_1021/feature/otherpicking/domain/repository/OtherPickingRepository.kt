package com.hxb.myandroidapp_1021.feature.otherpicking.domain.repository

import com.hxb.myandroidapp_1021.feature.otherpicking.domain.model.OtherPickingConfirmRequest
import com.hxb.myandroidapp_1021.feature.otherpicking.domain.model.OtherPickingOrder

/**
 * 其他拣选数据入口；当前只有模拟实现，后续接入真实接口时保持同一接口。
 */
interface OtherPickingRepository {

    /**
     * 加载其他拣选单及其明细。
     * TODO: 字段未确定：查询条件、分页方式和接口字段名待与后端确认。
     */
    suspend fun loadOrders(): List<OtherPickingOrder>

    /**
     * 提交本批次拣选结果，返回是否提交成功。
     * TODO: 字段未确定：确认接口的请求与响应字段待后端确认。
     */
    suspend fun submitBatch(request: OtherPickingConfirmRequest): Boolean
}
