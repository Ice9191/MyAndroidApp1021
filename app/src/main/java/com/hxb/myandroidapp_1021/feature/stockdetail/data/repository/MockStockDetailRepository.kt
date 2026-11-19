package com.hxb.myandroidapp_1021.feature.stockdetail.data.repository

import com.hxb.myandroidapp_1021.feature.stockdetail.domain.model.StockDetail
import com.hxb.myandroidapp_1021.feature.stockdetail.domain.repository.StockDetailRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

/**
 * 库存明细的本地模拟数据源，仅用于界面联调，不包含真实查询条件。
 */
class MockStockDetailRepository @Inject constructor() : StockDetailRepository {
    // TODO: 字段未确定：库存查询接口及锁定的默认仓库来源；暂用同一仓库的模拟单件记录。
    private val mockRecords = listOf(
        StockDetail(
            "E2801160600002094A5B6C92",
            "270200000003",
            "液压压接钳 16-300mm²",
            "16-300mm²",
            "把",
            "工器具",
            "已备货",
            "好件",
            null,
            "二号库",
            "C-02-01-05",
            true,
            "12个月"
        ),
        StockDetail(
            "R-00000141",
            "270000000001",
            "活动扳手",
            "12寸",
            "把",
            "工器具",
            "在库",
            "好件",
            null,
            "二号库",
            "A-01-03",
            false,
            null
        ),
        StockDetail(
            "R-00000143",
            "280000000001",
            "数显游标卡尺",
            "0-150mm",
            "把",
            "量具",
            "在库",
            "坏件",
            "显示异常，待核查",
            "二号库",
            "B-02-04",
            true,
            "12个月"
        )
    )

    override suspend fun loadRecords(): List<StockDetail> {
        delay(500.milliseconds)
        return mockRecords
    }
}
