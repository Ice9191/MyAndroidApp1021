package com.hxb.myandroidapp_1021.feature.tempborrow.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.feature.otherpicking.domain.model.OtherPickingDetail
import com.hxb.myandroidapp_1021.feature.otherpicking.domain.model.OtherPickingOrder
import com.hxb.myandroidapp_1021.feature.otherpicking.domain.model.PickingDetailStatus
import com.hxb.myandroidapp_1021.feature.otherpicking.domain.model.SourceType
import com.hxb.myandroidapp_1021.feature.tempborrow.domain.model.TempBorrowCreateRequest
import com.hxb.myandroidapp_1021.feature.tempborrow.domain.model.TempBorrowDetailStatus
import com.hxb.myandroidapp_1021.feature.tempborrow.domain.model.TempBorrowOrder
import com.hxb.myandroidapp_1021.feature.tempborrow.domain.repository.TempBorrowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TempBorrowListViewModel @Inject constructor(
    private val repository: TempBorrowRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TempBorrowListUiState())
    val uiState = _uiState.asStateFlow()

    private var inventoryLocations: Map<String, String> = emptyMap()

    init {
        viewModelScope.launch {
            val inventory = repository.loadInventory()
            val orders = repository.loadOrders()
            val workshops = repository.loadWorkshops()
            val workGroups = repository.loadWorkGroups()
            val borrowers = repository.loadBorrowers()
            inventoryLocations = inventory.associate { it.rfid to it.location }
            _uiState.update {
                it.copy(
                    inventory = inventory,
                    orders = orders,
                    workshopOptions = workshops,
                    workGroupOptions = workGroups,
                    borrowerOptions = borrowers
                )
            }
        }
    }

    fun onQueryChange(value: String) {
        _uiState.update { it.copy(query = value) }
    }

    fun onToolQueryChange(value: String) {
        _uiState.update { it.copy(toolQuery = value) }
    }

    fun onTabSelected(value: Int) {
        _uiState.update { it.copy(selectedTab = value) }
    }

    fun onBorrowerChange(value: String) {
        _uiState.update { it.copy(borrower = value, notificationMessage = null) }
    }

    fun onWorkOrderChange(value: String) {
        _uiState.update { it.copy(workOrderNo = value, notificationMessage = null) }
    }

    fun onWorkshopChange(value: String) {
        _uiState.update { it.copy(workshop = value) }
    }

    fun onWorkGroupChange(value: String) {
        _uiState.update { it.copy(workGroup = value) }
    }

    fun onRemarkChange(value: String) {
        _uiState.update { it.copy(remark = value) }
    }

    /**
     * 打开车间/工班/借用人查询弹窗，每次打开清空上一次的搜索关键字。
     */
    fun onQueryFieldClick(field: TempBorrowQueryField) {
        _uiState.update {
            it.copy(queryField = field, queryKeyword = "", notificationMessage = null)
        }
    }

    fun onQueryKeywordChange(value: String) {
        _uiState.update { it.copy(queryKeyword = value) }
    }

    /**
     * 把查询弹窗选中的候选值回填到对应字段并关闭弹窗。
     */
    fun onQueryOptionSelected(value: String) {
        _uiState.update { state ->
            val updated = when (state.queryField) {
                TempBorrowQueryField.WORKSHOP -> state.copy(workshop = value)
                TempBorrowQueryField.WORK_GROUP -> state.copy(workGroup = value)
                TempBorrowQueryField.BORROWER -> state.copy(borrower = value)
                null -> state
            }
            updated.copy(queryField = null, queryKeyword = "")
        }
    }

    fun closeQueryDialog() {
        _uiState.update { it.copy(queryField = null, queryKeyword = "") }
    }

    fun startNewOrder() {
        _uiState.update {
            it.copy(
                editing = true,
                selectedOrderNo = null,
                query = "",
                toolQuery = "",
                borrower = "",
                workOrderNo = "",
                workshop = "",
                workGroup = "",
                remark = "",
                selectedRfids = emptySet(),
                isSubmitting = false,
                queryField = null,
                queryKeyword = "",
                notificationMessage = null
            )
        }
    }

    fun selectOrder(orderNo: String) {
        _uiState.update {
            it.copy(
                selectedOrderNo = orderNo,
                editing = false,
                notificationMessage = null
            )
        }
    }

    fun showList() {
        _uiState.update {
            it.copy(
                selectedOrderNo = null,
                editing = false,
                query = "",
                toolQuery = "",
                queryField = null,
                queryKeyword = "",
                notificationMessage = null
            )
        }
    }

    fun toggleTool(rfid: String) {
        _uiState.update { state ->
            if (state.inventory.none { it.rfid == rfid }) {
                state
            } else {
                state.copy(
                    selectedRfids = if (rfid in state.selectedRfids) {
                        state.selectedRfids - rfid
                    } else {
                        state.selectedRfids + rfid
                    },
                    notificationMessage = null
                )
            }
        }
    }

    /**
     * 保存器具清单并返回单据列表；单号由数据源模拟生成。
     */
    fun saveOrder() {
        val state = _uiState.value
        if (state.isSubmitting) return
        val selectedRfids = state.selectedRfids
        val error = when {
            state.borrower.isBlank() -> "请填写借用人"
            state.workOrderNo.isBlank() -> "请填写工单号"
            state.inventory.none { it.rfid in selectedRfids } -> "请至少添加一件器具"
            else -> null
        }
        if (error != null) {
            notify(error)
            return
        }
        _uiState.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            val order = repository.createOrder(
                TempBorrowCreateRequest(
                    workOrderNo = state.workOrderNo.trim(),
                    borrower = state.borrower.trim(),
                    workshop = state.workshop.trim(),
                    workGroup = state.workGroup.trim(),
                    remark = state.remark.trim(),
                    rfids = selectedRfids.toList()
                )
            )
            _uiState.update { current ->
                current.copy(
                    orders = current.orders + order,
                    selectedOrderNo = null,
                    inventory = current.inventory.filterNot { it.rfid in selectedRfids },
                    editing = false,
                    selectedRfids = emptySet(),
                    query = "",
                    toolQuery = "",
                    isSubmitting = false
                )
            }
        }
    }

    /**
     * 确认页回传整单后替换对应单据，供返回列表时展示最新状态与进度。
     */
    fun updateOrder(order: TempBorrowOrder) {
        _uiState.update { state ->
            state.copy(
                orders = state.orders.map {
                    if (it.orderNo == order.orderNo) order else it
                }
            )
        }
    }

    // TODO: 字段未确定：临时借出单转临时拣选单时，拣选单号、物资编码、单位、批次号、仓库的接口字段与取值待后端确认。
    fun pickingTasks(): List<OtherPickingOrder> = _uiState.value.orders.mapNotNull { order ->
        val pickedRfids = _uiState.value.pickedRfids
        val pending = order.details.filter { it.status == TempBorrowDetailStatus.PENDING }
        if (pending.isEmpty()) {
            null
        } else {
            OtherPickingOrder(
                pickingOrderNo = "XJ${order.orderNo}",
                upstreamType = SourceType.TEMPORARY,
                upstreamNo = order.orderNo,
                workshop = order.workshop,
                workGroup = order.workGroup,
                plannedDate = order.createdAt.take(10),
                details = pending.map { detail ->
                    OtherPickingDetail(
                        lineNo = detail.lineNo,
                        rfid = detail.rfid,
                        itemCode = "",
                        description = detail.description,
                        unit = "",
                        batchNo = "",
                        warehouse = "",
                        location = inventoryLocations[detail.rfid].orEmpty(),
                        status = if (detail.rfid in pickedRfids) {
                            PickingDetailStatus.PICKED
                        } else {
                            PickingDetailStatus.UNPICKED
                        }
                    )
                }
            )
        }
    }

    /**
     * 只接收本模块来源任务的拣选结果；拣选完成不等同于确认借出。
     */
    fun syncPicking(tasks: List<OtherPickingOrder>) {
        val orderNos = _uiState.value.orders.map { it.orderNo }.toSet()
        val pickedRfids = tasks
            .asSequence()
            .filter { it.upstreamType == SourceType.TEMPORARY && it.upstreamNo in orderNos }
            .flatMap { it.details }
            .filter { it.status == PickingDetailStatus.PICKED }
            .map { it.rfid }
            .toSet()
        _uiState.update { it.copy(pickedRfids = pickedRfids) }
    }

    fun dismissNotification() {
        _uiState.update {
            it.copy(notificationMessage = null, notificationId = it.notificationId + 1)
        }
    }

    private fun notify(message: String) {
        _uiState.update {
            it.copy(notificationMessage = message, notificationId = it.notificationId + 1)
        }
    }
}
