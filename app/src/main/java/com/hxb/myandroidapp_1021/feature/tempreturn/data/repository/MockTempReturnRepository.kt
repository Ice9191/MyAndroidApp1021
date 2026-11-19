package com.hxb.myandroidapp_1021.feature.tempreturn.data.repository

import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnConfirmRequest
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnCreateRequest
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnDetail
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnOrder
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnPendingTool
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.repository.TempReturnRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

/**
 * 临时归还的本地演示数据源；单号由当前时间模拟生成，正式单号以服务端返回为准。
 */
class MockTempReturnRepository @Inject constructor() : TempReturnRepository {

    private val mockOrder = TempReturnOrder(
        orderNo = "LSGH20261112001",
        workOrderNo = "GD20261112001",
        createdBy = "演示材料员",
        createdAt = "2026-11-12 09:00:00",
        details = listOf(
            TempReturnDetail(
                orderNo = "LSGH20261112001",
                lineNo = 1,
                rfid = "E280 2001 0001",
                itemCode = "270000000003",
                description = "内六角扳手套装",
                unit = "套",
                createdBy = "演示材料员",
                createdAt = "2026-11-12 09:00:00"
            ),
            TempReturnDetail(
                orderNo = "LSGH20261112001",
                lineNo = 2,
                rfid = "E280 2001 0002",
                itemCode = "280000000003",
                description = "扭矩扳手 5–50N·m",
                unit = "把",
                createdBy = "演示材料员",
                createdAt = "2026-11-12 09:00:00"
            )
        )
    )

    /**
     * 预置待还器具，保证检索页冷启动就有演示数据；与临时借出的演示单据保持一致。
     */
    private val mockPendingTools = listOf(
        TempReturnPendingTool(
            borrowOrderNo = "LSJC20261110001",
            workOrderNo = "GD20261110001",
            borrower = "张伟",
            rfid = "E280 3001 0001",
            itemCode = "280000000001",
            description = "数显游标卡尺 0–150mm",
            unit = "把"
        ),
        TempReturnPendingTool(
            borrowOrderNo = "LSJC20261110001",
            workOrderNo = "GD20261110001",
            borrower = "张伟",
            rfid = "E280 3001 0002",
            itemCode = "280000000006",
            description = "数字万用表",
            unit = "台"
        ),
        TempReturnPendingTool(
            borrowOrderNo = "LSJC20261112001",
            workOrderNo = "GD20261112001",
            borrower = "李强",
            rfid = "E280 3001 0003",
            itemCode = "270000000001",
            description = "活动扳手 12 寸",
            unit = "把"
        ),
        TempReturnPendingTool(
            borrowOrderNo = "LSJC20261113001",
            workOrderNo = "GD20261113001",
            borrower = "王芳",
            rfid = "E280 3001 0004",
            itemCode = "290000000001",
            description = "绝缘手套",
            unit = "双"
        )
    )

    override suspend fun loadOrders(): List<TempReturnOrder> {
        delay(500.milliseconds)
        return listOf(mockOrder)
    }

    override suspend fun loadPendingTools(): List<TempReturnPendingTool> {
        delay(300.milliseconds)
        return mockPendingTools
    }

    override suspend fun createOrder(request: TempReturnCreateRequest): TempReturnOrder {
        delay(500.milliseconds)
        val orderNo = "LSGH${System.currentTimeMillis()}"
        val at = now()
        return TempReturnOrder(
            orderNo = orderNo,
            workOrderNo = request.workOrderNo,
            returner = request.returner,
            returnLocation = request.returnLocation,
            remark = request.remark,
            createdBy = "演示材料员",
            createdAt = at,
            details = request.details.mapIndexed { index, detail ->
                TempReturnDetail(
                    orderNo = orderNo,
                    lineNo = index + 1,
                    rfid = detail.rfid,
                    itemCode = detail.itemCode,
                    description = detail.description,
                    unit = detail.unit,
                    createdBy = "演示材料员",
                    createdAt = at
                )
            }
        )
    }

    override suspend fun submitReturn(request: TempReturnConfirmRequest): Boolean {
        delay(800.milliseconds)
        return true
    }

    private fun now(): String =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
}
