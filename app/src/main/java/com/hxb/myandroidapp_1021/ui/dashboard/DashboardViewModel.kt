package com.hxb.myandroidapp_1021.ui.dashboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.outlined.FactCheck
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Outbox
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hxb.myandroidapp_1021.navigation.InspectionList
import com.hxb.myandroidapp_1021.navigation.InventoryList
import com.hxb.myandroidapp_1021.navigation.OtherPickingList
import com.hxb.myandroidapp_1021.navigation.OtherPutawayList
import com.hxb.myandroidapp_1021.navigation.PickingList
import com.hxb.myandroidapp_1021.navigation.PutawayList
import com.hxb.myandroidapp_1021.navigation.RepairList
import com.hxb.myandroidapp_1021.navigation.ReturnList
import com.hxb.myandroidapp_1021.navigation.ScrapList
import com.hxb.myandroidapp_1021.navigation.ScrapOutList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class DashboardViewModel : ViewModel() {
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
                // 原入口误指向盘点；文档“上架单”要求独立的 PDA 上架作业入口。
                MenuItemData("工器具上架", Icons.Filled.QrCodeScanner, PutawayList)
            )
        ),
        MenuSectionData(
            title = "借用归还",
            items = listOf(
                MenuItemData("拣选", Icons.AutoMirrored.Filled.ExitToApp, PickingList),
                MenuItemData("归还", Icons.AutoMirrored.Filled.Login, ReturnList)
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
                MenuItemData("工具送检", Icons.AutoMirrored.Outlined.FactCheck, InspectionList),
                MenuItemData("工具送修", Icons.Outlined.Build, RepairList)
            )
        ),
        MenuSectionData(
            title = "库存核减",
            items = listOf(
                MenuItemData("工具报废", Icons.Outlined.DeleteForever, ScrapList),
                MenuItemData("报废出库", Icons.Outlined.Outbox, ScrapOutList)
            )
        ),
        MenuSectionData(
            title = "盘点管理",
            items = listOf(
                MenuItemData("盘点单", Icons.Filled.QrCodeScanner, InventoryList)
            )
        )
    )

    fun onLogout(onSuccess: () -> Unit) {
        // TODO: 字段未确定，认证协议确认后清理真实登录凭证。
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
