package com.hxb.myandroidapp_1021.feature.tempreturn.domain.repository

import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnConfirmRequest
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnCreateRequest
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnOrder
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnPendingTool

interface TempReturnRepository {
    suspend fun loadOrders(): List<TempReturnOrder>

    suspend fun loadPendingTools(): List<TempReturnPendingTool>

    suspend fun createOrder(request: TempReturnCreateRequest): TempReturnOrder

    suspend fun submitReturn(request: TempReturnConfirmRequest): Boolean
}
