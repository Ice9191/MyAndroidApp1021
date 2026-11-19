package com.hxb.myandroidapp_1021.feature.tempreturn.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.feature.tempborrow.domain.model.TempBorrowDetailStatus
import com.hxb.myandroidapp_1021.feature.tempborrow.domain.model.TempBorrowOrder
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnCreateDetail
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnCreateRequest
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnDetailStatus
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnOrder
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnPendingTool
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.repository.TempReturnRepository
import com.hxb.myandroidapp_1021.feature.otherputaway.domain.model.OtherPutawayDetail
import com.hxb.myandroidapp_1021.feature.otherputaway.domain.model.OtherPutawayOrder
import com.hxb.myandroidapp_1021.feature.otherputaway.domain.model.PutawayDetailStatus
import com.hxb.myandroidapp_1021.feature.otherputaway.domain.model.SourceType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 临时归还列表与新建表单的 ViewModel；确认归还逻辑在 TempReturnConfirmViewModel。
 */
@HiltViewModel
class TempReturnViewModel @Inject constructor(
    private val repository: TempReturnRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TempReturnUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val orders = repository.loadOrders()
            _uiState.update { it.copy(orders = orders) }
        }
        viewModelScope.launch {
            val pendingTools = repository.loadPendingTools()
            _uiState.update { it.copy(pendingTools = pendingTools) }
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

    fun onReturnerChange(value: String) {
        _uiState.update { it.copy(returner = value, notificationMessage = null) }
    }

    fun onWorkOrderChange(value: String) {
        _uiState.update { it.copy(workOrderNo = value, notificationMessage = null) }
    }

    fun onReturnLocationChange(value: String) {
        _uiState.update { it.copy(returnLocation = value, notificationMessage = null) }
    }

    fun onRemarkChange(value: String) {
        _uiState.update { it.copy(remark = value, notificationMessage = null) }
    }

    fun startNewOrder() {
        _uiState.update {
            it.copy(
                editing = true,
                selectedOrderNo = null,
                query = "",
                toolQuery = "",
                returner = "",
                workOrderNo = "",
                returnLocation = "",
                remark = "",
                selectedRfids = emptySet(),
                isSubmitting = false,
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
                notificationMessage = null
            )
        }
    }

    /**
     * 从待还器具中增删本次归还的器具；添加第一件时用工单号预填空白的表单。
     */
    fun toggleTool(rfid: String) {
        _uiState.update { state ->
            val tool = state.pendingTools.firstOrNull { it.rfid == rfid } ?: return@update state
            val adding = rfid !in state.selectedRfids
            state.copy(
                selectedRfids = if (adding) {
                    state.selectedRfids + rfid
                } else {
                    state.selectedRfids - rfid
                },
                workOrderNo = if (adding && state.workOrderNo.isBlank()) {
                    tool.workOrderNo
                } else {
                    state.workOrderNo
                },
                notificationMessage = null
            )
        }
    }

    /**
     * 保存归还单并返回列表；单号由数据源模拟生成。
     */
    fun saveOrder() {
        val state = _uiState.value
        if (state.isSubmitting) return
        val selectedTools = state.selectedTools
        val error = when {
            state.returner.isBlank() -> "请填写归还人"
            state.workOrderNo.isBlank() -> "请填写工单号"
            state.returnLocation.isBlank() -> "请填写归还库位"
            selectedTools.isEmpty() -> "请至少添加一件器具"
            else -> null
        }
        if (error != null) {
            notify(error)
            return
        }
        _uiState.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            val order = repository.createOrder(
                TempReturnCreateRequest(
                    returner = state.returner.trim(),
                    workOrderNo = state.workOrderNo.trim(),
                    returnLocation = state.returnLocation.trim(),
                    remark = state.remark.trim(),
                    details = selectedTools.map {
                        TempReturnCreateDetail(
                            rfid = it.rfid,
                            itemCode = it.itemCode,
                            description = it.description,
                            unit = it.unit
                        )
                    }
                )
            )
            val selectedRfids = state.selectedRfids
            _uiState.update { current ->
                current.copy(
                    orders = current.orders + order,
                    pendingTools = current.pendingTools.filterNot {
                        it.rfid in selectedRfids
                    },
                    selectedOrderNo = null,
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
    fun updateOrder(order: TempReturnOrder) {
        _uiState.update { state ->
            state.copy(
                orders = state.orders.map {
                    if (it.orderNo == order.orderNo) order else it
                }
            )
        }
    }

    /**
     * 把临时借出模块已借出的明细投影成待还器具；已进入归还单的 RFID 不再重复展示。
     */
    fun syncBorrowedTools(borrowedOrders: List<TempBorrowOrder>) {
        _uiState.update { state ->
            val occupied = state.orders.flatMap { order ->
                order.details.map { it.rfid }
            }.toSet()
            val pending = (borrowedOrders.flatMap { borrow ->
                borrow.details
                    .filter { it.status == TempBorrowDetailStatus.BORROWED }
                    .filterNot { it.rfid in occupied }
                    .map { detail ->
                        TempReturnPendingTool(
                            borrowOrderNo = borrow.orderNo,
                            workOrderNo = borrow.workOrderNo,
                            borrower = borrow.borrower,
                            rfid = detail.rfid,
                            itemCode = detail.itemCode,
                            description = detail.description,
                            unit = detail.unit
                        )
                    }
            } + state.pendingTools)
                .filterNot { it.rfid in occupied }
                .distinctBy { it.rfid }
            val pendingRfids = pending.map { it.rfid }.toSet()
            state.copy(
                pendingTools = pending,
                selectedRfids = state.selectedRfids.intersect(pendingRfids)
            )
        }
    }

    /**
     * 已确认归还的明细生成 PDA 上架任务单，货位取归还单登记的归还库位。
     * TODO: 字段未确定：上架单号生成规则、上架单明细编码与单位字段需与后端确认。
     */
    fun putawayTasks(): List<OtherPutawayOrder> {
        val putawayRfids = _uiState.value.putawayRfids
        return _uiState.value.orders.mapNotNull { order ->
            val returned = order.details.filter { it.status == TempReturnDetailStatus.RETURNED }
            if (returned.isEmpty()) {
                null
            } else {
                OtherPutawayOrder(
                    putawayOrderNo = "SJ${order.orderNo}",
                    upstreamType = SourceType.TEMPORARY,
                    upstreamNo = order.orderNo,
                    createdAt = order.createdAt,
                    details = returned.map {
                        OtherPutawayDetail(
                            lineNo = it.lineNo,
                            rfid = it.rfid,
                            itemCode = it.itemCode,
                            description = it.description,
                            unit = it.unit,
                            location = order.returnLocation,
                            status = if (it.rfid in putawayRfids) {
                                PutawayDetailStatus.COMPLETED
                            } else {
                                PutawayDetailStatus.PENDING
                            }
                        )
                    }
                )
            }
        }
    }

    /**
     * 只接收本模块来源任务的上架结果；上架完成不等同于确认归还。
     */
    fun syncPutaway(tasks: List<OtherPutawayOrder>) {
        val orderNos = _uiState.value.orders.map { it.orderNo }.toSet()
        val putawayRfids = tasks
            .filter { it.upstreamType == SourceType.TEMPORARY && it.upstreamNo in orderNos }
            .flatMap { it.details }
            .filter { it.status == PutawayDetailStatus.COMPLETED }
            .map { it.rfid }
            .toSet()
        _uiState.update { it.copy(putawayRfids = putawayRfids) }
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
