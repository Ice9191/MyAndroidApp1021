package com.hxb.myandroidapp_1021.feature.picking.domain.repository

import com.hxb.myandroidapp_1021.feature.picking.domain.model.PickingConfirmRequest
import com.hxb.myandroidapp_1021.feature.picking.domain.model.PickingOrder

interface PickingRepository {
    /**
     * 返回待处理的拣选单及其明细。
     */
    suspend fun loadOrders(): List<PickingOrder>

    /**
     * 返回拣选作业页的单据及明细。
     */
    suspend fun loadScanOrder(): PickingOrder

    /**
     * 提交本批次拣选，返回是否成功。
     */
    suspend fun submitPicking(request: PickingConfirmRequest): Boolean
}
