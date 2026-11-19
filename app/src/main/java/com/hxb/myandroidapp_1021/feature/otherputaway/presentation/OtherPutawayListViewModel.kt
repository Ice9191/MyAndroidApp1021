package com.hxb.myandroidapp_1021.feature.otherputaway.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.feature.otherputaway.domain.model.OtherPutawayOrder
import com.hxb.myandroidapp_1021.feature.otherputaway.domain.model.PutawayDetailStatus
import com.hxb.myandroidapp_1021.feature.otherputaway.domain.model.SourceType
import com.hxb.myandroidapp_1021.feature.otherputaway.domain.repository.OtherPutawayRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 其他上架列表状态管理；数据经 [OtherPutawayRepository] 获取，页面只观察只读状态。
 */
@HiltViewModel
class OtherPutawayListViewModel @Inject constructor(
    private val otherPutawayRepository: OtherPutawayRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OtherPutawayListUiState())
    val uiState: StateFlow<OtherPutawayListUiState> = _uiState.asStateFlow()

    /**
     * 刷新列表；首次进入由 PullToRefresh 在页面完全展示后自动触发。
     * 临时归还生成的临时上架单不在仓库加载结果里，刷新后需要保留，避免被覆盖。
     */
    fun refresh() {
        if (_uiState.value.isRefreshing) return
        _uiState.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            val loadedOrders = otherPutawayRepository.loadOrders()
            _uiState.update { state ->
                val loadedNumbers = loadedOrders.map { it.putawayOrderNo }.toSet()
                val localTemporaryOrders = state.orders.filter {
                    it.upstreamType == SourceType.TEMPORARY && it.putawayOrderNo !in loadedNumbers
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

    fun onFilterChange(filter: OtherPutawayFilter) {
        _uiState.update { it.copy(draftFilter = filter) }
    }

    /** 重置筛选条件，列表随之恢复为全部来源、全部状态。 */
    fun resetFilters() {
        _uiState.update { it.copy(draftFilter = OtherPutawayFilter(), filter = OtherPutawayFilter()) }
    }

    /** 应用抽屉中的筛选条件并关闭抽屉。 */
    fun applyFilters() {
        _uiState.update { it.copy(filter = it.draftFilter, showFilters = false) }
    }

    /** 扫描页确认单件上架后回写单据，列表卡片的上架进度立即更新。 */
    fun updateOrder(order: OtherPutawayOrder) {
        _uiState.update { state ->
            state.copy(
                orders = state.orders.map {
                    if (it.putawayOrderNo == order.putawayOrderNo) order else it
                }
            )
        }
    }

    /**
     * 写入或合并临时归还生成的临时上架单；已有单据保留其已上架明细与关闭状态。
     */
    fun upsertTemporaryOrder(order: OtherPutawayOrder) {
        _uiState.update { state ->
            val existing = state.orders.firstOrNull { it.putawayOrderNo == order.putawayOrderNo }
            val orders = if (existing == null) {
                state.orders + order
            } else {
                state.orders.map {
                    if (it.putawayOrderNo == order.putawayOrderNo) it.mergeCompletedFrom(order) else it
                }
            }
            state.copy(orders = orders)
        }
    }

    /**
     * 合并来源单据时保留本地已上架的明细，避免重新生成任务后进度回退。
     */
    private fun OtherPutawayOrder.mergeCompletedFrom(source: OtherPutawayOrder): OtherPutawayOrder {
        val completedRfids = details
            .filter { it.status == PutawayDetailStatus.COMPLETED }
            .map { it.rfid }
            .toSet()
        return source.copy(
            closed = closed || source.closed,
            details = source.details.map { detail ->
                if (detail.rfid in completedRfids) {
                    detail.copy(status = PutawayDetailStatus.COMPLETED)
                } else {
                    detail
                }
            }
        )
    }
}
