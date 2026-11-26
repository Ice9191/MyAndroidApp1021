package com.hxb.myandroidapp_1021.feature.dashboard.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector
import com.hxb.myandroidapp_1021.feature.dashboard.domain.model.DashboardData
import com.hxb.myandroidapp_1021.feature.dashboard.domain.model.MenuSectionPermission
import com.hxb.myandroidapp_1021.navigation.OtherPickingList
import com.hxb.myandroidapp_1021.navigation.OtherPutawayList
import com.hxb.myandroidapp_1021.navigation.OverdueAlertList
import com.hxb.myandroidapp_1021.navigation.PickingList
import com.hxb.myandroidapp_1021.navigation.PutawayList
import com.hxb.myandroidapp_1021.navigation.ReturnList
import com.hxb.myandroidapp_1021.navigation.StockDetailList
import com.hxb.myandroidapp_1021.navigation.TempBorrow
import com.hxb.myandroidapp_1021.navigation.TempReturn

/**
 * 前端首页状态，连接结果及菜单配置不是数据库字段。
 */
data class DashboardUiState(
    val isLoading: Boolean = false,
    val user: DashboardData? = null,
    val serverConnected: Boolean = false,
    val menuSections: List<MenuSectionData> = emptyList(),
    val isLoggingOut: Boolean = false,
    val errorMessage: String? = null
) {
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

private val menuDestinations: Map<String, Pair<Any, ImageVector>> = mapOf(
    "PutawayList" to (PutawayList to Icons.Filled.QrCodeScanner),
    "PickingList" to (PickingList to Icons.AutoMirrored.Filled.ExitToApp),
    "ReturnList" to (ReturnList to Icons.AutoMirrored.Filled.Login),
    "TempBorrow" to (TempBorrow to Icons.AutoMirrored.Filled.ExitToApp),
    "TempReturn" to (TempReturn to Icons.AutoMirrored.Filled.Login),
    "OtherPickingList" to (OtherPickingList to Icons.AutoMirrored.Filled.ExitToApp),
    "OtherPutawayList" to (OtherPutawayList to Icons.Filled.QrCodeScanner),
    "StockDetailList" to (StockDetailList to Icons.AutoMirrored.Filled.ListAlt),
    "OverdueAlertList" to (OverdueAlertList to Icons.Filled.Warning)
)

/**
 * 原首页固定展示全部菜单，按系统文档“权限管理”改为同时检查模块与功能点授权。
 * 仅展示当前版本已有对应页面的入口，保留后端返回的标题和顺序。
 */
internal fun List<MenuSectionPermission>.toMenuSections(): List<MenuSectionData> =
    mapNotNull { section ->
        if (!section.allowed) return@mapNotNull null

        val items = section.items.mapNotNull item@{ item ->
            if (!item.allowed) return@item null
            val (route, icon) = menuDestinations[item.route] ?: return@item null
            MenuItemData(title = item.title, icon = icon, route = route)
        }
        if (items.isEmpty()) null else MenuSectionData(title = section.title, items = items)
    }
