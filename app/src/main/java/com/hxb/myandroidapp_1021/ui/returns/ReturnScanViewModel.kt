package com.hxb.myandroidapp_1021.ui.returns

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

class ReturnScanViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        ReturnScanUiState(
            order = ReturnOrder(
                orderNo = "GH20261111001",
                status = ReturnStatus.UNRETURNED,
                workshop = "检修一班",
                returner = "张三",
                pendingCount = 3,
                returnedDate = "",
                totalCount = 3,
                returnedCount = 0
            ),
            isLoading = true
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            delay(500.milliseconds) // 模拟网络请求
            val mockTools = listOf(
                ReturnToolScanItem(
                    id = 1,
                    name = "绝缘操作杆",
                    itemCode = "GJ-001",
                    rfid = "E280 0001"
                ),
                ReturnToolScanItem(
                    id = 2,
                    name = "安全帽",
                    itemCode = "GJ-002",
                    rfid = "E280 0002"
                ),
                ReturnToolScanItem(id = 3, name = "绝缘绳", itemCode = "GJ-003", rfid = "E280 0003")
            )
            _uiState.update { state ->
                state.copy(isLoading = false, tools = mockTools)
            }
        }
    }

    fun selectOrder(order: ReturnOrder) {
        _uiState.update { state ->
            state.copy(
                order = order,
                tools = state.tools.map { it.copy(isRead = false, readDate = "") })
        }
    }

    fun toggleRead(ids: Set<Int>) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        _uiState.update { state ->
            state.copy(tools = state.tools.map { tool ->
                if (tool.id !in ids) tool else tool.copy(
                    isRead = !tool.isRead,
                    readDate = if (tool.isRead) "" else today
                )
            })
        }
    }
}
