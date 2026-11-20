package com.hxb.myandroidapp_1021.feature.tempborrow.data.repository

import com.hxb.myandroidapp_1021.feature.tempborrow.domain.model.TempBorrowConfirmRequest
import com.hxb.myandroidapp_1021.feature.tempborrow.domain.model.TempBorrowDetail
import com.hxb.myandroidapp_1021.feature.tempborrow.domain.model.TempBorrowDetailStatus
import com.hxb.myandroidapp_1021.feature.tempborrow.domain.model.TempBorrowOrder
import com.hxb.myandroidapp_1021.feature.tempborrow.domain.model.TempBorrowStatus
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnConfirmRequest
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnDetail
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnOrder
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 临时借出与临时归还共用的内存数据源，模拟服务端把借出单和归还单保存在同一份存储里。
 * 最后一条明细确认借出时，在同一次写入里更新借出单并按来源借出单号生成 1:1 归还单；
 * 重复确认沿用已有归还单，保留已经确认的归还进度。
 * TODO: 字段未确定：接入后端后由服务端在同一事务里完成最后一条借出确认与归还单生成，并保证来源借出单号唯一。
 */
@Singleton
class TempBorrowReturnStore @Inject constructor() {

    private val lock = Any()

    private val mockOperator = "演示材料员"

    private val borrowOrders = seedBorrowOrders().toMutableList()

    private val returnOrders = borrowOrders
        .filter { it.status == TempBorrowStatus.BORROWED }
        .map {
            buildReturnOrder(
                order = it,
                operator = it.updatedBy.ifBlank { mockOperator },
                at = it.updatedAt.ifBlank { it.createdAt }
            )
        }
        .toMutableList()

    fun loadBorrowOrders(): List<TempBorrowOrder> = synchronized(lock) {
        borrowOrders.toList()
    }

    fun loadReturnOrders(): List<TempReturnOrder> = synchronized(lock) {
        returnOrders.toList()
    }

    fun addBorrowOrder(order: TempBorrowOrder): TempBorrowOrder = synchronized(lock) {
        borrowOrders += order
        order
    }

    /**
     * 确认单件借出并保存借出单；整单变为已借出时生成归还单。
     */
    fun submitBorrow(
        request: TempBorrowConfirmRequest,
        operator: String,
        at: String
    ): TempBorrowOrder? = synchronized(lock) {
        val index = borrowOrders.indexOfFirst { it.orderNo == request.orderNo }
        if (index < 0) {
            return@synchronized null
        }
        val updated = borrowOrders[index].markBorrowed(request.lineNo, operator, at)
        borrowOrders[index] = updated
        if (updated.status == TempBorrowStatus.BORROWED &&
            returnOrders.none { it.borrowOrderNo == updated.orderNo }
        ) {
            returnOrders += buildReturnOrder(updated, operator, at)
        }
        updated
    }

    fun submitReturn(
        request: TempReturnConfirmRequest,
        operator: String,
        at: String
    ): TempReturnOrder? = synchronized(lock) {
        val index = returnOrders.indexOfFirst { it.orderNo == request.orderNo }
        if (index < 0) {
            return@synchronized null
        }
        val updated = returnOrders[index].markReturned(
            lineNo = request.lineNo,
            operator = operator,
            at = at,
            returner = request.returner,
            workOrderNo = request.workOrderNo,
            returnLocation = request.returnLocation,
            remark = request.remark
        )
        returnOrders[index] = updated
        updated
    }

