package com.hxb.myandroidapp_1021.feature.otherpicking.data.repository

import com.hxb.myandroidapp_1021.feature.otherpicking.domain.model.OtherPickingConfirmRequest
import com.hxb.myandroidapp_1021.feature.otherpicking.domain.model.OtherPickingDetail
import com.hxb.myandroidapp_1021.feature.otherpicking.domain.model.OtherPickingOrder
import com.hxb.myandroidapp_1021.feature.otherpicking.domain.model.PickingDetailStatus
import com.hxb.myandroidapp_1021.feature.otherpicking.domain.model.SourceType
import com.hxb.myandroidapp_1021.feature.otherpicking.domain.repository.OtherPickingRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

/**
 * 其他拣选的模拟数据实现，数据仅用于页面联调。
 * TODO: 字段未确定：拣选单号、车间、工班、计划日期为页面需要的展示字段，真实接口待后端确认。
 */
class MockOtherPickingRepository @Inject constructor() : OtherPickingRepository {

    private val mockOrders = listOf(
        OtherPickingOrder(
            pickingOrderNo = "XJ20261111001",
            upstreamType = SourceType.TEMPORARY,
            upstreamNo = "QT20261111001",
            workshop = "总装车间",
            workGroup = "装配一班",
            plannedDate = "2026-11-11",
            details = mockTools()
        ),
        OtherPickingOrder(
            pickingOrderNo = "XJ20261111002",
            upstreamType = SourceType.INSPECTION,
            upstreamNo = "QT20261111002",
            workshop = "总装车间",
            workGroup = "装配二班",
            plannedDate = "2026-11-11",
            details = mockTools(pickedLineNos = setOf(3))
        ),
        OtherPickingOrder(
            pickingOrderNo = "XJ20261111003",
            upstreamType = SourceType.REPAIR,
            upstreamNo = "QT20261111003",
            workshop = "检修车间",
            workGroup = "维修一班",
            plannedDate = "2026-11-12",
            details = mockTools(pickedLineNos = setOf(3))
        ),
        OtherPickingOrder(
            pickingOrderNo = "XJ20261111004",
            upstreamType = SourceType.SCRAP,
            upstreamNo = "QT20261111004",
            workshop = "检修车间",
            workGroup = "维修二班",
            plannedDate = "2026-11-12",
            details = mockTools(pickedLineNos = setOf(3))
        ),
        OtherPickingOrder(
            pickingOrderNo = "XJ20261111005",
            upstreamType = SourceType.RETURN,
            upstreamNo = "QT20261111005",
            workshop = "仓储中心",
            workGroup = "退货班",
            plannedDate = "2026-11-13",
            details = mockTools(pickedLineNos = setOf(1, 2, 3))
        )
    )

    override suspend fun loadOrders(): List<OtherPickingOrder> {
        delay(500.milliseconds)
        return mockOrders
    }

    override suspend fun submitBatch(request: OtherPickingConfirmRequest): Boolean {
        // TODO: 字段未确定：接入真实接口后按响应结果返回成功或失败。
        delay(800.milliseconds)
        return true
    }

    /**
     * 生成一套模拟明细，pickedLineNos 指定已拣选的行号，便于展示不同单据状态。
     */
    private fun mockTools(pickedLineNos: Set<Int> = emptySet()): List<OtherPickingDetail> {
        val tools = listOf(
            OtherPickingDetail(
                lineNo = 1,
                rfid = "E280 0001 0001",
                itemCode = "JYT-12",
                description = "绝缘扳手",
                unit = "把",
                batchNo = "B20261001",
                warehouse = "总装库",
                location = "A-01-01"
            ),
            OtherPickingDetail(
                lineNo = 2,
                rfid = "E280 0001 0002",
                itemCode = "DMM-01",
                description = "数字万用表",
                unit = "台",
                batchNo = "B20261002",
                warehouse = "总装库",
                location = "A-01-02"
            ),
            OtherPickingDetail(
                lineNo = 3,
                rfid = "E280 0001 0003",
                itemCode = "CAL-150",
                description = "游标卡尺",
                unit = "把",
                batchNo = "B20261003",
                warehouse = "计量库",
                location = "B-02-04"
            )
        )
        return tools.map { tool ->
            if (tool.lineNo in pickedLineNos) {
                tool.copy(status = PickingDetailStatus.PICKED)
            } else {
                tool
            }
        }
    }
}
