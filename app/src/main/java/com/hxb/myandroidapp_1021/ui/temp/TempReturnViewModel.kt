package com.hxb.myandroidapp_1021.ui.temp

import androidx.lifecycle.ViewModel
import com.hxb.myandroidapp_1021.ui.otherputaway.OtherPutawayTaskUi
import com.hxb.myandroidapp_1021.ui.otherputaway.OtherPutawayToolUi
import com.hxb.myandroidapp_1021.ui.otherputaway.SourceType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TempReturnViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TempReturnUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val mockOrder = TempReturnOrder(
            orderNo = "LSGH20261112001",
            workOrderNo = "GD20261112001",
            returner = "",
            location = "",
            remark = "",
            createdBy = "演示材料员",
            createdAt = "2026-11-12 09:00:00",
            lines = listOf(
                TempReturnLine(
                    1, TempTool("E280 2001 0001", "270000000003", "内六角扳手套装", "套", "A0103"),
                    "LSJC20261111001", "张三", "2026-11-11 09:00:00"
                ),
                TempReturnLine(
                    2,
                    TempTool("E280 2001 0002", "280000000003", "扭矩扳手 5–50N·m", "把", "B0103"),
                    "LSJC20261111001",
                    "张三",
                    "2026-11-11 09:00:00"
                )
            )
        )
        _uiState.update { it.copy(orders = listOf(mockOrder)) }
    }

    fun onQueryChange(value: String) {
        _uiState.update { it.copy(query = value) }
    }

    fun onTabSelected(value: Int) {
        _uiState.update { it.copy(selectedTab = value) }
    }

    fun onReturnerChange(value: String) {
        _uiState.update { it.copy(returner = value, message = null) }
    }

    fun onWorkOrderChange(value: String) {
        _uiState.update { it.copy(workOrderNo = value, message = null) }
    }

    fun onLocationChange(value: String) {
        _uiState.update { it.copy(location = value, message = null) }
    }

    fun onRemarkChange(value: String) {
        _uiState.update { it.copy(remark = value, message = null) }
    }

    fun selectOrder(orderNo: String) {
        _uiState.update { state ->
            val order = state.orders.firstOrNull { it.orderNo == orderNo } ?: return@update state
            state.copy(
                selectedOrderNo = orderNo,
                returner = order.returner,
                workOrderNo = order.workOrderNo,
                location = order.location,
                remark = order.remark,
                message = null
            )
        }
    }

    fun showList() {
        _uiState.update {
            it.copy(
                selectedOrderNo = null,
                query = "",
                confirmationRfid = null,
                message = null
            )
        }
    }

    /**
     * 模拟借出后生成归还单；追加新借出明细时保留已经登记的归还信息和核销结果。
     */
    fun syncBorrowedOrders(orders: List<TempBorrowOrder>) {
        _uiState.update { state ->
            var nextOrders = state.orders
            orders.forEach { borrow ->
                val lines = borrow.lines.filter { it.borrowed }
                if (lines.isNotEmpty()) {
                    val orderNo = "GH-${borrow.orderNo}"
                    val existing = nextOrders.firstOrNull { it.orderNo == orderNo }
                    val mockOrder = TempReturnOrder(
                        orderNo,
                        borrow.workOrderNo,
                        "",
                        "",
                        "",
                        borrow.createdBy,
                        lines.first().borrowedAt,
                        lines.map {
                            TempReturnLine(
                                it.lineNo,
                                it.tool,
                                borrow.orderNo,
                                borrow.borrower,
                                it.borrowedAt
                            )
                        }
                    )
                    nextOrders =
                        if (existing == null) nextOrders + mockOrder else nextOrders.map { current ->
                            if (current.orderNo != orderNo) current else current.copy(lines = mockOrder.lines.map { line ->
                                current.lines.firstOrNull { it.tool.rfid == line.tool.rfid } ?: line
                            })
                        }
                }
            }
            state.copy(orders = nextOrders)
        }
    }

    fun requestConfirmation(rfid: String?) {
        _uiState.update { state ->
            val error = when {
                rfid == null -> null
                state.returner.isBlank() -> "请填写归还人"
                state.workOrderNo.isBlank() -> "请填写工单号"
                state.location.isBlank() -> "请填写归还库位"
                else -> null
            }
            state.copy(confirmationRfid = if (error == null) rfid else null, message = error)
        }
    }

    /**
     * 依据临时归还库存状态图，确认只完成归还核销，随后交给其他上架；不直接恢复在库。
     */
    fun confirmLine() {
        _uiState.update { state ->
            val order = state.selectedOrder ?: return@update state
            val rfid = state.confirmationRfid ?: return@update state
            if (state.returner.isBlank() || state.workOrderNo.isBlank() || state.location.isBlank()) return@update state
            state.copy(
                orders = state.orders.map { current ->
                    if (current.orderNo != order.orderNo) current else current.copy(
                        returner = state.returner.trim(), workOrderNo = state.workOrderNo.trim(),
                        location = state.location.trim(), remark = state.remark.trim(),
                        lines = current.lines.map { line ->
                            if (line.tool.rfid != rfid || line.returned) line else line.copy(
                                returned = true,
                                returnedAt = now()
                            )
                        }
                    )
                },
                confirmationRfid = null, message = "已确认归还，器具待上架"
            )
        }
    }

    fun putawayTask(): OtherPutawayTaskUi? =
        putawayTasks().firstOrNull { it.sourceNo == _uiState.value.selectedOrderNo }

    fun putawayTasks(): List<OtherPutawayTaskUi> = _uiState.value.orders.mapNotNull { order ->
        val returned = order.lines.filter { it.returned }
        if (returned.isEmpty()) null else OtherPutawayTaskUi(
            sourceNo = order.orderNo, sourceType = SourceType.TEMPORARY, date = order.createdAt,
            tools = returned.map { OtherPutawayToolUi(it.tool.rfid, it.tool.name, order.location) }
        )
    }

    private fun now(): String =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
}
