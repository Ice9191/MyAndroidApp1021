package com.hxb.myandroidapp_1021.feature.returns.data.repository

import com.hxb.myandroidapp_1021.feature.returns.domain.model.ReturnConfirmRequest
import com.hxb.myandroidapp_1021.feature.returns.domain.model.ReturnDetail
import com.hxb.myandroidapp_1021.feature.returns.domain.model.ReturnDetailStatus
import com.hxb.myandroidapp_1021.feature.returns.domain.model.ReturnOrder
import com.hxb.myandroidapp_1021.feature.returns.domain.model.toReturnOrderStatus
import com.hxb.myandroidapp_1021.feature.returns.domain.repository.ReturnRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

/**
 * 归还上架的模拟数据源：列表、扫描单据和上架提交都走与正式接口相同的方法。
 * 上架单货位是单据目标货位，明细的实际货位允许与单据货位不同。
 * TODO: 字段未确定，模拟值需在接口字段确认后替换，时间格式需与后端统一。
 */
class MockReturnRepository @Inject constructor() : ReturnRepository {
    private val mockOrders = listOf(
        order(
            returnOrderNo = "GH20261111001",
            orderNo = "SJ20261111001",
            workOrderNo = "GD20261111001",
            putawayBy = "张伟",
            putawayAt = "2026-11-11 10:03",
            location = "A-01-02-01",
            createdAt = "2026-11-11 08:00",
            updatedBy = "张伟",
            updatedAt = "2026-11-11 10:03",
            details = listOf(
                detail(
                    orderNo = "SJ20261111001",
                    lineNo = 1,
                    rfid = "SJ202610270002-002",
                    itemCode = "300202",
                    description = "携带型接地线 110kV",
                    unit = "组",
                    createdAt = "2026-11-11 08:00",
                    actualLocation = "A-01-02-01",
                    updatedBy = "张伟",
                    updatedAt = "2026-11-11 10:01",
                    status = ReturnDetailStatus.COMPLETED
                ),
                detail(
                    orderNo = "SJ20261111001",
                    lineNo = 2,
                    rfid = "SJ202610270002-003",
                    itemCode = "300203",
                    description = "绝缘操作杆",
                    unit = "件",
                    createdAt = "2026-11-11 08:00",
                    actualLocation = "A-01-02-02"
                ),
                detail(
                    orderNo = "SJ20261111001",
                    lineNo = 3,
                    rfid = "SJ202610270002-004",
                    itemCode = "300204",
                    description = "安全帽",
                    unit = "个",
                    createdAt = "2026-11-11 08:00",
                    actualLocation = "B-02-01-03",
                    updatedBy = "张伟",
                    updatedAt = "2026-11-11 10:03",
                    status = ReturnDetailStatus.COMPLETED
                ),
                detail(
                    orderNo = "SJ20261111001",
                    lineNo = 4,
                    rfid = "SJ202610270002-005",
                    itemCode = "300205",
                    description = "验电器 10kV",
                    unit = "支",
                    createdAt = "2026-11-11 08:00",
                    actualLocation = "A-01-02-03"
                ),
                detail(
                    orderNo = "SJ20261111001",
                    lineNo = 5,
                    rfid = "SJ202610270002-006",
                    itemCode = "300206",
                    description = "绝缘手套",
                    unit = "双",
                    createdAt = "2026-11-11 08:00",
                    actualLocation = "A-01-02-04"
                )
            )
        ),
        order(
            returnOrderNo = "GH20261112001",
            orderNo = "SJ20261112001",
            workOrderNo = "GD20261112001",
            location = "A-01-01-01",
            createdAt = "2026-11-12 08:00",
            updatedAt = "2026-11-12 08:00",
            details = listOf(
                detail(
                    orderNo = "SJ20261112001",
                    lineNo = 1,
                    rfid = "SJ202610280001-001",
                    itemCode = "300301",
                    description = "安全带",
                    unit = "副",
                    createdAt = "2026-11-12 08:00",
                    actualLocation = "A-01-01-01"
                ),
                detail(
                    orderNo = "SJ20261112001",
                    lineNo = 2,
                    rfid = "SJ202610280001-002",
                    itemCode = "300302",
                    description = "脚扣",
                    unit = "副",
                    createdAt = "2026-11-12 08:00",
                    actualLocation = "A-01-01-02"
                ),
                detail(
                    orderNo = "SJ20261112001",
                    lineNo = 3,
                    rfid = "SJ202610280001-003",
                    itemCode = "300303",
                    description = "验电器 35kV",
                    unit = "支",
                    createdAt = "2026-11-12 08:00",
                    actualLocation = "A-01-01-03"
                )
            )
        ),
        order(
            returnOrderNo = "GH20261110001",
            orderNo = "SJ20261110001",
            workOrderNo = "GD20261110001",
            putawayBy = "王健",
            putawayAt = "2026-11-10 15:05",
            location = "C-02-01-01",
            createdAt = "2026-11-10 14:10",
            updatedBy = "王健",
            updatedAt = "2026-11-10 15:05",
            details = listOf(
                detail(
                    orderNo = "SJ20261110001",
                    lineNo = 1,
                    rfid = "SJ202610290001-001",
                    itemCode = "300401",
                    description = "力矩扳手",
                    unit = "把",
                    createdAt = "2026-11-10 14:10",
                    actualLocation = "C-02-01-01",
                    updatedBy = "王健",
                    updatedAt = "2026-11-10 15:02",
                    status = ReturnDetailStatus.COMPLETED
                ),
                detail(
                    orderNo = "SJ20261110001",
                    lineNo = 2,
                    rfid = "SJ202610290001-002",
                    itemCode = "300402",
                    description = "万用表",
                    unit = "台",
                    createdAt = "2026-11-10 14:10",
                    actualLocation = "C-02-01-02",
                    updatedBy = "王健",
                    updatedAt = "2026-11-10 15:05",
                    status = ReturnDetailStatus.COMPLETED
                )
            )
        ),
        order(
            returnOrderNo = "GH20261113001",
            orderNo = "SJ20261113001",
            workOrderNo = "GD20261113001",
            putawayBy = "李强",
            putawayAt = "2026-11-13 09:30",
            location = "A-02-01-01",
            createdAt = "2026-11-13 08:00",
            updatedBy = "李强",
            updatedAt = "2026-11-13 09:30",
            details = listOf(
                detail(
                    orderNo = "SJ20261113001",
                    lineNo = 1,
                    rfid = "SJ202611130001-001",
                    itemCode = "300501",
                    description = "扭力扳手",
                    unit = "把",
                    createdAt = "2026-11-13 08:00",
                    actualLocation = "A-02-01-01",
                    updatedBy = "李强",
                    updatedAt = "2026-11-13 09:20",
                    status = ReturnDetailStatus.COMPLETED
                ),
                detail(
                    orderNo = "SJ20261113001",
                    lineNo = 2,
                    rfid = "SJ202611130001-002",
                    itemCode = "300502",
                    description = "绝缘靴",
                    unit = "双",
                    createdAt = "2026-11-13 08:00",
                    actualLocation = "A-02-01-02",
                    updatedBy = "李强",
                    updatedAt = "2026-11-13 09:25",
                    status = ReturnDetailStatus.COMPLETED
                ),
                detail(
                    orderNo = "SJ20261113001",
                    lineNo = 3,
                    rfid = "SJ202611130001-003",
                    itemCode = "300503",
                    description = "验电笔",
                    unit = "支",
                    createdAt = "2026-11-13 08:00",
                    actualLocation = "A-02-01-03"
                ),
                detail(
                    orderNo = "SJ20261113001",
                    lineNo = 4,
                    rfid = "SJ202611130001-004",
                    itemCode = "300504",
                    description = "安全绳",
                    unit = "根",
                    createdAt = "2026-11-13 08:00",
                    status = ReturnDetailStatus.DAMAGED
                ),
                detail(
                    orderNo = "SJ20261113001",
                    lineNo = 5,
                    rfid = "SJ202611130001-005",
                    itemCode = "300505",
                    description = "绝缘梯",
                    unit = "架",
                    createdAt = "2026-11-13 08:00",
                    status = ReturnDetailStatus.PENDING_INSPECTION
                )
            )
        ),
        order(
            returnOrderNo = "GH20261114001",
            orderNo = "SJ20261114001",
            workOrderNo = "GD20261114001",
            putawayBy = "李强",
            putawayAt = "2026-11-14 10:10",
            location = "B-03-02-01",
            createdAt = "2026-11-14 08:00",
            updatedBy = "李强",
            updatedAt = "2026-11-14 10:10",
            details = listOf(
                detail(
                    orderNo = "SJ20261114001",
                    lineNo = 1,
                    rfid = "SJ202611140001-001",
                    itemCode = "300601",
                    description = "高压验电器",
                    unit = "支",
                    createdAt = "2026-11-14 08:00",
                    actualLocation = "B-03-02-01",
                    updatedBy = "李强",
                    updatedAt = "2026-11-14 10:10",
                    status = ReturnDetailStatus.COMPLETED
                ),
                detail(
                    orderNo = "SJ20261114001",
                    lineNo = 2,
                    rfid = "SJ202611140001-002",
                    itemCode = "300602",
                    description = "接地棒",
                    unit = "组",
                    createdAt = "2026-11-14 08:00",
                    actualLocation = "B-03-02-02"
                ),
                detail(
                    orderNo = "SJ20261114001",
                    lineNo = 3,
                    rfid = "SJ202611140001-003",
                    itemCode = "300603",
                    description = "防护眼镜",
                    unit = "副",
                    createdAt = "2026-11-14 08:00",
                    actualLocation = "B-03-02-03"
                ),
                detail(
                    orderNo = "SJ20261114001",
                    lineNo = 4,
                    rfid = "SJ202611140001-004",
                    itemCode = "300604",
                    description = "绝缘手套",
                    unit = "双",
                    createdAt = "2026-11-14 08:00",
                    status = ReturnDetailStatus.DAMAGED
                )
            )
        ),
        order(
            returnOrderNo = "GH20261115001",
            orderNo = "SJ20261115001",
            workOrderNo = "GD20261115001",
            location = "C-03-01-01",
            createdAt = "2026-11-15 08:00",
            updatedAt = "2026-11-15 08:00",
            details = listOf(
                detail(
                    orderNo = "SJ20261115001",
                    lineNo = 1,
                    rfid = "SJ202611150001-001",
                    itemCode = "300701",
                    description = "万用表",
                    unit = "台",
                    createdAt = "2026-11-15 08:00",
                    actualLocation = "C-03-01-01"
                ),
                detail(
                    orderNo = "SJ20261115001",
                    lineNo = 2,
                    rfid = "SJ202611150001-002",
                    itemCode = "300702",
                    description = "钳形电流表",
                    unit = "台",
                    createdAt = "2026-11-15 08:00",
                    status = ReturnDetailStatus.PENDING_INSPECTION
                ),
                detail(
                    orderNo = "SJ20261115001",
                    lineNo = 3,
                    rfid = "SJ202611150001-003",
                    itemCode = "300703",
                    description = "绝缘杆",
                    unit = "根",
                    createdAt = "2026-11-15 08:00",
                    status = ReturnDetailStatus.DAMAGED
                )
            )
        )
    )

