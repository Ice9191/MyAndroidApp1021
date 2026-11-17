package com.hxb.myandroidapp_1021.pages.dashboard

import androidx.compose.ui.graphics.vector.ImageVector
import com.hxb.myandroidapp_1021.domain.dashboard.DashboardData as DomainDashboardData


/**
 * 首页人员响应字段，依据文档“人员”“权限管理”。
 */
typealias DashboardData = DomainDashboardData

/**
 * 前端首页状态，连接结果及菜单配置不是数据库字段。
 */
data class DashboardUiState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val userRole: String = "",
    val avatarUrl: String = "",
    val serverConnected: Boolean = false,
    val menuSections: List<MenuSectionData> = emptyList(),
    val fromCache: Boolean = false,
    val errorMessage: String? = null,
    val isLoggingOut: Boolean = false
) {
    val serverText: String
        get() = if (serverConnected) "服务器已连接" else "服务器未连接"
}

/**
 * 本地菜单展示配置，图标和路由不由后端返回。
 */
data class MenuItemData(
    val title: String,
    val icon: ImageVector,
    val route: Any
)

data class MenuSectionData(
    val title: String,
    val items: List<MenuItemData>
)
