package com.hxb.myandroidapp_1021.feature.tempborrow.data.repository

import com.hxb.myandroidapp_1021.feature.tempborrow.domain.model.TempBorrowConfirmRequest
import com.hxb.myandroidapp_1021.feature.tempborrow.domain.model.TempBorrowCreateRequest
import com.hxb.myandroidapp_1021.feature.tempborrow.domain.model.TempBorrowDetail
import com.hxb.myandroidapp_1021.feature.tempborrow.domain.model.TempBorrowInventoryItem
import com.hxb.myandroidapp_1021.feature.tempborrow.domain.model.TempBorrowOrder
import com.hxb.myandroidapp_1021.feature.tempborrow.domain.repository.TempBorrowRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

/**
 * 临时借出的本地演示数据源；单据存入借还共用的内存数据源，单号、经办人与时间均为模拟值。
 */
class MockTempBorrowRepository @Inject constructor(
    private val store: TempBorrowReturnStore
) : TempBorrowRepository {

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

    override suspend fun loadOrders(): List<TempBorrowOrder> {
        delay(500.milliseconds)
        return store.loadBorrowOrders()
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

    override suspend fun createOrder(request: TempBorrowCreateRequest): TempBorrowOrder {
        delay(500.milliseconds)
        val createdAt = now()
        val orderNo = "LSJC${System.currentTimeMillis()}"
        val details = mockInventory
            .filter { it.rfid in request.rfids }
            .mapIndexed { index, item ->
                TempBorrowDetail(
                    orderNo = orderNo,
                    lineNo = index + 1,
                    rfid = item.rfid,
                    itemCode = item.itemCode,
                    description = item.description,
                    unit = item.unit,
                    createdBy = mockOperator,
                    createdAt = createdAt
                )
            }
        return store.addBorrowOrder(
            TempBorrowOrder(
                orderNo = orderNo,
                workOrderNo = request.workOrderNo,
                borrower = request.borrower,
                workshop = request.workshop,
                workGroup = request.workGroup,
                remark = request.remark,
                createdBy = mockOperator,
                createdAt = createdAt,
                details = details
            )
        )
    }

    override suspend fun submitBorrow(request: TempBorrowConfirmRequest): TempBorrowOrder? {
        delay(800.milliseconds)
        return store.submitBorrow(request, mockOperator, now())
    }

    private fun now(): String =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
}
