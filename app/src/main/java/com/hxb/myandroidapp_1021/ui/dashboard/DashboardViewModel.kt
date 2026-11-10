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
import com.hxb.myandroidapp_1021.navigation.PickingList
import com.hxb.myandroidapp_1021.navigation.RepairList
import com.hxb.myandroidapp_1021.navigation.ReturnList
import com.hxb.myandroidapp_1021.navigation.ScrapList
import com.hxb.myandroidapp_1021.navigation.ScrapOutList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState(
                isLoading = false,
                userName = "张三",
                userRole = "材料员",
                avatarUrl = "https://picsum.photos/800/300",
                serverConnected = true,
                menuSections = mockMenuSections()
            )
        }
    }

    private fun mockMenuSections(): List<MenuSectionData> = listOf(
        MenuSectionData(
            title = "入库管理",
            items = listOf(
                MenuItemData("工器具上架", Icons.Filled.QrCodeScanner, InventoryList)
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

    fun onLogout() {
        // TODO: 清 token / 调接口
    }
}