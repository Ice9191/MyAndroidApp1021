package com.hxb.myandroidapp_1021.ui.putaway

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

class PutawayListViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PutawayListUiState(isLoading = true))
    val uiState: StateFlow<PutawayListUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            delay(500.milliseconds)
            val mockOrders = listOf(
                PutawayOrder(
                    inboundOrderNo = "RK20261111001", orderNo = "SJ20261111001",
                    status = "部分上架", operator = "王芳", putawayTime = "2026-11-11 09:00",
                    details = listOf(
                        PutawayDetail(
                            "SJ20261111001",
                            "1",
                            "1",
                            "待上架",
                            "PC001",
                            "E280 0001",
                            "GJ-001",
                            "绝缘扳手",
                            "件",
                            "中心工具库",
                            "",
                            "待上架"
                        ),
                        PutawayDetail(
                            "SJ20261111001",
                            "1",
                            "2",
                            "待上架",
                            "PC001",
                            "E280 0002",
                            "GJ-001",
                            "绝缘扳手",
                            "件",
                            "中心工具库",
                            "",
                            "待上架"
                        ),
                        PutawayDetail(
                            "SJ20261111001",
                            "2",
                            "3",
                            "已上架",
                            "PC002",
                            "E280 0003",
                            "GJ-002",
                            "数字万用表",
                            "件",
                            "中心工具库",
                            "A1-03",
                            "在库"
                        )
                    )
                ),
                PutawayOrder(
                    inboundOrderNo = "RK20261111002", orderNo = "SJ20261111002",
                    status = "待上架", operator = "", putawayTime = "",
                    details = listOf(
                        PutawayDetail(
                            "SJ20261111002",
                            "1",
                            "1",
                            "待上架",
                            "PC003",
                            "E280 0004",
                            "GJ-003",
                            "验电器",
                            "件",
                            "电气工具库",
                            "",
                            "待上架"
                        ),
                        PutawayDetail(
                            "SJ20261111002",
                            "2",
                            "2",
                            "待上架",
                            "PC003",
                            "E280 0005",
                            "GJ-004",
                            "绝缘手套",
                            "件",
                            "电气工具库",
                            "",
                            "待上架"
                        )
                    )
                ),
                PutawayOrder(
                    inboundOrderNo = "RK20261111003", orderNo = "SJ20261111003",
                    status = "已上架", operator = "张三", putawayTime = "2026-11-11 08:30",
                    details = listOf(
                        PutawayDetail(
                            "SJ20261111003",
                            "1",
                            "1",
                            "已上架",
                            "PC004",
                            "E280 0006",
                            "GJ-005",
                            "游标卡尺",
                            "件",
                            "中心工具库",
                            "A1-02",
                            "在库"
                        )
                    )
                )
            )
            _uiState.update { it.copy(isLoading = false, orders = mockOrders) }
        }
    }

    fun onSearchQueryChange(value: String) {
        _uiState.update { it.copy(searchQuery = value) }
    }

    fun onFilterChange(value: PutawayFilter) {
        _uiState.update { it.copy(selectedFilter = value) }
    }

    fun selectOrder(order: PutawayOrder): Boolean {
        if (order.status == "异常") {
            _uiState.update { it.copy(errorMessage = "异常单据不能启动上架，请核对入库来源单据") }
            return false
        }
        _uiState.update { it.copy(selectedOrderNo = order.orderNo, errorMessage = null) }
        return true
    }

    /**
     * 文档要求按明细更新库存；不能等整单完成才入库。
     * 模拟成功后只更新当前明细，再汇总单据进度，返回列表时保留已完成结果。
     */
    fun confirmItem(request: PutawayRequest): Boolean {
        val order = _uiState.value.selectedOrder ?: return false
        val detail =
            order.details.firstOrNull { it.lineNo == request.lineNo && it.rfid == request.rfid }
                ?: return false
        if (order.orderNo != request.orderNo || detail.isCompleted || request.location.isBlank() || request.operator.isBlank()) return false
        val putawayTime =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        _uiState.update { state ->
            state.copy(orders = state.orders.map { current ->
                if (current.orderNo != request.orderNo) current else {
                    val details = current.details.map { item ->
                        if (item.lineNo != request.lineNo) item else item.copy(
                            status = "已上架", location = request.location, inventoryStatus = "在库"
                        )
                    }
                    current.copy(
                        details = details,
                        status = if (details.all { it.isCompleted }) "已上架" else "部分上架",
                        operator = request.operator,
                        putawayTime = putawayTime
                    )
                }
            })
        }
        return true
    }
}