    private val mockScanOrder = mockOrders.first()

    override suspend fun loadOrders(): List<ReturnOrder> {
        delay(500.milliseconds)
        return mockOrders
    }

    override suspend fun loadScanOrder(): ReturnOrder {
        delay(500.milliseconds)
        return mockScanOrder
    }

    override suspend fun submitPutaway(request: ReturnConfirmRequest): Boolean {
        delay(800.milliseconds)
        return true
    }

    private fun order(
        orderNo: String,
        returnOrderNo: String,
        workOrderNo: String,
        createdAt: String,
        updatedAt: String,
        putawayBy: String = "",
        putawayAt: String = "",
        location: String = "",
        updatedBy: String = "材料员",
        details: List<ReturnDetail>
    ): ReturnOrder = ReturnOrder(
        orderNo = orderNo,
        returnOrderNo = returnOrderNo,
        workOrderNo = workOrderNo,
        createdBy = "材料员",
        createdAt = createdAt,
        updatedBy = updatedBy,
        updatedAt = updatedAt,
        putawayBy = putawayBy,
        putawayAt = putawayAt,
        location = location,
        status = details.toReturnOrderStatus(),
        details = details
    )

    /**
     * 构造明细：待上架明细的 actualLocation 是系统推荐货位（初始不为空），
     * 损坏/待送检明细才为空；状态由调用方显式传入，不能再用 actualLocation 是否为空反推。
     */
    private fun detail(
        orderNo: String,
        lineNo: Int,
        rfid: String,
        itemCode: String,
        description: String,
        unit: String,
        createdAt: String,
        actualLocation: String = "",
        updatedBy: String = "材料员",
        updatedAt: String = createdAt,
        status: ReturnDetailStatus = ReturnDetailStatus.PENDING
    ): ReturnDetail = ReturnDetail(
        orderNo = orderNo,
        lineNo = lineNo,
        rfid = rfid,
        itemCode = itemCode,
        description = description,
        unit = unit,
        createdBy = "材料员",
        createdAt = createdAt,
        updatedBy = updatedBy,
        updatedAt = updatedAt,
        actualLocation = actualLocation,
        status = status
    )
}
