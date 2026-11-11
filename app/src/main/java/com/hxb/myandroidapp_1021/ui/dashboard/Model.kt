package com.hxb.myandroidapp_1021.ui.dashboard

import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

/**
 * 首页人员响应字段，依据文档“人员”“权限管理”。
 * TODO: 字段未确定：PDA 首页接口及 JSON 字段名；avatarUrl 文档未提供，暂沿用页面字段。
 */
@Serializable
data class DashboardData(val userName: String, val userRole: String, val avatarUrl: String)

/** 前端首页状态，连接结果及菜单配置不是数据库字段。 */
data class DashboardUiState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val userRole: String = "",
    val avatarUrl: String = "",
    val serverConnected: Boolean = false,
    val menuSections: List<MenuSectionData> = emptyList()
) {
    val serverText: String
        get() = if (serverConnected) "服务器已连接" else "服务器未连接"
}

/** 本地菜单展示配置，图标和路由不由后端返回。 */
data class MenuItemData(
    val title: String,
    val icon: ImageVector,
    val route: Any
)

data class MenuSectionData(
    val title: String,
    val items: List<MenuItemData>
)
