package com.hxb.myandroidapp_1021.feature.overduealert.data.repository

import com.hxb.myandroidapp_1021.feature.overduealert.domain.model.OverdueAlert
import com.hxb.myandroidapp_1021.feature.overduealert.domain.repository.OverdueAlertRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class MockOverdueAlertRepository @Inject constructor() : OverdueAlertRepository {
    // 演示数据覆盖正常、空处理状态两类告警；接入接口后由真实仓库替换。
    private val mockAlerts = listOf(
        OverdueAlert(
            alertId = "ALM20261113001", alertType = "超期未还",
            workOrderNo = "GD20261109001", borrowOrderNo = "JY20261109001",
            borrower = "张伟", toolName = "活动扳手 12 寸",
            rfid = "RFID-10001", itemCode = "270000000001",
            borrowedAt = "2026-11-09 09:30", dueAt = "2026-11-11 18:00",
            overdueDays = 2, status = "已催还", handlingStatus = null
        ),
        OverdueAlert(
            alertId = "ALM20261113002", alertType = "超期未还",
            workOrderNo = "GD20261110003", borrowOrderNo = "JY20261110002",
            borrower = "李娜", toolName = "扭矩扳手 5–50 N·m",
            rfid = "RFID-10002", itemCode = "280000000003",
            borrowedAt = "2026-11-10 14:10", dueAt = "2026-11-12 18:00",
            overdueDays = 1, status = "提醒中", handlingStatus = null
        ),
        OverdueAlert(
            alertId = "ALM20261113003", alertType = "超期未还",
            workOrderNo = "GD20261106007", borrowOrderNo = "JY20261106005",
            borrower = "王强", toolName = "绝缘防护工具套装",
            rfid = "RFID-10003", itemCode = "270000000011",
            borrowedAt = "2026-11-06 10:00", dueAt = "2026-11-08 18:00",
            overdueDays = 5, status = "已催还", handlingStatus = null
        )
    )

    override suspend fun loadAlerts(): List<OverdueAlert> {
        delay(500.milliseconds)
        return mockAlerts
    }
}
