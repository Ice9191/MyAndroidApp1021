package com.hxb.myandroidapp_1021.pages.returns

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

class ReturnScanViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ReturnScanUiState(isLoading = true))
    val uiState: StateFlow<ReturnScanUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            delay(500.milliseconds)
            val mockOrder = ReturnOrder(
                orderNo = "GH20261111001",
                status = ReturnStatus.PARTIALLY_RETURNED,
                workshop = "检修一班",
                returner = "张伟",
                pendingCount = 3,
                returnedDate = "2026-11-11 09:30",
                totalCount = 5,
                returnedCount = 2,
                workOrderNo = "GD20261111001",
                createdBy = "材料员",
                createdAt = "2026-11-11 08:00"
            )
            val mockTools = listOf(
                ReturnToolScanItem(
                    id = 1,
                    name = "携带型接地线 110kV",
                    itemCode = "SJ202610270002-002",
                    rfid = "300202",
                    location = "C-01-02-01",
                    unit = "组",
                    warehouse = "C区精密仪器库",
                    condition = "好件"
                ),
                ReturnToolScanItem(
                    id = 2,
                    name = "绝缘操作杆",
                    itemCode = "SJ202610270002-003",
                    rfid = "300203",
                    location = "A1-2",
                    unit = "件",
                    warehouse = "C区精密仪器库",
                    condition = "坏件"
                ),
                ReturnToolScanItem(
                    id = 3,
                    name = "安全帽",
                    itemCode = "SJ202610270002-004",
                    rfid = "300204",
                    location = "A1-3",
                    unit = "个",
                    warehouse = "C区精密仪器库",
                    condition = "好件"
                )
            )
            // TODO: 字段未确定：仓库与货位候选、推荐规则和锁定范围需后端接口下发。
            val mockWarehouses = listOf("A区工具库", "B区工具库", "C区精密仪器库")
            val mockLocations = listOf("C-01-01-01", "C-01-01-02", "C-01-02-01")
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    order = state.order ?: mockOrder,
                    tools = mockTools,
                    warehouseOptions = mockWarehouses,
                    locationOptions = mockLocations
                )
            }
        }
    }

    /**
     * 新单据进入同一流程时清空上次读签结果；加载中的单据选择不会被模拟响应覆盖。
     */
    fun selectOrder(order: ReturnOrder) {
        _uiState.update { state ->
            state.copy(
                order = order,
                tools = state.tools.map { it.copy(isRead = false, readDate = "") },
                selectedLineId = null,
                showConfirmDrawer = false,
                draftLocation = "",
                draftWarehouse = "",
                submitError = null
            )
        }
    }

    /**
     * 点击明细打开单条上架抽屉：只选中并预置推荐仓库与货位，不提交、不改读取状态。
     */
    fun selectTool(lineId: Int) {
        _uiState.update { state ->
            val tool = state.tools.firstOrNull { it.id == lineId } ?: return@update state
            state.copy(
                selectedLineId = lineId,
                showConfirmDrawer = true,
                draftWarehouse = tool.warehouse,
                draftLocation = if (tool.isPutaway || tool.isBadCondition) "" else tool.location,
                submitError = null
            )
        }
    }

    fun onDraftWarehouseChange(value: String) {
        _uiState.update { it.copy(draftWarehouse = value) }
    }

    fun onDraftLocationChange(value: String) {
        _uiState.update { it.copy(draftLocation = value) }
    }

    /**
     * 关闭抽屉不提交，丢弃本次未保存的货位调整，保留已读取结果。
     */
    fun dismissConfirmDrawer() {
        _uiState.update { state ->
            if (state.isSubmitting) state
            else state.copy(
                showConfirmDrawer = false,
                selectedLineId = null,
                draftLocation = "",
                draftWarehouse = "",
                submitError = null
            )
        }
    }

    /**
     * 只提交当前选中明细：先要求完成 RFID 核对，成功后更新该明细与单据进度并回调已上架件数。
     * TODO: 字段未确定，接入真实接口后以响应结果更新明细、数量、状态与上架时间。
     */
    fun confirmPutaway(onSuccess: (String, Int) -> Unit) {
        val state = _uiState.value
        val order = state.order ?: return
        val tool = state.selectedTool ?: return
        val location = state.draftLocation.trim()
        if (state.isSubmitting || tool.isPutaway || location.isBlank()) return
        if (tool.isBadCondition) {
            _uiState.update { it.copy(submitError = "坏件不允许上架，请转送修或送检流程处理") }
            return
        }
        if (!tool.isRead) {
            _uiState.update { it.copy(submitError = "请先完成该器具的 RFID 核对") }
            return
        }
        _uiState.update { it.copy(isSubmitting = true, submitError = null) }
        viewModelScope.launch {
            // TODO: 字段未确定，确认后将 ReturnConfirmRequest(order.orderNo, tool.id, tool.rfid, location) 交给真实上架接口。
            delay(800.milliseconds)
            var doneCount = 0
            _uiState.update { current ->
                val updated = current.tools.map {
                    if (it.id == tool.id) {
                        it.copy(isPutaway = true, location = location, putawayAt = now())
                    } else {
                        it
                    }
                }
                doneCount = updated.count { it.isPutaway }
                current.copy(
                    tools = updated,
                    order = order.copy(
                        pendingCount = (updated.size - doneCount).coerceAtLeast(0),
                        returnedCount = doneCount,
                        status = when {
                            doneCount <= 0 -> ReturnStatus.UNRETURNED
                            doneCount >= updated.size -> ReturnStatus.RETURNED
                            else -> ReturnStatus.PARTIALLY_RETURNED
                        }
                    ),
                    showConfirmDrawer = false,
                    selectedLineId = null,
                    draftLocation = "",
                    draftWarehouse = "",
                    isSubmitting = false,
                    submitError = null
                )
            }
            onSuccess(order.orderNo, doneCount)
        }
    }

    private fun now(): String =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

    /**
     * 只切换本次读签状态及时间，不直接改动单据已完成数量或库存。
     */
    fun toggleRead(ids: Set<Int>) {
        val readDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        _uiState.update { state ->
            state.copy(tools = state.tools.map { tool ->
                if (tool.id !in ids) tool else tool.copy(
                    isRead = !tool.isRead,
                    readDate = if (tool.isRead) "" else readDate
                )
            })
        }
    }
}
