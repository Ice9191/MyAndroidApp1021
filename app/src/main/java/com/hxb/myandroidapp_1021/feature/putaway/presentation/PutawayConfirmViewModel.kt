package com.hxb.myandroidapp_1021.feature.putaway.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.feature.putaway.domain.model.PutawayDetail
import com.hxb.myandroidapp_1021.feature.putaway.domain.model.PutawayOrder
import com.hxb.myandroidapp_1021.feature.putaway.domain.repository.PutawayRepository
import com.hxb.myandroidapp_1021.utils.SimulatedRfidScan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PutawayConfirmViewModel @Inject constructor(
    private val putawayRepository: PutawayRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PutawayConfirmUiState())
    val uiState: StateFlow<PutawayConfirmUiState> = _uiState.asStateFlow()

    private var details: List<PutawayDetail> = emptyList()
    private var locationWarehouse: String? = null
    private var scannedRfids: Set<String> = emptySet()
    private var selectedLineNo: String? = null

    /**
     * 绑定当前上架单；换单时清空扫描、筛选和详情抽屉状态。
     */
    fun bindOrder(order: PutawayOrder?) {
        val newDetails = order?.details.orEmpty()
        if (_uiState.value.hasOrder == (order != null) && details == newDetails) return
        details = newDetails
        locationWarehouse = null
        scannedRfids = emptySet()
        selectedLineNo = null
        _uiState.update {
            it.copy(
                hasOrder = order != null,
                scanQuery = "",
                visibleDetails = emptyList(),
                selectedDetail = null
            )
        }
        refreshDetails()
    }

    /**
     * 扫描/输入的货位码解析为仓库，用于过滤明细；输入已变化时丢弃旧的解析结果。
     */
    fun onScanQueryChange(value: String) {
        _uiState.update { it.copy(scanQuery = value) }
        locationWarehouse = null
        refreshDetails()
        viewModelScope.launch {
            val location = putawayRepository.validateLocation(value.trim())
            if (_uiState.value.scanQuery != value) return@launch
            locationWarehouse = location?.warehouse
            refreshDetails()
        }
    }

    /**
     * 模拟一次扫描：从当前可见明细中随机选一个未扫描 RFID。
     */
    fun onScanClick() {
        val candidates = _uiState.value.visibleDetails.map { it.detail.rfid }
        val rfid = SimulatedRfidScan.scan(candidates, scannedRfids) ?: return
        scannedRfids = scannedRfids + rfid
        refreshDetails()
    }

    fun selectDetail(lineNo: String) {
        selectedLineNo = lineNo
        refreshDetails()
    }

    fun clearSelectedDetail() {
        selectedLineNo = null
        refreshDetails()
    }

    /**
     * 统一计算可见明细、已扫描/总数和当前抽屉明细，界面只负责渲染。
     */
    private fun refreshDetails() {
        val query = _uiState.value.scanQuery.trim()
        val visible = details
            .filter { matchesQuery(it, query) }
            .map(::toUi)
        val selected = details.firstOrNull { it.lineNo == selectedLineNo }?.let(::toUi)
        _uiState.update { it.copy(visibleDetails = visible, selectedDetail = selected) }
    }

    private fun toUi(detail: PutawayDetail): PutawayDetailUi {
        val group = details.filter { sameMaterial(it, detail) }
        return PutawayDetailUi(
            detail = detail,
            quantity = "${group.count { it.rfid in scannedRfids }}/${group.size}",
            isScanned = detail.rfid in scannedRfids
        )
    }

    private fun matchesQuery(detail: PutawayDetail, query: String): Boolean {
        if (query.isEmpty()) return true
        val textMatched =
            detail.orderNo.contains(query, true) ||
            detail.lineNo.contains(query, true) ||
            detail.rfid.contains(query, true) ||
            detail.itemCode.contains(query, true) ||
            detail.description.contains(query, true) ||
            detail.warehouse.contains(query, true) ||
            detail.batchNo.contains(query, true)
        return textMatched || locationWarehouse?.let { detail.warehouse == it } == true
    }

    private fun sameMaterial(item: PutawayDetail, detail: PutawayDetail): Boolean =
        if (detail.itemCode.isNotBlank()) {
            item.itemCode == detail.itemCode
        } else {
            item.description == detail.description
        }
}
