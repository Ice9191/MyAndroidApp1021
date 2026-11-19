package com.hxb.myandroidapp_1021.feature.otherputaway.domain.repository

import com.hxb.myandroidapp_1021.feature.otherputaway.domain.model.OtherPutawayConfirmRequest
import com.hxb.myandroidapp_1021.feature.otherputaway.domain.model.OtherPutawayOrder

/**
 * 其他上架数据来源；页面只依赖本接口，模拟实现位于 data 层。
 */
interface OtherPutawayRepository {

    /**
     * 查询其他上架任务及明细。
     * TODO: 字段未确定：查询条件、分页方式和接口字段名待与后端确认。
     */
    suspend fun loadOrders(): List<OtherPutawayOrder>

    /**
     * 提交单件上架确认；返回 true 表示提交成功。
     * TODO: 字段未确定：提交接口的字段名与返回结构待与后端确认。
     */
    suspend fun submitPutaway(request: OtherPutawayConfirmRequest): Boolean
}
