package com.hxb.myandroidapp_1021.feature.tempborrow.domain.repository

import com.hxb.myandroidapp_1021.feature.tempborrow.domain.model.TempBorrowConfirmRequest
import com.hxb.myandroidapp_1021.feature.tempborrow.domain.model.TempBorrowCreateRequest
import com.hxb.myandroidapp_1021.feature.tempborrow.domain.model.TempBorrowInventoryItem
import com.hxb.myandroidapp_1021.feature.tempborrow.domain.model.TempBorrowOrder

interface TempBorrowRepository {
    suspend fun loadOrders(): List<TempBorrowOrder>

    suspend fun loadInventory(): List<TempBorrowInventoryItem>

    /**
     * 新建单据；模拟实现返回带单号和明细的完整单据，正式单号应由后端生成。
     */
    suspend fun createOrder(request: TempBorrowCreateRequest): TempBorrowOrder

    suspend fun submitBorrow(request: TempBorrowConfirmRequest): Boolean
}
