package com.hxb.myandroidapp_1021.feature.tempreturn.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.feature.otherputaway.domain.model.OtherPutawayDetail
import com.hxb.myandroidapp_1021.feature.otherputaway.domain.model.OtherPutawayOrder
import com.hxb.myandroidapp_1021.feature.otherputaway.domain.model.PutawayDetailStatus
import com.hxb.myandroidapp_1021.feature.otherputaway.domain.model.SourceType
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnDetailStatus
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnOrder
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.repository.TempReturnRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 临时归还列表的 ViewModel；归还单由临时借出确认到最后一件时自动生成，页面不再提供新建。
 */
@HiltViewModel
class TempReturnListViewModel @Inject constructor(
    private val repository: TempReturnRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TempReturnListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        refresh()
    }

    /**
     * 重新读取共享数据源；进入列表时调用，确保看到借出确认后新生成的归还单。
     */
    fun refresh() {
        viewModelScope.launch {
            val orders = repository.loadOrders()
            _uiState.update { it.copy(orders = orders) }
        }
    }

    fun onQueryChange(value: String) {
        _uiState.update { it.copy(query = value) }
    }

    fun onTabSelected(value: Int) {
        _uiState.update { it.copy(selectedTab = value) }
    }

    fun selectOrder(orderNo: String) {
        _uiState.update {
            it.copy(selectedOrderNo = orderNo)
        }
    }

    fun showList() {
        _uiState.update {
            it.copy(
                selectedOrderNo = null,
                query = ""
            )
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
            .asSequence()
            .filter { it.upstreamType == SourceType.TEMPORARY && it.upstreamNo in orderNos }
            .flatMap { it.details }
            .filter { it.status == PutawayDetailStatus.COMPLETED }
            .map { it.rfid }
            .toSet()
        _uiState.update { it.copy(putawayRfids = putawayRfids) }
    }
}
