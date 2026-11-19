package com.hxb.myandroidapp_1021.feature.otherpicking.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.feature.otherpicking.domain.model.OtherPickingOrder
import com.hxb.myandroidapp_1021.feature.otherpicking.domain.model.PickingDetailStatus
import com.hxb.myandroidapp_1021.feature.otherpicking.domain.model.SourceType
import com.hxb.myandroidapp_1021.feature.otherpicking.domain.repository.OtherPickingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 其他拣选列表状态管理；数据经 [OtherPickingRepository] 获取，页面只观察只读状态。
 */
@HiltViewModel
class OtherPickingListViewModel @Inject constructor(
    private val otherPickingRepository: OtherPickingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OtherPickingListUiState())
    val uiState: StateFlow<OtherPickingListUiState> = _uiState.asStateFlow()

    /**
     * 刷新列表；首次进入由 PullToRefresh 在页面完全展示后自动触发。
     * 临时借出生成的临时拣选单不在仓库加载结果里，刷新后需要保留，避免被覆盖。
     */
    fun refresh() {
        if (_uiState.value.isRefreshing) return
        _uiState.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            val loadedOrders = otherPickingRepository.loadOrders()
            _uiState.update { state ->
                val loadedNumbers = loadedOrders.map { it.pickingOrderNo }.toSet()
                val localTemporaryOrders = state.orders.filter {
                    it.upstreamType == SourceType.TEMPORARY && it.pickingOrderNo !in loadedNumbers
                }
                state.copy(
                    isRefreshing = false,
                    hasLoaded = true,
                    orders = loadedOrders + localTemporaryOrders
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun showFilters() {
        _uiState.update { it.copy(showFilters = true, draftFilter = it.filter) }
    }

    fun dismissFilters() {
        _uiState.update { it.copy(showFilters = false) }
    }

    fun onFilterChange(filter: OtherPickingFilter) {
        _uiState.update { it.copy(draftFilter = filter) }
    }

    /** 重置筛选条件，列表随之恢复为全部来源、全部状态。 */
    fun resetFilters() {
        _uiState.update { it.copy(draftFilter = OtherPickingFilter(), filter = OtherPickingFilter()) }
    }

    /** 应用抽屉中的筛选条件并关闭抽屉。 */
    fun applyFilters() {
        _uiState.update { it.copy(filter = it.draftFilter, showFilters = false) }
    }

    /** 本批次提交成功后，把对应单据的明细标记为已拣选，列表进度立即更新。 */
    fun confirmPicked(pickingOrderNo: String, rfids: List<String>) {
        _uiState.update { state ->
            state.copy(
                orders = state.orders.map { order ->
                    if (order.pickingOrderNo == pickingOrderNo) {
                        order.markPicked(rfids.toSet())
                    } else {
                        order
                    }
                },
                toastMessage = "拣选成功",
                toastId = state.toastId + 1
            )
        }
    }

    /** 提示条展示结束或手动消失后清空消息。 */
    fun dismissToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    /**
     * 写入或合并临时借出生成的临时拣选单；已有单据保留其已拣选明细与关闭状态。
     */
    fun upsertTemporaryOrder(order: OtherPickingOrder) {
        _uiState.update { state ->
            val existing = state.orders.firstOrNull { it.pickingOrderNo == order.pickingOrderNo }
            val orders = if (existing == null) {
                state.orders + order
            } else {
                state.orders.map {
                    if (it.pickingOrderNo == order.pickingOrderNo) it.mergePickedFrom(order) else it
                }
            }
            state.copy(orders = orders)
        }
    }

    /**
     * 合并来源单据时保留本地已拣选的明细，避免重新生成任务后进度回退。
     */
    private fun OtherPickingOrder.mergePickedFrom(source: OtherPickingOrder): OtherPickingOrder {
        val pickedRfids = details
            .filter { it.status == PickingDetailStatus.PICKED }
            .map { it.rfid }
            .toSet()
        return source.copy(
            closed = closed || source.closed,
            details = source.details.map { detail ->
                if (detail.rfid in pickedRfids) detail.copy(status = PickingDetailStatus.PICKED) else detail
            }
        )
    }
}
