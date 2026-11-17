package com.hxb.myandroidapp_1021.domain.stockdetail

import com.hxb.myandroidapp_1021.domain.common.QueryData
import com.hxb.myandroidapp_1021.domain.common.Repository

interface StockDetailRepository : Repository {
    suspend fun records(): QueryData<List<StockDetail>>
    suspend fun changeCondition(rfid: String, condition: String): StockDetail
}
