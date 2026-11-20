package com.hxb.myandroidapp_1021.feature.tempborrow.data.repository

import com.hxb.myandroidapp_1021.feature.tempborrow.domain.model.TempBorrowConfirmRequest
import com.hxb.myandroidapp_1021.feature.tempborrow.domain.model.TempBorrowCreateRequest
import com.hxb.myandroidapp_1021.feature.tempborrow.domain.model.TempBorrowDetail
import com.hxb.myandroidapp_1021.feature.tempborrow.domain.model.TempBorrowDetailStatus
import com.hxb.myandroidapp_1021.feature.tempborrow.domain.model.TempBorrowInventoryItem
import com.hxb.myandroidapp_1021.feature.tempborrow.domain.model.TempBorrowOrder
import com.hxb.myandroidapp_1021.feature.tempborrow.domain.model.TempBorrowStatus
import com.hxb.myandroidapp_1021.feature.tempborrow.domain.repository.TempBorrowRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

/**
 * 临时借出的本地演示数据源；单号、经办人与时间均为模拟值。
 */
class MockTempBorrowRepository @Inject constructor() : TempBorrowRepository {

    private val mockOperator = "演示材料员"

    /**
     * 车间、工班与借用人候选项，用于新建单据时的查询弹窗；正式数据由后端基础档案提供。
     */
    private val mockWorkshops = listOf(
        "检修车间",
        "变电检修车间",
        "总装车间",
        "架大修一车间",
        "架大修二车间",
        "供电车间",
        "信号车间",
        "车辆车间"
    )

    private val mockWorkGroups = listOf(
        "电气一班",
        "电气二班",
        "高压班",
        "试验班",
        "继电保护班",
        "变电检修班",
        "接触网班",
        "信号检修班",
        "机械班",
        "综合班"
    )

    private val mockBorrowers = listOf(
        "张伟",
        "李强",
        "王芳",
        "赵敏",
        "刘洋",
        "陈晨",
        "杨帆",
        "周涛",
        "孙磊",
        "黄静",
        "吴迪",
        "郑洁"
    )

    private val mockInventory = listOf(
        TempBorrowInventoryItem("E280 1001 0001", "270000000001", "活动扳手 12 寸", "把", "A0101"),
        TempBorrowInventoryItem("E280 1001 0002", "270000000001", "活动扳手 12 寸", "把", "A0102"),
        TempBorrowInventoryItem("E280 1001 0003", "280000000001", "数显游标卡尺 0–150mm", "把", "B0101"),
        TempBorrowInventoryItem("E280 1001 0004", "280000000006", "数字万用表", "台", "B0102")
    )

    /**
     * 预置单据使用的器具，使用独立标签，避免与可借库存重复。
     */
    private val mockBorrowedTools = listOf(
        TempBorrowInventoryItem("E280 3001 0001", "280000000001", "数显游标卡尺 0–150mm", "把", "B0101"),
        TempBorrowInventoryItem("E280 3001 0002", "280000000006", "数字万用表", "台", "B0102"),
        TempBorrowInventoryItem("E280 3001 0003", "270000000001", "活动扳手 12 寸", "把", "A0101"),
        TempBorrowInventoryItem("E280 3001 0004", "290000000001", "绝缘手套", "双", "C0101")
    )

    /**
     * 预置两张已借出的演示单据，供临时借出列表和临时归还的待还器具检索使用。
     */
    private val mockOrders = listOf(
        TempBorrowOrder(
            orderNo = "LSJC20261110001",
            status = TempBorrowStatus.BORROWED,
            workOrderNo = "GD20261110001",
            borrower = "张伟",
            workshop = "检修车间",
            workGroup = "电气一班",
            borrowTime = "2026-11-10 09:15:00",
            createdBy = mockOperator,
            createdAt = "2026-11-10 09:00:00",
            details = listOf(
                borrowedDetail("LSJC20261110001", 1, mockBorrowedTools[0], "2026-11-10 09:15:00"),
                borrowedDetail("LSJC20261110001", 2, mockBorrowedTools[1], "2026-11-10 09:15:00")
            )
        ),
        TempBorrowOrder(
            orderNo = "LSJC20261112001",
            status = TempBorrowStatus.BORROWED,
            workOrderNo = "GD20261112001",
            borrower = "李强",
            workshop = "变电检修车间",
            workGroup = "高压班",
            borrowTime = "2026-11-12 10:20:00",
            createdBy = mockOperator,
            createdAt = "2026-11-12 10:00:00",
            details = listOf(
                borrowedDetail("LSJC20261112001", 1, mockBorrowedTools[2], "2026-11-12 10:20:00")
            )
        ),
        TempBorrowOrder(
            orderNo = "LSJC20261113001",
            status = TempBorrowStatus.BORROWED,
            workOrderNo = "GD20261113001",
            borrower = "王芳",
            workshop = "检修车间",
            workGroup = "试验班",
            borrowTime = "2026-11-13 08:40:00",
            createdBy = mockOperator,
            createdAt = "2026-11-13 08:30:00",
            details = listOf(
                borrowedDetail("LSJC20261113001", 1, mockBorrowedTools[3], "2026-11-13 08:40:00")
            )
        )
    )

    override suspend fun loadOrders(): List<TempBorrowOrder> {
        delay(500.milliseconds)
        return mockOrders
    }

    override suspend fun loadInventory(): List<TempBorrowInventoryItem> {
        delay(300.milliseconds)
        return mockInventory
    }

    override suspend fun loadWorkshops(): List<String> {
        delay(200.milliseconds)
        return mockWorkshops
    }

    override suspend fun loadWorkGroups(): List<String> {
        delay(200.milliseconds)
        return mockWorkGroups
    }

    override suspend fun loadBorrowers(): List<String> {
        delay(200.milliseconds)
        return mockBorrowers
    }

    /**
     * 构造一条已借出的明细，用于预置演示单据。
     */
    private fun borrowedDetail(
        orderNo: String,
        lineNo: Int,
        item: TempBorrowInventoryItem,
        borrowedAt: String
    ): TempBorrowDetail = TempBorrowDetail(
        orderNo = orderNo,
        lineNo = lineNo,
        status = TempBorrowDetailStatus.BORROWED,
        rfid = item.rfid,
        itemCode = item.itemCode,
        description = item.description,
        unit = item.unit,
        createdBy = mockOperator,
        createdAt = borrowedAt,
        updatedBy = mockOperator,
        updatedAt = borrowedAt
    )

    override suspend fun createOrder(request: TempBorrowCreateRequest): TempBorrowOrder {
        delay(500.milliseconds)
        val createdAt = now()
        val details = mockInventory
            .filter { it.rfid in request.rfids }
            .mapIndexed { index, item ->
                TempBorrowDetail(
                    orderNo = "", // 单号生成后统一回填
                    lineNo = index + 1,
                    rfid = item.rfid,
                    itemCode = item.itemCode,
                    description = item.description,
                    unit = item.unit,
                    createdBy = mockOperator,
                    createdAt = createdAt
                )
            }
        val orderNo = "LSJC${System.currentTimeMillis()}"
        return TempBorrowOrder(
            orderNo = orderNo,
            workOrderNo = request.workOrderNo,
            borrower = request.borrower,
            workshop = request.workshop,
            workGroup = request.workGroup,
            remark = request.remark,
            createdBy = mockOperator,
            createdAt = createdAt,
            details = details.map { it.copy(orderNo = orderNo) }
        )
    }

    override suspend fun submitBorrow(request: TempBorrowConfirmRequest): Boolean {
        delay(800.milliseconds)
        return true
    }

    private fun now(): String =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
}
