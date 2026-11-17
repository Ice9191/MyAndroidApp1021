package com.hxb.myandroidapp_1021.domain.temp

import com.hxb.myandroidapp_1021.domain.common.QueryData
import com.hxb.myandroidapp_1021.domain.common.Repository

interface TempRepository : Repository {
    suspend fun borrowOrders(): QueryData<List<TempBorrowOrder>>
    suspend fun returnOrders(): QueryData<List<TempReturnOrder>>
    suspend fun availableTools(): QueryData<List<TempTool>>
    suspend fun create(request: TempBorrowRequest): TempBorrowOrder
    suspend fun borrow(request: TempBorrowConfirmRequest): TempBorrowOrder
    suspend fun returnTool(request: TempReturnConfirmRequest): TempReturnOrder
}
