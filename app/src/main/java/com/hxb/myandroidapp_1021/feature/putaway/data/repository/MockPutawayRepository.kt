package com.hxb.myandroidapp_1021.feature.putaway.data.repository

import com.hxb.myandroidapp_1021.feature.putaway.domain.model.PutawayDetail
import com.hxb.myandroidapp_1021.feature.putaway.domain.model.PutawayDetailStatus
import com.hxb.myandroidapp_1021.feature.putaway.domain.model.PutawayLocation
import com.hxb.myandroidapp_1021.feature.putaway.domain.model.PutawayOrder
import com.hxb.myandroidapp_1021.feature.putaway.domain.model.PutawayOrderStatus
import com.hxb.myandroidapp_1021.feature.putaway.domain.model.PutawayRequest
import com.hxb.myandroidapp_1021.feature.putaway.domain.repository.PutawayRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class MockPutawayRepository @Inject constructor() : PutawayRepository {
    // 模拟建档绑定用 EPC 中的可打印 ASCII 文本作为业务 RFID 编码。
    private val mockRfidBindings = mapOf(
        "475730303430303034000000" to "GW0040004",
        "475730303430303035000000" to "GW0040005",
        "475730303430303033000000" to "GW0040003",
        "475730303430303032000000" to "GW0040002"
    )

    override suspend fun findRfidByEpc(epc: String): String? = mockRfidBindings[epc]

    private val mockOrders = listOf(
        PutawayOrder(
            inboundOrderNo = "RK20261111001",
            orderNo = "SJ20261111001",
            status = PutawayOrderStatus.PARTIALLY,
            operator = "王芳",
            putawayTime = "2026-11-11 09:00",
            details = listOf(
                PutawayDetail(
                    orderNo = "SJ20261111001",
                    inboundLineNo = "1",
                    lineNo = "1",
                    status = PutawayDetailStatus.PENDING,
                    batchNo = "PC001",
                    rfid = "GW0040004",
                    itemCode = "GJ-001",
                    description = "绝缘扳手",
                    unit = "件",
                    warehouse = "中心工具库"
                ),
                PutawayDetail(
                    orderNo = "SJ20261111001",
                    inboundLineNo = "1",
                    lineNo = "2",
                    status = PutawayDetailStatus.PENDING,
                    batchNo = "PC001",
                    rfid = "GW0040005",
                    itemCode = "GJ-001",
                    description = "绝缘扳手",
                    unit = "件",
                    warehouse = "中心工具库"
                ),
                PutawayDetail(
                    orderNo = "SJ20261111001",
                    inboundLineNo = "2",
                    lineNo = "3",
                    status = PutawayDetailStatus.COMPLETED,
                    batchNo = "PC002",
                    rfid = "E280 0003",
                    itemCode = "GJ-002",
                    description = "数字万用表",
                    unit = "件",
                    warehouse = "中心工具库"
                )
            )
        ),
        PutawayOrder(
            inboundOrderNo = "RK20261111002",
            orderNo = "SJ20261111002",
            status = PutawayOrderStatus.PENDING,
            operator = "",
            putawayTime = "",
            details = listOf(
                PutawayDetail(
                    orderNo = "SJ20261111002",
                    inboundLineNo = "1",
                    lineNo = "1",
                    status = PutawayDetailStatus.PENDING,
                    batchNo = "PC003",
                    rfid = "GW0040003",
                    itemCode = "GJ-003",
                    description = "验电器",
                    unit = "件",
                    warehouse = "电气工具库"
                ),
                PutawayDetail(
                    orderNo = "SJ20261111002",
                    inboundLineNo = "2",
                    lineNo = "2",
                    status = PutawayDetailStatus.PENDING,
                    batchNo = "PC003",
                    rfid = "GW0040002",
                    itemCode = "GJ-004",
                    description = "绝缘手套",
                    unit = "件",
                    warehouse = "电气工具库"
                )
            )
        ),
        PutawayOrder(
            inboundOrderNo = "RK20261111003",
            orderNo = "SJ20261111003",
            status = PutawayOrderStatus.COMPLETED,
            operator = "张三",
            putawayTime = "2026-11-11 08:30",
            details = listOf(
                PutawayDetail(
                    orderNo = "SJ20261111003",
                    inboundLineNo = "1",
                    lineNo = "1",
                    status = PutawayDetailStatus.COMPLETED,
                    batchNo = "PC004",
                    rfid = "E280 0006",
                    itemCode = "GJ-005",
                    description = "游标卡尺",
                    unit = "件",
                    warehouse = "中心工具库"
                )
            )
        ),
        PutawayOrder(
            inboundOrderNo = "RK20261111004",
            orderNo = "SJ20261111004",
            status = PutawayOrderStatus.PENDING,
            operator = "",
            putawayTime = "",
            details = listOf(
                PutawayDetail(
                    orderNo = "SJ20261111004",
                    inboundLineNo = "1",
                    lineNo = "1",
                    status = PutawayDetailStatus.PENDING,
                    batchNo = "PC005",
                    rfid = "E280 0007",
                    itemCode = "GJ-006",
                    description = "液压钳",
                    unit = "件",
                    warehouse = "电气工具库"
                ),
                PutawayDetail(
                    orderNo = "SJ20261111004",
                    inboundLineNo = "1",
                    lineNo = "2",
                    status = PutawayDetailStatus.PENDING,
                    batchNo = "PC005",
                    rfid = "E280 0008",
                    itemCode = "GJ-006",
                    description = "液压钳",
                    unit = "件",
                    warehouse = "电气工具库"
                ),
                PutawayDetail(
                    orderNo = "SJ20261111004",
                    inboundLineNo = "2",
                    lineNo = "3",
                    status = PutawayDetailStatus.PENDING,
                    batchNo = "PC006",
                    rfid = "E280 0009",
                    itemCode = "GJ-007",
                    description = "剥线钳",
                    unit = "件",
                    warehouse = "电气工具库"
                )
            )
        ),
        PutawayOrder(
            inboundOrderNo = "RK20261111005",
            orderNo = "SJ20261111005",
            status = PutawayOrderStatus.PARTIALLY,
            operator = "李强",
            putawayTime = "2026-11-11 10:20",
            details = listOf(
                PutawayDetail(
                    orderNo = "SJ20261111005",
                    inboundLineNo = "1",
                    lineNo = "1",
                    status = PutawayDetailStatus.COMPLETED,
                    batchNo = "PC007",
                    rfid = "E280 0010",
                    itemCode = "GJ-008",
                    description = "卷尺",
                    unit = "件",
                    warehouse = "中心工具库"
                ),
                PutawayDetail(
                    orderNo = "SJ20261111005",
                    inboundLineNo = "2",
                    lineNo = "2",
                    status = PutawayDetailStatus.COMPLETED,
                    batchNo = "PC007",
                    rfid = "E280 0011",
                    itemCode = "GJ-008",
                    description = "卷尺",
                    unit = "件",
                    warehouse = "中心工具库"
                ),
                PutawayDetail(
                    orderNo = "SJ20261111005",
                    inboundLineNo = "3",
                    lineNo = "3",
                    status = PutawayDetailStatus.PENDING,
                    batchNo = "PC008",
                    rfid = "E280 0012",
                    itemCode = "GJ-009",
                    description = "水平仪",
                    unit = "件",
                    warehouse = "中心工具库"
                )
            )
        ),
        PutawayOrder(
            inboundOrderNo = "RK20261111006",
            orderNo = "SJ20261111006",
            status = PutawayOrderStatus.PENDING,
            operator = "",
            putawayTime = "",
            details = listOf(
                PutawayDetail(
                    orderNo = "SJ20261111006",
                    inboundLineNo = "1",
                    lineNo = "1",
                    status = PutawayDetailStatus.PENDING,
                    batchNo = "PC009",
                    rfid = "E280 0013",
                    itemCode = "GJ-010",
                    description = "万用表笔",
                    unit = "件",
                    warehouse = "电气工具库"
                ),
                PutawayDetail(
                    orderNo = "SJ20261111006",
                    inboundLineNo = "2",
                    lineNo = "2",
                    status = PutawayDetailStatus.PENDING,
                    batchNo = "PC009",
                    rfid = "E280 0014",
                    itemCode = "GJ-011",
                    description = "尖嘴钳",
                    unit = "件",
                    warehouse = "电气工具库"
                )
            )
        ),
        PutawayOrder(
            inboundOrderNo = "RK20261111007",
            orderNo = "SJ20261111007",
            status = PutawayOrderStatus.COMPLETED,
            operator = "赵敏",
            putawayTime = "2026-11-11 11:10",
            details = listOf(
                PutawayDetail(
                    orderNo = "SJ20261111007",
                    inboundLineNo = "1",
                    lineNo = "1",
                    status = PutawayDetailStatus.COMPLETED,
                    batchNo = "PC010",
                    rfid = "E280 0015",
                    itemCode = "GJ-012",
                    description = "内六角扳手",
                    unit = "件",
                    warehouse = "中心工具库"
                ),
                PutawayDetail(
                    orderNo = "SJ20261111007",
                    inboundLineNo = "2",
                    lineNo = "2",
                    status = PutawayDetailStatus.COMPLETED,
                    batchNo = "PC010",
                    rfid = "E280 0016",
                    itemCode = "GJ-013",
                    description = "活动扳手",
                    unit = "件",
                    warehouse = "中心工具库"
                )
            )
        )
    )
    private val mockOperator = "材料员"
    private val mockLocations = listOf(
        PutawayLocation("132222500364330582", "中心工具库"),
        PutawayLocation("A1-01", "中心工具库"),
        PutawayLocation("A1-02", "中心工具库"),
        PutawayLocation("A1-03", "中心工具库"),
        PutawayLocation("B1-01", "电气工具库"),
        PutawayLocation("B1-02", "电气工具库")
    )

    override suspend fun loadOrders(): List<PutawayOrder> {
        delay(500.milliseconds)
        return mockOrders
    }

    /**
     * 操作人正式实现应从登录凭证获取；当前 Mock 返回固定角色。
     * TODO: 字段未确定，确认登录态来源后替换。
     */
    override suspend fun loadOperator(): String {
        delay(500.milliseconds)
        return mockOperator
    }

    override suspend fun validateLocation(code: String): PutawayLocation? =
        mockLocations.firstOrNull { it.code == code }

    /**
     * 模拟 500 毫秒提交延迟，当前对货位和操作人都非空的请求返回成功。
     */
    override suspend fun confirmPutaway(request: PutawayRequest): Boolean {
        delay(500.milliseconds)
        return request.location.isNotBlank() && request.operator.isNotBlank()
    }
}
