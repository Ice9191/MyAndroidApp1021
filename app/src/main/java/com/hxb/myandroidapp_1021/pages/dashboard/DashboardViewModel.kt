package com.hxb.myandroidapp_1021.pages.dashboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Warning
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.domain.dashboard.DashboardRepository
import com.hxb.myandroidapp_1021.domain.session.SessionRepository
import com.hxb.myandroidapp_1021.navigation.OtherPickingList
import com.hxb.myandroidapp_1021.navigation.OtherPutawayList
import com.hxb.myandroidapp_1021.navigation.OverdueAlertList
import com.hxb.myandroidapp_1021.navigation.PickingList
import com.hxb.myandroidapp_1021.navigation.PutawayList
import com.hxb.myandroidapp_1021.navigation.ReturnList
import com.hxb.myandroidapp_1021.navigation.StockDetailList
import com.hxb.myandroidapp_1021.navigation.TempBorrow
import com.hxb.myandroidapp_1021.navigation.TempReturn
import com.hxb.myandroidapp_1021.utils.operationResult
import com.hxb.myandroidapp_1021.utils.userMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DashboardRepository,
    private val session: SessionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        viewModelScope.launch { repository.revision.collect { refresh() } }
    }

    fun refresh() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            operationResult { repository.profile() }
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            userName = result.data.userName,
                            userRole = result.data.userRole,
                            avatarUrl = result.data.avatarUrl,
                            serverConnected = !result.fromCache,
                            fromCache = result.fromCache,
                            menuSections = createMenuSections().map { section ->
                                section.copy(items = section.items.filter { item ->
                                    val permission = when (item.route) {
                                        PutawayList -> "putaway.read"
                                        PickingList -> "picking.read"
                                        ReturnList -> "returns.read"
                                        TempBorrow -> "tempBorrow.read"
                                        TempReturn -> "tempReturn.read"
                                        OtherPickingList -> "otherPicking.read"
                                        OtherPutawayList -> "otherPutaway.read"
                                        StockDetailList -> "inventory.read"
                                        OverdueAlertList -> "overdueAlerts.read"
                                        else -> ""
                                    }
                                    permission in result.data.permissions
                                })
                            }.filter { it.items.isNotEmpty() }
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            serverConnected = false,
                            errorMessage = error.userMessage()
                        )
                    }
                }
        }
    }

    private fun createMenuSections(): List<MenuSectionData> = listOf(
        MenuSectionData(
            title = "入库管理",
            items = listOf(
                MenuItemData("工器具上架", Icons.Filled.QrCodeScanner, PutawayList)
            )
        ),
        MenuSectionData(
            title = "工单作业",
            items = listOf(
                MenuItemData("工单拣选", Icons.AutoMirrored.Filled.ExitToApp, PickingList),
                MenuItemData("工单归还上架", Icons.AutoMirrored.Filled.Login, ReturnList)
            )
        ),
        MenuSectionData(
            title = "临时借还",
            items = listOf(
                MenuItemData("临时借出", Icons.AutoMirrored.Filled.ExitToApp, TempBorrow),
                MenuItemData("临时归还", Icons.AutoMirrored.Filled.Login, TempReturn)
            )
        ),
        MenuSectionData(
            title = "其他作业",
            items = listOf(
                MenuItemData("其他拣选", Icons.AutoMirrored.Filled.ExitToApp, OtherPickingList),
                MenuItemData("其他上架", Icons.Filled.QrCodeScanner, OtherPutawayList)
            )
        ),
        MenuSectionData(
            title = "库存管理",
            items = listOf(
                MenuItemData("库存明细", Icons.AutoMirrored.Filled.ListAlt, StockDetailList)
            )
        ),
        MenuSectionData(
            title = "告警记录",
            items = listOf(
                MenuItemData("超期告警", Icons.Filled.Warning, OverdueAlertList)
            )
        )
    )

    fun onLogout(onSuccess: () -> Unit) {
        if (_uiState.value.isLoggingOut) return
        _uiState.update { it.copy(isLoggingOut = true) }
        viewModelScope.launch {
            operationResult { session.logout() }
                .onSuccess { onSuccess() }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoggingOut = false,
                            errorMessage = error.userMessage()
                        )
                    }
                }
        }
    }
}
