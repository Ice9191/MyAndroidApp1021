package com.hxb.myandroidapp_1021.models

import androidx.compose.ui.graphics.vector.ImageVector

// 每个 Tab 需要的信息
data class TabItem(
    val name: String,                 // Tab名称，如"首页"
    val selectedIcon: ImageVector,    // 选中时图标
    val unselectedIcon: ImageVector,  // 未选中时图标
    val route: String                 // 路由名称，如"home"
)
