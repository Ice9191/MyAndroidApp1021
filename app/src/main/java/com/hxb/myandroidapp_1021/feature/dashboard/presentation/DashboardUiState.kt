package com.hxb.myandroidapp_1021.feature.dashboard.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector
import com.hxb.myandroidapp_1021.feature.dashboard.domain.model.DashboardData
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
    val menuSections: List<MenuSectionData> = emptyList()
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

/**
 * 首页按业务分组的菜单配置，全部为本地展示数据。
 */
val dashboardMenuSections: List<MenuSectionData> = listOf(
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
