package com.hxb.myandroidapp_1021.ui.picking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class PickingScanViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PickingScanUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            delay(500.milliseconds) // 模拟网络请求
            val mockTools = listOf(
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
                    status = ToolScanItemStatus.UNPICKED,
                    itemCode = "GJ-002",
                    rfid = "E280 1160 6000 0200 0002",
                    recommendedLocation = "A1-2",
                    unit = "个"
                ),
                PickingToolScanItem(
                    id = 3,
                    name = "游标卡尺",
                    status = ToolScanItemStatus.UNPICKED,
                    itemCode = "GJ-003",
                    rfid = "E280 1160 6000 0200 0003",
                    recommendedLocation = "A1-3",
                    unit = "个"
                ),
                PickingToolScanItem(
                    id = 4,
                    name = "绝缘手套",
                    status = ToolScanItemStatus.UNPICKED,
                    itemCode = "GJ-004",
                    rfid = "E280 1160 6000 0200 0004",
                    recommendedLocation = "A1-4",
                    unit = "个"
                ),
                PickingToolScanItem(
                    id = 5,
                    name = "验电器",
                    status = ToolScanItemStatus.UNPICKED,
                    itemCode = "GJ-005",
                    rfid = "E280 1160 6000 0200 0005",
                    recommendedLocation = "A1-5",
                    unit = "个"
                )
            )

            _uiState.update { oldState ->
                oldState.copy(
                    isLoading = false,
                    orderNo = "JC20231028001",
                    orderStatus = PickingStatus.PARTIALLY_PICKED,
                    workshop = "检修一班",
                    borrower = "张三",
                    tools = mockTools
                )
            }
        }
    }

    /**
     * 切换工具读取状态
     */
    fun toggleRead(ids: Set<Int>) {
        _uiState.update { state ->
            val updatedTools = state.tools.map { tool ->
                if (tool.id !in ids) return@map tool
                val newStatus = when (tool.status) {
                    ToolScanItemStatus.PICKED -> ToolScanItemStatus.UNPICKED
                    ToolScanItemStatus.UNPICKED -> ToolScanItemStatus.PICKED
                }
                tool.copy(status = newStatus)
            }
            state.copy(tools = updatedTools)
        }
    }
}
