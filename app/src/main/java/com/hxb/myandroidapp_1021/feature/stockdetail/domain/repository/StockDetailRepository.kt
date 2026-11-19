package com.hxb.myandroidapp_1021.feature.stockdetail.domain.repository

import com.hxb.myandroidapp_1021.feature.stockdetail.domain.model.StockDetail

/**
 * 库存明细查询入口；明细由后端按当前仓库返回，PDA 只查询展示并模拟好坏件变更。
 */
interface StockDetailRepository {
    /**
     * 查询库存明细记录。
     * TODO: 字段未确定：查询条件、分页方式和接口字段名待与后端确认。
     */
    suspend fun loadRecords(): List<StockDetail>
}
