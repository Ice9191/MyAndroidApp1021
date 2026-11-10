package com.hxb.myandroidapp_1021.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.hxb.myandroidapp_1021.data.models.PickingToolScanItem
import com.hxb.myandroidapp_1021.data.models.ToolScanItemStatus

class PickingScanState : ViewModel() {
    val orderNo = "JC20231028001"
    val orderStatus = "待处理"
    val workshop = "检修一班"
    val borrower = "张三"

    var tools by mutableStateOf(listOf(
        PickingToolScanItem(
            id = 1,
            name = "绝缘扳手",
            status = ToolScanItemStatus.PICKED,
            itemCode = "GJ-001",
            rfid = "E280 1160 6000 0200 0001",
            recommendedLocation = "A1-1",
            unit = "个"
        ),
        PickingToolScanItem(
            id = 2,
            name = "数字万用表",
            itemCode = "GJ-002",
            rfid = "E280 1160 6000 0200 0002",
            recommendedLocation = "A1-2",
            unit = "个"
        ),
        PickingToolScanItem(
            id = 3,
            name = "游标卡尺",
            itemCode = "GJ-003",
            rfid = "E280 1160 6000 0200 0003",
            recommendedLocation = "A1-3",
            unit = "个"
        ),
        PickingToolScanItem(
            id = 4,
            name = "绝缘手套",
            itemCode = "GJ-004",
            rfid = "E280 1160 6000 0200 0004",
            recommendedLocation = "A1-4",
            unit = "个"
        ),
        PickingToolScanItem(
            id = 5,
            name = "验电器",
            itemCode = "GJ-005",
            rfid = "E280 1160 6000 0200 0005",
            recommendedLocation = "A1-5",
            unit = "个"
        )
    ))
        private set

    val readList get() = tools.filter { it.status == ToolScanItemStatus.PICKED }
    val unreadList get() = tools.filter { it.status == ToolScanItemStatus.UNPICKED }

    fun toggleRead(ids: Set<Int>) {
        tools = tools.map { tool ->
            if (tool.id !in ids) return@map tool
            val status = when (tool.status) {
                ToolScanItemStatus.PICKED -> ToolScanItemStatus.UNPICKED
                ToolScanItemStatus.UNPICKED -> ToolScanItemStatus.PICKED
            }
            tool.copy(status = status)
        }
    }
}
