package com.hxb.myandroidapp_1021.pages.temp

import androidx.lifecycle.ViewModel
import com.hxb.myandroidapp_1021.pages.otherpicking.OtherPickingTaskUi
import com.hxb.myandroidapp_1021.pages.otherpicking.OtherPickingToolUi
import com.hxb.myandroidapp_1021.pages.otherpicking.SourceType
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
        updateState { it.copy(inventory = mockInventory) }
    }

    fun onQueryChange(value: String) {
        updateState { it.copy(query = value) }
    }

    fun onTabSelected(value: Int) {
        updateState { it.copy(selectedTab = value) }
    }

    fun onBorrowerChange(value: String) {
        updateState { it.copy(borrower = value, notificationMessage = null) }
    }

    fun onWorkOrderChange(value: String) {
        updateState { it.copy(workOrderNo = value, notificationMessage = null) }
    }

    fun onWorkshopChange(value: String) {
        updateState { it.copy(workshop = value) }
    }

    fun onTeamChange(value: String) {
        updateState { it.copy(team = value) }
    }

    fun onRemarkChange(value: String) {
        updateState { it.copy(remark = value) }
    }

    fun startNewOrder() {
        updateState {
            it.copy(
                editing = true,
                selectedOrderNo = null,
                query = "",
                borrower = "",
                workOrderNo = "",
                workshop = "",
                team = "",
                remark = "",
                selectedRfids = emptySet(),
                notificationMessage = null
            )
        }
    }

    fun selectOrder(orderNo: String) {
        updateState {
            it.copy(
                selectedOrderNo = orderNo,
                editing = false,
                notificationMessage = null
            )
        }
    }

    fun showList() {
        updateState {
            it.copy(
                selectedOrderNo = null,
                editing = false,
                query = "",
                confirmationRfid = null,
                notificationMessage = null
            )
        }
    }

    fun toggleTool(rfid: String) {
        updateState { state ->
            if (state.inventory.none { it.rfid == rfid }) state else state.copy(
                selectedRfids = if (rfid in state.selectedRfids) state.selectedRfids - rfid else state.selectedRfids + rfid,
                notificationMessage = null
            )
        }
    }

    /**
     * 先保存器具清单再执行拣选；模拟单号只用于页面流程，不推定后端生成规则。
     */
    fun saveOrder() {
        updateState { state ->
            val tools = state.inventory.filter { it.rfid in state.selectedRfids }
            when {
                state.borrower.isBlank() -> state.copy(
                    notificationMessage = "请填写借用人",
                    notificationId = state.notificationId + 1
                )

                state.workOrderNo.isBlank() -> state.copy(
                    notificationMessage = "请填写工单号",
                    notificationId = state.notificationId + 1
                )

                tools.isEmpty() -> state.copy(
                    notificationMessage = "请至少选择一件在库器具",
                    notificationId = state.notificationId + 1
                )

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
                        orders = state.orders + mockOrder,
                        selectedOrderNo = mockOrder.orderNo,
                        inventory = state.inventory.filterNot { it.rfid in state.selectedRfids },
                        editing = false,
                        selectedRfids = emptySet(),
                        query = "",
                        notificationMessage = null
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
        updateState { state ->
            val orderNos = state.orders.map { it.orderNo }.toSet()
            state.copy(pickedRfids = tasks.filter { it.sourceType == SourceType.TEMPORARY && it.sourceNo in orderNos }
                .flatMap { it.tools }.filter { it.completed }.map { it.rfid }.toSet())
        }
    }

    fun requestConfirmation(rfid: String?) {
        updateState { it.copy(confirmationRfid = rfid, notificationMessage = null) }
    }

    /**
     * 按用户确认的顺序，整单拣选完成后才允许逐条借出，重复确认不改变结果。
     */
    fun confirmLine() {
        updateState { state ->
            val order = state.selectedOrder ?: return@updateState state
            val rfid = state.confirmationRfid ?: return@updateState state
            if (!order.lines.all { it.tool.rfid in state.pickedRfids }) {
                return@updateState state.copy(
                    confirmationRfid = null,
                    notificationMessage = "请先完成本单全部器具的 PDA 拣选",
                    notificationId = state.notificationId + 1
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
                confirmationRfid = null,
                notificationMessage = "已确认借出，可在临时归还中查看待还记录",
                notificationId = state.notificationId + 1
            )
        }
    }

    fun dismissNotification() {
        updateState { it.copy(notificationMessage = null) }
    }

    private fun now(): String =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

    /**
     * 所有状态变更统一走这里，先改业务状态，再刷新筛选列表和派生状态。
     */
    private fun updateState(transform: (TempBorrowUiState) -> TempBorrowUiState) {
        _uiState.update { transform(it).withDerivedState() }
    }

    /**
     * 刷新查询条件、标签页对应的列表，以及所选单据和整单拣选进度；展示字段由页面拼装。
     */
    private fun TempBorrowUiState.withDerivedState(): TempBorrowUiState {
        val visibleOrders = orders.filter { order ->
            (query.isBlank() || listOf(
                order.orderNo,
                order.workOrderNo,
                order.borrower
            ).any { value -> value.contains(query.trim(), true) }) &&
                    when (selectedTab) {
                        1 -> order.status == "草稿"
                        2 -> order.status == "已借出"
                        else -> true
                    }
        }
        val visibleTools = inventory.filter { tool ->
            query.isBlank() || listOf(tool.rfid, tool.itemCode, tool.name).any { value ->
                value.contains(
                    query.trim(),
                    true
                )
            }
        }
        val order = orders.firstOrNull { it.orderNo == selectedOrderNo }
        return copy(
            selectedOrder = order,
            visibleOrders = visibleOrders,
            visibleTools = visibleTools,
            allPicked = order?.lines?.all { it.tool.rfid in pickedRfids } ?: false
        )
    }
}
