package com.hxb.myandroidapp_1021.feature.otherputaway.data.repository

import com.hxb.myandroidapp_1021.feature.otherputaway.domain.model.OtherPutawayConfirmRequest
import com.hxb.myandroidapp_1021.feature.otherputaway.domain.model.OtherPutawayDetail
import com.hxb.myandroidapp_1021.feature.otherputaway.domain.model.OtherPutawayOrder
import com.hxb.myandroidapp_1021.feature.otherputaway.domain.model.PutawayDetailStatus
import com.hxb.myandroidapp_1021.feature.otherputaway.domain.model.SourceType
import com.hxb.myandroidapp_1021.feature.otherputaway.domain.repository.OtherPutawayRepository
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import javax.inject.Inject

/**
 * 其他上架模拟仓库；模拟数据集中在本文件，变量统一以 mock 开头，便于接入真实接口时替换。
 */
class MockOtherPutawayRepository @Inject constructor() : OtherPutawayRepository {

    private val mockOrders = listOf(
        mockOrder(
            putawayOrderNo = "SJ20261111001",
            upstreamNo = "QT20261111001",
            upstreamType = SourceType.TEMPORARY,
            createdAt = "2026-11-11 09:30",
            completedLineNos = emptySet()
        ),
        mockOrder(
            putawayOrderNo = "SJ20261111002",
            upstreamNo = "QT20261111002",
            upstreamType = SourceType.INSPECTION,
            createdAt = "2026-11-11 14:10",
            completedLineNos = setOf(3)
        ),
        mockOrder(
            putawayOrderNo = "SJ20261111003",
            upstreamNo = "QT20261111003",
            upstreamType = SourceType.REPAIR,
            createdAt = "2026-11-12 10:05",
            completedLineNos = setOf(3)
        ),
        mockOrder(
            putawayOrderNo = "SJ20261111004",
            upstreamNo = "QT20261111004",
            upstreamType = SourceType.SCRAP,
            createdAt = "2026-11-12 15:40",
            completedLineNos = setOf(3)
        ),
        mockOrder(
            putawayOrderNo = "SJ20261111005",
            upstreamNo = "QT20261111005",
            upstreamType = SourceType.RETURN,
            createdAt = "2026-11-13 08:20",
            completedLineNos = setOf(1, 2, 3)
        )
    )

    override suspend fun loadOrders(): List<OtherPutawayOrder> {
        delay(500.milliseconds)
        return mockOrders
    }

    override suspend fun submitPutaway(request: OtherPutawayConfirmRequest): Boolean {
        // TODO: 字段未确定：接入真实接口后按响应结果返回成功或失败。
        delay(800.milliseconds)
        return true
    }

    /**
     * 构造模拟上架单：三条明细固定，已完成行号用于模拟部分上架与全部完成。
     */
    private fun mockOrder(
        putawayOrderNo: String,
        upstreamNo: String,
        upstreamType: SourceType,
        createdAt: String,
        completedLineNos: Set<Int>
    ): OtherPutawayOrder {
        val details = listOf(
            OtherPutawayDetail(
                lineNo = 1,
                rfid = "$putawayOrderNo-01",
                itemCode = "JYT-12",
                description = "绝缘扳手",
                unit = "把",
                location = "总装库 / A-01-01"
            ),
            OtherPutawayDetail(
                lineNo = 2,
                rfid = "$putawayOrderNo-02",
                itemCode = "DMM-01",
                description = "数字万用表",
                unit = "台",
                location = "总装库 / A-01-02"
            ),
            OtherPutawayDetail(
                lineNo = 3,
                rfid = "$putawayOrderNo-03",
                itemCode = "CAL-150",
                description = "游标卡尺",
                unit = "把",
                location = "计量库 / B-02-04"
            )
        ).map { detail ->
            if (detail.lineNo in completedLineNos) {
                detail.copy(actualLocation = detail.location, status = PutawayDetailStatus.COMPLETED)
            } else {
                detail
            }
        }
        return OtherPutawayOrder(
            putawayOrderNo = putawayOrderNo,
            upstreamType = upstreamType,
            upstreamNo = upstreamNo,
            createdAt = createdAt,
            details = details
        )
    }
}
