package com.hxb.myandroidapp_1021.ui.dashboard

import androidx.compose.ui.graphics.vector.ImageVector

data class DashboardUiState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val userRole: String = "",
    val avatarUrl: String = "",
    val serverConnected: Boolean = false,
    val menuSections: List<MenuSectionData> = emptyList()
){
    val serverText: String
        get() = if (serverConnected) "服务器已连接" else "服务器未连接"
}

data class MenuItemData(
    val title: String,
    val icon: ImageVector,
    val route: Any
)

data class MenuSectionData(
    val title: String,
    val items: List<MenuItemData>
)
