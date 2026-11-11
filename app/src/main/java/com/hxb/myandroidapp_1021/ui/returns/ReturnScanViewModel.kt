package com.hxb.myandroidapp_1021.ui.returns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

class ReturnScanViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ReturnScanUiState(isLoading = true))
    val uiState: StateFlow<ReturnScanUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            delay(500.milliseconds)
            val mockOrder = ReturnOrder(
                orderNo = "GH20261111001",
                status = ReturnStatus.PARTIALLY_RETURNED,
                workshop = "检修一班",
                returner = "张伟",
                pendingCount = 3,
                returnedDate = "2026-11-11 09:30",
                totalCount = 5,
                returnedCount = 2,
                workOrderNo = "GD20261111001",
                createdBy = "材料员",
                createdAt = "2026-11-11 08:00"
            )
            val mockTools = listOf(
                ReturnToolScanItem(
                    id = 1,
                    name = "绝缘操作杆",
                    itemCode = "GJ-001",
                    rfid = "E280 0001",
                    location = "A1-1",
                    unit = "件"
                ),
                ReturnToolScanItem(
                    id = 2,
                    name = "安全帽",
                    itemCode = "GJ-002",
                    rfid = "E280 0002",
                    location = "A1-2",
                    unit = "件"
                ),
                ReturnToolScanItem(
                    id = 3,
                    name = "绝缘绳",
                    itemCode = "GJ-003",
                    rfid = "E280 0003",
                    location = "A1-3",
                    unit = "件"
                )
            )
            _uiState.update { state ->
                state.copy(isLoading = false, order = state.order ?: mockOrder, tools = mockTools)
            }
        }
    }

    /**
     * 新单据进入同一流程时清空上次读签结果；加载中的单据选择不会被模拟响应覆盖。
     */
    fun selectOrder(order: ReturnOrder) {
        _uiState.update { state ->
            state.copy(
                order = order,
                tools = state.tools.map { it.copy(isRead = false, readDate = "") })
        }
    }

    /**
     * 只切换本次读签状态及时间，不直接改动单据已完成数量或库存。
     */
    fun toggleRead(ids: Set<Int>) {
        val readDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        _uiState.update { state ->
            state.copy(tools = state.tools.map { tool ->
                if (tool.id !in ids) tool else tool.copy(
                    isRead = !tool.isRead,
                    readDate = if (tool.isRead) "" else readDate
                )
            })
        }
    }
}
