package com.hxb.myandroidapp_1021.ui.temp

import androidx.lifecycle.ViewModel
import com.hxb.myandroidapp_1021.ui.otherpicking.OtherPickingTaskUi
import com.hxb.myandroidapp_1021.ui.otherpicking.OtherPickingToolUi
import com.hxb.myandroidapp_1021.ui.otherpicking.SourceType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TempBorrowViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TempBorrowUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val mockInventory = listOf(
            TempTool("E280 1001 0001", "270000000001", "活动扳手 12 寸", "把", "A0101"),
            TempTool("E280 1001 0002", "270000000001", "活动扳手 12 寸", "把", "A0102"),
            TempTool("E280 1001 0003", "280000000001", "数显游标卡尺 0–150mm", "把", "B0101"),
            TempTool("E280 1001 0004", "280000000006", "数字万用表", "台", "B0102")
        )
        _uiState.update { it.copy(inventory = mockInventory) }
    }

    fun onQueryChange(value: String) {
        _uiState.update { it.copy(query = value) }
    }

    fun onTabSelected(value: Int) {
        _uiState.update { it.copy(selectedTab = value) }
    }

    fun onBorrowerChange(value: String) {
        _uiState.update { it.copy(borrower = value, message = null) }
    }

    fun onWorkOrderChange(value: String) {
        _uiState.update { it.copy(workOrderNo = value, message = null) }
    }

    fun onWorkshopChange(value: String) {
        _uiState.update { it.copy(workshop = value) }
    }

    fun onTeamChange(value: String) {
        _uiState.update { it.copy(team = value) }
    }

    fun onRemarkChange(value: String) {
        _uiState.update { it.copy(remark = value) }
    }

    fun startNewOrder() {
        _uiState.update {
            it.copy(
                editing = true, selectedOrderNo = null, query = "", borrower = "", workOrderNo = "",
                workshop = "", team = "", remark = "", selectedRfids = emptySet(), message = null
            )
        }
    }

    fun selectOrder(orderNo: String) {
        _uiState.update { it.copy(selectedOrderNo = orderNo, editing = false, message = null) }
    }

    fun showList() {
        _uiState.update {
            it.copy(
                selectedOrderNo = null,
                editing = false,
                query = "",
                confirmationRfid = null,
                message = null
            )
        }
    }

    fun toggleTool(rfid: String) {
        _uiState.update { state ->
            if (state.inventory.none { it.rfid == rfid }) state else state.copy(
                selectedRfids = if (rfid in state.selectedRfids) state.selectedRfids - rfid else state.selectedRfids + rfid,
                message = null
            )
        }
    }

    /**
     * 先保存器具清单再执行拣选；模拟单号只用于页面流程，不推定后端生成规则。
     */
    fun saveOrder() {
        _uiState.update { state ->
            val tools = state.inventory.filter { it.rfid in state.selectedRfids }
            when {
                state.borrower.isBlank() -> state.copy(message = "请填写借用人")
                state.workOrderNo.isBlank() -> state.copy(message = "请填写工单号")
                tools.isEmpty() -> state.copy(message = "请至少选择一件在库器具")
                else -> {
                    val mockOrder = TempBorrowOrder(
                        orderNo = "LSJC${System.currentTimeMillis()}",
                        workOrderNo = state.workOrderNo.trim(),
                        borrower = state.borrower.trim(),
                        workshop = state.workshop.trim(),
                        team = state.team.trim(),
                        remark = state.remark.trim(),
                        createdBy = "演示材料员",
                        createdAt = now(),
                        lines = tools.mapIndexed { index, tool -> TempBorrowLine(index + 1, tool) }
                    )
                    state.copy(
                        orders = state.orders + mockOrder, selectedOrderNo = mockOrder.orderNo,
                        inventory = state.inventory.filterNot { it.rfid in state.selectedRfids },
                        editing = false, selectedRfids = emptySet(), query = "", message = null
                    )
                }
            }
        }
    }

    fun pickingTask(): OtherPickingTaskUi? =
        pickingTasks().firstOrNull { it.sourceNo == _uiState.value.selectedOrderNo }

    fun pickingTasks(): List<OtherPickingTaskUi> = _uiState.value.orders.map { order ->
        OtherPickingTaskUi(
            sourceNo = order.orderNo, sourceType = SourceType.TEMPORARY, date = order.createdAt,
            tools = order.lines.map {
                OtherPickingToolUi(
                    it.tool.rfid,
                    it.tool.name,
                    it.tool.location
                )
            }
        )
    }

    /**
     * 只接收本模块来源任务的拣选结果，拣选完成不等同于确认借出。
     */
    fun syncPicking(tasks: List<OtherPickingTaskUi>) {
        _uiState.update { state ->
            val orderNos = state.orders.map { it.orderNo }.toSet()
            state.copy(pickedRfids = tasks.filter { it.sourceType == SourceType.TEMPORARY && it.sourceNo in orderNos }
                .flatMap { it.tools }.filter { it.completed }.map { it.rfid }.toSet())
        }
    }

    fun requestConfirmation(rfid: String?) {
        _uiState.update { it.copy(confirmationRfid = rfid, message = null) }
    }

    /**
     * 按用户确认的顺序，整单拣选完成后才允许逐条借出，重复确认不改变结果。
     */
    fun confirmLine() {
        _uiState.update { state ->
            val order = state.selectedOrder ?: return@update state
            val rfid = state.confirmationRfid ?: return@update state
            if (!order.lines.all { it.tool.rfid in state.pickedRfids }) {
                return@update state.copy(
                    confirmationRfid = null,
                    message = "请先完成本单全部器具的 PDA 拣选"
                )
            }
            state.copy(
                orders = state.orders.map { current ->
                    if (current.orderNo != order.orderNo) current else current.copy(lines = current.lines.map { line ->
                        if (line.tool.rfid != rfid || line.borrowed) line else line.copy(
                            borrowed = true,
                            borrowedAt = now()
                        )
                    })
                },
                confirmationRfid = null, message = "已确认借出，可在临时归还中查看待还记录"
            )
        }
    }

    private fun now(): String =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
}
