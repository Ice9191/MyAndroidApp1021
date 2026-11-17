package com.hxb.myandroidapp_1021.pages.dashboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.feature.login.domain.repository.LoginRepository
import com.hxb.myandroidapp_1021.navigation.OtherPickingList
import com.hxb.myandroidapp_1021.navigation.OtherPutawayList
import com.hxb.myandroidapp_1021.navigation.OverdueAlertList
import com.hxb.myandroidapp_1021.navigation.PickingList
import com.hxb.myandroidapp_1021.navigation.PutawayList
import com.hxb.myandroidapp_1021.navigation.ReturnList
import com.hxb.myandroidapp_1021.navigation.TempBorrow
import com.hxb.myandroidapp_1021.navigation.TempReturn
import com.hxb.myandroidapp_1021.navigation.StockDetailList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            delay(500.milliseconds)
            val mockDashboard = DashboardData(
                userName = "张三",
                userRole = "材料员",
                avatarUrl = "https://picsum.photos/800/300"
            )
            val mockMenuSections = createMenuSections()
            val mockServerConnected = true
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    userName = mockDashboard.userName,
                    userRole = mockDashboard.userRole,
                    avatarUrl = mockDashboard.avatarUrl,
                    serverConnected = mockServerConnected,
                    menuSections = mockMenuSections
                )
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
        loginRepository.logout()
        _uiState.update {
            it.copy(
                userName = "",
                userRole = "",
                avatarUrl = "",
                menuSections = emptyList()
            )
        }
        onSuccess()
    }
}
