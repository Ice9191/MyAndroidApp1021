package com.hxb.myandroidapp_1021.feature.putaway.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.core.rfid.RfidScanner
import com.hxb.myandroidapp_1021.core.rfid.ScanInput
import com.hxb.myandroidapp_1021.core.scanner.BarcodeScanner
import com.hxb.myandroidapp_1021.feature.putaway.domain.model.PutawayDetail
import com.hxb.myandroidapp_1021.feature.putaway.domain.model.PutawayOrder
import com.hxb.myandroidapp_1021.feature.putaway.domain.model.PutawayRequest
import com.hxb.myandroidapp_1021.feature.putaway.domain.repository.PutawayRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class PutawayConfirmViewModel @Inject constructor(
    private val putawayRepository: PutawayRepository,
    private val rfidScanner: RfidScanner,
    private val barcodeScanner: BarcodeScanner
) : ViewModel() {
    private val _uiState = MutableStateFlow(PutawayConfirmUiState())
    val uiState: StateFlow<PutawayConfirmUiState> = _uiState.asStateFlow()

    private var details: List<PutawayDetail> = emptyList()
    private var orderNo: String? = null
    private var locationWarehouse: String? = null
    private var scannedRfids: Set<String> = emptySet()
    private var selectedLineNo: String? = null
    private var scanKeyJob: Job? = null
    private var scanJob: Job? = null
    private var putawayJob: Job? = null
    private var isLocationScanning = false

    /**
     * 绑定当前上架单；换单时清空扫描、筛选和详情抽屉状态。
     */
    fun bindOrder(order: PutawayOrder?) {
        val newDetails = order?.details.orEmpty().filterNot { it.isCompleted }
        if (orderNo == order?.orderNo) {
            details = newDetails
            scannedRfids = scannedRfids.intersect(details.map { it.rfid }.toSet())
            refreshDetails()
            return
        }
        stopRfidScan()
        putawayJob?.cancel()
        orderNo = order?.orderNo
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
        stopRfidScan()
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

    fun startScanKeyListener(onPutawayConfirmed: (PutawayRequest) -> Boolean) {
        val previousListener = scanKeyJob
        if (previousListener?.isActive == true) return
        scanKeyJob = viewModelScope.launch {
            previousListener?.join()
            val rfidPressed = MutableStateFlow(false)
            val readerPreparation = launch {
                try {
                    rfidScanner.prepareReader()
                } catch (failure: CancellationException) {
                    throw failure
                } catch (failure: Exception) {
                    Log.w("PutawayConfirm", "预打开 RFID 读头失败，按键时将重试", failure)
                }
            }
            try {
                rfidScanner.scanKeyInput()
                    .onEach { input -> rfidPressed.value = input == ScanInput.RFID }
                    .filter { input ->
                        when (input) {
                            ScanInput.RFID -> scanJob == null || isLocationScanning
                            ScanInput.BARCODE -> true
                            null -> false
                        }
                    }
                    .collectLatest { input ->
                        when (input) {
                            ScanInput.RFID -> scanWhilePressed(rfidPressed)
                            ScanInput.BARCODE -> scanLocation(onPutawayConfirmed)
                            null -> Unit
                        }
                    }
            } catch (failure: CancellationException) {
                throw failure
            } catch (failure: Exception) {
                showNotification(failure.message ?: "无法连接实体扫描键")
            } finally {
                withContext(NonCancellable) {
                    readerPreparation.cancelAndJoin()
                    rfidScanner.releaseReader()
                }
            }
        }
    }

    fun stopScanKeyListener() {
        scanKeyJob?.cancel()
    }

    /**
     * 从实际盘点开始保留至少一秒读签时间。长按超过一秒时，松手后停止盘点。
     * 按文档“上架单”流程，读到的单件等待扫码确认货位。
     */
    private suspend fun scanWhilePressed(rfidPressed: StateFlow<Boolean>) {
        if (putawayJob?.isActive == true) return
        val candidates = _uiState.value.visibleDetails
            .map { it.detail }
            .filter { !it.isCompleted && it.rfid !in scannedRfids }
            .map { it.rfid }
            .toSet()
        if (candidates.isEmpty()) return

        scanJob = currentCoroutineContext().job
        try {
            coroutineScope {
                val inventoryStarted = CompletableDeferred<Unit>()
                val inventory = launch {
                    rfidScanner.scanEpcs { active ->
                        _uiState.update { it.copy(isScanning = active) }
                        if (active) inventoryStarted.complete(Unit)
                    }.collect { epc ->
                        val rfid = putawayRepository.findRfidByEpc(epc) ?: return@collect
                        if (rfid !in candidates || rfid in scannedRfids) return@collect
                        scannedRfids = scannedRfids + rfid
                        refreshDetails()
                    }
                }
                try {
                    inventoryStarted.await()
                    delay(1.seconds)
                    rfidPressed.first { !it }
                } finally {
                    inventory.cancel()
                }
            }
        } catch (failure: CancellationException) {
            throw failure
        } catch (failure: Exception) {
            showNotification(failure.message ?: "RFID 扫描失败")
        } finally {
            scanJob = null
            _uiState.update { it.copy(isScanning = false) }
        }
    }

    private suspend fun scanLocation(onPutawayConfirmed: (PutawayRequest) -> Boolean) {
        if (orderNo == null || putawayJob?.isActive == true) return
        scanJob = currentCoroutineContext().job
        isLocationScanning = true
        val locationCode = try {
            barcodeScanner.scan {
                _uiState.update { it.copy(isScanning = true) }
            }
        } catch (failure: CancellationException) {
            throw failure
        } catch (failure: Exception) {
            showNotification(failure.message ?: "货位码扫描失败")
            return
        } finally {
            scanJob = null
            isLocationScanning = false
            _uiState.update { it.copy(isScanning = false) }
        }
        if (locationCode == null) {
            showNotification("未读到货位码")
            return
        }
        submitScannedDetails(locationCode, onPutawayConfirmed)
    }

    /**
     * 只提交扫货位前已识别的待上架明细。成功一件移除一件，失败明细留在本单方便重试。
     */
    private fun submitScannedDetails(locationCode: String, onPutawayConfirmed: (PutawayRequest) -> Boolean) {
        val candidates = details.filter { it.rfid in scannedRfids }
        if (candidates.isEmpty()) {
            showNotification("请先扫描待上架物资的 RFID")
            return
        }
        putawayJob = viewModelScope.launch {
            try {
                val operator = putawayRepository.loadOperator()
                var failedCount = 0
                for (detail in candidates) {
                    val request = PutawayRequest(detail.orderNo, detail.lineNo, detail.rfid, locationCode, operator)
                    if (putawayRepository.confirmPutaway(request) && onPutawayConfirmed(request)) {
                        details = details.filterNot { it.lineNo == detail.lineNo }
                        scannedRfids = scannedRfids - detail.rfid
                        if (selectedLineNo == detail.lineNo) selectedLineNo = null
                        refreshDetails()
                    } else {
                        failedCount++
                    }
                }
                showNotification(if (failedCount == 0) "上架成功" else "${failedCount} 件上架失败，请重试")
            } catch (failure: CancellationException) {
                throw failure
            } catch (failure: Exception) {
                showNotification(failure.message ?: "上架失败，请重试")
            }
        }
    }

    private fun stopRfidScan() {
        scanJob?.cancel()
    }

    private fun showNotification(message: String) {
        _uiState.update {
            it.copy(notificationMessage = message, notificationId = it.notificationId + 1)
        }
    }

    /**
     * ToastNotification 自动消失后清除消息，并递增 id 以便下一次通知重新计时。
     */
    fun onNotificationDismiss() {
        _uiState.update {
            it.copy(
                notificationMessage = null,
                notificationId = it.notificationId + 1
            )
        }
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
     * 统一计算可见明细、扫描状态和当前抽屉明细。
     */
    private fun refreshDetails() {
        val query = _uiState.value.scanQuery.trim()
        val visible = details
            .filter { matchesQuery(it, query) }
            .map(::toUi)
        val selected = details.firstOrNull { it.lineNo == selectedLineNo }?.let(::toUi)
        _uiState.update { it.copy(visibleDetails = visible, selectedDetail = selected) }
    }

    private fun toUi(detail: PutawayDetail): PutawayDetailUi = PutawayDetailUi(
        detail = detail,
        isScanned = detail.rfid in scannedRfids
    )

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

}