    /**
     * 由借出单组装归还单：带入来源借出单号、工单号、车间、工班、借用人和全部器具明细，
     * 明细直接按顺序继承，不按 RFID 去重；归还单与明细初始均为待确认。
     */
    private fun buildReturnOrder(
        order: TempBorrowOrder,
        operator: String,
        at: String
    ): TempReturnOrder {
        val orderNo = "LSGH${order.orderNo.removePrefix("LSJC")}"
        return TempReturnOrder(
            orderNo = orderNo,
            borrowOrderNo = order.orderNo,
            workOrderNo = order.workOrderNo,
            workshop = order.workshop,
            workGroup = order.workGroup,
            borrower = order.borrower,
            createdBy = operator,
            createdAt = at,
            details = order.details.mapIndexed { index, detail ->
                TempReturnDetail(
                    orderNo = orderNo,
                    lineNo = index + 1,
                    rfid = detail.rfid,
                    itemCode = detail.itemCode,
                    description = detail.description,
                    unit = detail.unit,
                    createdBy = operator,
                    createdAt = at
                )
            }
        )
    }

    /**
     * 预置演示数据：三张已借出单据（各自已有归还单），以及一张 3 件器具的待确认借出单，
     * 用于演示确认到最后一件时自动生成归还单。
     */
    private fun seedBorrowOrders(): List<TempBorrowOrder> = listOf(
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
            updatedBy = mockOperator,
            updatedAt = "2026-11-10 09:15:00",
            details = listOf(
                seedBorrowDetail(
                    "LSJC20261110001", 1, "E280 3001 0001", "280000000001",
                    "数显游标卡尺 0–150mm", "把", "2026-11-10 09:15:00"
                ),
                seedBorrowDetail(
                    "LSJC20261110001", 2, "E280 3001 0002", "280000000006",
                    "数字万用表", "台", "2026-11-10 09:15:00"
                )
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
            updatedBy = mockOperator,
            updatedAt = "2026-11-12 10:20:00",
            details = listOf(
                seedBorrowDetail(
                    "LSJC20261112001", 1, "E280 3001 0003", "270000000001",
                    "活动扳手 12 寸", "把", "2026-11-12 10:20:00"
                )
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
            updatedBy = mockOperator,
            updatedAt = "2026-11-13 08:40:00",
            details = listOf(
                seedBorrowDetail(
                    "LSJC20261113001", 1, "E280 3001 0004", "290000000001",
                    "绝缘手套", "双", "2026-11-13 08:40:00"
                )
            )
        ),
        TempBorrowOrder(
            orderNo = "LSJC20261120001",
            status = TempBorrowStatus.PENDING,
            workOrderNo = "GD20261120001",
            borrower = "赵敏",
            workshop = "总装车间",
            workGroup = "机械班",
            createdBy = mockOperator,
            createdAt = "2026-11-20 08:30:00",
            details = listOf(
                seedBorrowDetail(
                    "LSJC20261120001", 1, "E280 4001 0001", "270000000001",
                    "活动扳手 12 寸", "把", "2026-11-20 08:30:00",
                    status = TempBorrowDetailStatus.PENDING
                ),
                seedBorrowDetail(
                    "LSJC20261120001", 2, "E280 4001 0002", "280000000001",
                    "数显游标卡尺 0–150mm", "把", "2026-11-20 08:30:00",
                    status = TempBorrowDetailStatus.PENDING
                ),
                seedBorrowDetail(
                    "LSJC20261120001", 3, "E280 4001 0003", "280000000006",
                    "数字万用表", "台", "2026-11-20 08:30:00",
                    status = TempBorrowDetailStatus.PENDING
                )
            )
        )
    )

    private fun seedBorrowDetail(
        orderNo: String,
        lineNo: Int,
        rfid: String,
        itemCode: String,
        description: String,
        unit: String,
        at: String,
        status: TempBorrowDetailStatus = TempBorrowDetailStatus.BORROWED
    ): TempBorrowDetail = TempBorrowDetail(
        orderNo = orderNo,
        lineNo = lineNo,
        status = status,
        rfid = rfid,
        itemCode = itemCode,
        description = description,
        unit = unit,
        createdBy = mockOperator,
        createdAt = at,
        updatedBy = if (status == TempBorrowDetailStatus.BORROWED) mockOperator else "",
        updatedAt = if (status == TempBorrowDetailStatus.BORROWED) at else ""
    )
}
