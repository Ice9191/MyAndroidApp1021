package com.hxb.myandroidapp_1021.feature.putaway.domain.repository

import com.hxb.myandroidapp_1021.feature.putaway.domain.model.PutawayLocation
import com.hxb.myandroidapp_1021.feature.putaway.domain.model.PutawayOrder
import com.hxb.myandroidapp_1021.feature.putaway.domain.model.PutawayRequest

interface PutawayRepository {
    /**
     * 返回待处理的上架单及明细。
     */
    suspend fun loadOrders(): List<PutawayOrder>

    /**
     * 返回当前上架操作人。
     */
    suspend fun loadOperator(): String

    /**
     * 按货位码查询货位；不存在时返回 null。
     */
    suspend fun validateLocation(code: String): PutawayLocation?

    /**
     * 提交单件上架，返回是否成功。
     */
    suspend fun confirmPutaway(request: PutawayRequest): Boolean
}
