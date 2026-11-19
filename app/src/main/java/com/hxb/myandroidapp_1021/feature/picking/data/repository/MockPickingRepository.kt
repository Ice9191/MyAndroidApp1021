package com.hxb.myandroidapp_1021.feature.picking.data.repository

import com.hxb.myandroidapp_1021.feature.picking.domain.model.PickingConfirmRequest
import com.hxb.myandroidapp_1021.feature.picking.domain.model.PickingDetail
import com.hxb.myandroidapp_1021.feature.picking.domain.model.PickingDetailStatus
import com.hxb.myandroidapp_1021.feature.picking.domain.model.PickingOrder
import com.hxb.myandroidapp_1021.feature.picking.domain.model.PickingStatus
import com.hxb.myandroidapp_1021.feature.picking.domain.repository.PickingRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class MockPickingRepository @Inject constructor() : PickingRepository {
    // 初始状态：全部拣选单和拣选单明细均为未拣选
    private val mockOrders = listOf(
        PickingOrder(
            borrowOrderNo = "JJ20261111001",
            orderNo = "JC20261028001",
            workshop = "架大修一车间",
            workGroup = "电气一班",
            borrower = "张三",
            plannedDate = "2026-11-11 09:30",
            createdBy = "材料员",
            createdAt = "2026-11-11 08:00",
            updatedBy = "材料员",
            updatedAt = "2026-11-11 08:00",
            status = PickingStatus.UNPICKED,
            details = listOf(
                PickingDetail(
                    orderNo = "JC20261028001",
                    lineNo = 1,
                    rfid = "E280 0001",
                    itemCode = "GJ-001",
                    description = "绝缘扳手",
                    unit = "件",
                    batchNo = "PC001",
                    warehouse = "中心工具库",
                    location = "A1-1",
                    createdBy = "材料员",
                    createdAt = "2026-11-11 08:00",
                    updatedBy = "材料员",
                    updatedAt = "2026-11-11 08:00",
                    status = PickingDetailStatus.UNPICKED
                ),
                PickingDetail(
                    orderNo = "JC20261028001",
                    lineNo = 2,
                    rfid = "E280 0002",
                    itemCode = "GJ-002",
                    description = "数字万用表",
                    unit = "件",
                    batchNo = "PC001",
                    warehouse = "中心工具库",
                    location = "A1-2",
                    createdBy = "材料员",
                    createdAt = "2026-11-11 08:00",
                    updatedBy = "材料员",
                    updatedAt = "2026-11-11 08:00",
                    status = PickingDetailStatus.UNPICKED
                ),
                PickingDetail(
                    orderNo = "JC20261028001",
                    lineNo = 3,
                    rfid = "E280 0003",
                    itemCode = "GJ-003",
                    description = "游标卡尺",
                    unit = "件",
                    batchNo = "PC002",
                    warehouse = "中心工具库",
                    location = "A1-3",
                    createdBy = "材料员",
                    createdAt = "2026-11-11 08:00",
                    updatedBy = "材料员",
                    updatedAt = "2026-11-11 08:00",
                    status = PickingDetailStatus.UNPICKED
                )
            )
        ),
        PickingOrder(
            borrowOrderNo = "JJ20261111002",
            orderNo = "JC20261028002",
            workshop = "架大修二车间",
            workGroup = "机械二班",
            borrower = "李四",
            plannedDate = "2026-11-11 10:00",
            createdBy = "材料员",
            createdAt = "2026-11-11 08:10",
            updatedBy = "材料员",
            updatedAt = "2026-11-11 08:10",
            status = PickingStatus.UNPICKED,
            details = listOf(
                PickingDetail(
                    orderNo = "JC20261028002",
                    lineNo = 1,
                    rfid = "E280 0004",
                    itemCode = "GJ-004",
                    description = "液压钳",
                    unit = "件",
                    batchNo = "PC003",
                    warehouse = "中心工具库",
                    location = "B1-1",
                    createdBy = "材料员",
                    createdAt = "2026-11-11 08:10",
                    updatedBy = "材料员",
                    updatedAt = "2026-11-11 08:10",
                    status = PickingDetailStatus.UNPICKED
                ),
                PickingDetail(
                    orderNo = "JC20261028002",
                    lineNo = 2,
                    rfid = "E280 0005",
                    itemCode = "GJ-005",
                    description = "剥线钳",
                    unit = "件",
                    batchNo = "PC003",
                    warehouse = "中心工具库",
                    location = "B1-2",
                    createdBy = "材料员",
                    createdAt = "2026-11-11 08:10",
                    updatedBy = "材料员",
                    updatedAt = "2026-11-11 08:10",
                    status = PickingDetailStatus.UNPICKED
                ),
                PickingDetail(
                    orderNo = "JC20261028002",
                    lineNo = 3,
                    rfid = "E280 0006",
                    itemCode = "GJ-006",
                    description = "数字钳形表",
                    unit = "件",
                    batchNo = "PC004",
                    warehouse = "电气工具库",
                    location = "C1-1",
                    createdBy = "材料员",
                    createdAt = "2026-11-11 08:10",
                    updatedBy = "材料员",
                    updatedAt = "2026-11-11 08:10",
                    status = PickingDetailStatus.UNPICKED
                )
            )
        ),
        PickingOrder(
            borrowOrderNo = "JJ20261111003",
            orderNo = "JC20261028003",
            workshop = "架大修一车间",
            workGroup = "电气二班",
            borrower = "王五",
            plannedDate = "2026-11-12 10:30",
            createdBy = "材料员",
            createdAt = "2026-11-11 08:20",
            updatedBy = "材料员",
            updatedAt = "2026-11-11 08:20",
            status = PickingStatus.UNPICKED,
            details = listOf(
                PickingDetail(
                    orderNo = "JC20261028003",
                    lineNo = 1,
                    rfid = "E280 0007",
                    itemCode = "GJ-007",
                    description = "电钻",
                    unit = "件",
                    batchNo = "PC005",
                    warehouse = "中心工具库",
                    location = "A2-1",
                    createdBy = "材料员",
                    createdAt = "2026-11-11 08:20",
                    updatedBy = "材料员",
                    updatedAt = "2026-11-11 08:20",
                    status = PickingDetailStatus.UNPICKED
                )
            )
        ),
        PickingOrder(
            borrowOrderNo = "JJ20261111004",
            orderNo = "JC20261028004",
            workshop = "架大修一车间",
            workGroup = "机械一班",
            borrower = "赵六",
            plannedDate = "2026-11-12 09:00",
            createdBy = "材料员",
            createdAt = "2026-11-11 08:30",
            updatedBy = "材料员",
            updatedAt = "2026-11-11 08:30",
            status = PickingStatus.UNPICKED,
            details = listOf(
                PickingDetail(
                    orderNo = "JC20261028004",
                    lineNo = 1,
                    rfid = "E280 0008",
                    itemCode = "GJ-008",
                    description = "内六角扳手",
                    unit = "件",
                    batchNo = "PC006",
                    warehouse = "中心工具库",
                    location = "A3-1",
                    createdBy = "材料员",
                    createdAt = "2026-11-11 08:30",
                    updatedBy = "材料员",
                    updatedAt = "2026-11-11 08:30",
                    status = PickingDetailStatus.UNPICKED
                ),
                PickingDetail(
                    orderNo = "JC20261028004",
                    lineNo = 2,
                    rfid = "E280 0009",
                    itemCode = "GJ-009",
                    description = "棘轮扳手",
                    unit = "件",
                    batchNo = "PC006",
                    warehouse = "中心工具库",
                    location = "A3-2",
                    createdBy = "材料员",
                    createdAt = "2026-11-11 08:30",
                    updatedBy = "材料员",
                    updatedAt = "2026-11-11 08:30",
                    status = PickingDetailStatus.UNPICKED
                )
            )
        ),
        PickingOrder(
            borrowOrderNo = "JJ20261111005",
            orderNo = "JC20261028005",
            workshop = "架大修二车间",
            workGroup = "电气二班",
            borrower = "孙七",
            plannedDate = "2026-11-12 14:30",
            createdBy = "材料员",
            createdAt = "2026-11-11 08:40",
            updatedBy = "材料员",
            updatedAt = "2026-11-11 08:40",
            status = PickingStatus.UNPICKED,
            details = listOf(
                PickingDetail(
                    orderNo = "JC20261028005",
                    lineNo = 1,
                    rfid = "E280 0010",
                    itemCode = "GJ-010",
                    description = "绝缘手套",
                    unit = "副",
                    batchNo = "PC007",
                    warehouse = "电气工具库",
                    location = "C2-1",
                    createdBy = "材料员",
                    createdAt = "2026-11-11 08:40",
                    updatedBy = "材料员",
                    updatedAt = "2026-11-11 08:40",
                    status = PickingDetailStatus.UNPICKED
                )
            )
        ),
        PickingOrder(
            borrowOrderNo = "JJ20261111006",
            orderNo = "JC20261028006",
            workshop = "架大修二车间",
            workGroup = "机械三班",
            borrower = "周八",
            plannedDate = "2026-11-13 08:30",
            createdBy = "材料员",
            createdAt = "2026-11-11 08:50",
            updatedBy = "材料员",
            updatedAt = "2026-11-11 08:50",
            status = PickingStatus.UNPICKED,
            details = listOf(
                PickingDetail(
                    orderNo = "JC20261028006",
                    lineNo = 1,
                    rfid = "E280 0011",
                    itemCode = "GJ-011",
                    description = "扭力扳手",
                    unit = "件",
                    batchNo = "PC008",
                    warehouse = "中心工具库",
                    location = "B2-1",
                    createdBy = "材料员",
                    createdAt = "2026-11-11 08:50",
                    updatedBy = "材料员",
                    updatedAt = "2026-11-11 08:50",
                    status = PickingDetailStatus.UNPICKED
                ),
                PickingDetail(
                    orderNo = "JC20261028006",
                    lineNo = 2,
                    rfid = "E280 0012",
                    itemCode = "GJ-012",
                    description = "套筒组套",
                    unit = "套",
                    batchNo = "PC008",
                    warehouse = "中心工具库",
                    location = "B2-2",
                    createdBy = "材料员",
                    createdAt = "2026-11-11 08:50",
                    updatedBy = "材料员",
                    updatedAt = "2026-11-11 08:50",
                    status = PickingDetailStatus.UNPICKED
                ),
                PickingDetail(
                    orderNo = "JC20261028006",
                    lineNo = 3,
                    rfid = "E280 0013",
                    itemCode = "GJ-013",
                    description = "十字螺丝刀",
                    unit = "把",
                    batchNo = "PC009",
                    warehouse = "中心工具库",
                    location = "B2-3",
                    createdBy = "材料员",
                    createdAt = "2026-11-11 08:50",
                    updatedBy = "材料员",
                    updatedAt = "2026-11-11 08:50",
                    status = PickingDetailStatus.UNPICKED
                )
            )
        ),
        PickingOrder(
            borrowOrderNo = "JJ20261111007",
            orderNo = "JC20261028007",
            workshop = "架大修一车间",
            workGroup = "电气三班",
            borrower = "吴九",
            plannedDate = "2026-11-13 15:00",
            createdBy = "材料员",
            createdAt = "2026-11-11 09:00",
            updatedBy = "材料员",
            updatedAt = "2026-11-11 09:00",
            status = PickingStatus.UNPICKED,
            details = listOf(
                PickingDetail(
                    orderNo = "JC20261028007",
                    lineNo = 1,
                    rfid = "E280 0014",
                    itemCode = "GJ-014",
                    description = "高压验电器",
                    unit = "支",
                    batchNo = "PC010",
                    warehouse = "电气工具库",
                    location = "C3-1",
                    createdBy = "材料员",
                    createdAt = "2026-11-11 09:00",
                    updatedBy = "材料员",
                    updatedAt = "2026-11-11 09:00",
                    status = PickingDetailStatus.UNPICKED
                ),
                PickingDetail(
                    orderNo = "JC20261028007",
                    lineNo = 2,
                    rfid = "E280 0015",
                    itemCode = "GJ-015",
                    description = "绝缘靴",
                    unit = "双",
                    batchNo = "PC010",
                    warehouse = "电气工具库",
                    location = "C3-2",
                    createdBy = "材料员",
                    createdAt = "2026-11-11 09:00",
                    updatedBy = "材料员",
                    updatedAt = "2026-11-11 09:00",
                    status = PickingDetailStatus.UNPICKED
                )
            )
        )
    )

    private val mockScanOrder = mockOrders.first()

    override suspend fun loadOrders(): List<PickingOrder> {
        delay(500.milliseconds)
        return mockOrders
    }

    override suspend fun loadScanOrder(): PickingOrder {
        delay(500.milliseconds)
        return mockScanOrder
    }

    /**
     * TODO: 字段未确定，接入真实接口后按响应结果返回成功或失败，并同步后端单据状态。
     */
    override suspend fun submitPicking(request: PickingConfirmRequest): Boolean {
        delay(800.milliseconds)
        return true
    }
}
