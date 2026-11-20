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

    /**
     * 确认单件借出并保存整单；最后一条明细确认后同一事务生成对应归还单，返回更新后的借出单。
     * 找不到单据时返回 null。
     */
    suspend fun submitBorrow(request: TempBorrowConfirmRequest): TempBorrowOrder?

    /**
     * 借出信息中可选的基础档案候选项；正式数据由后端基础档案接口提供。
     * TODO: 字段未确定：车间、工班、人员的接口字段、编码规则及层级关系需后端确认。
     */
    suspend fun loadWorkshops(): List<String>

    suspend fun loadWorkGroups(): List<String>

    suspend fun loadBorrowers(): List<String>
}
