package com.hxb.myandroidapp_1021.models

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * 开发过程中，把所有的数据模型都放在这里是没问题的
 */

// 每个 Tab 需要的信息
data class TabItem(
    val name: String,                 // Tab名称，如"首页"
    val selectedIcon: ImageVector,    // 选中时图标
    val unselectedIcon: ImageVector,  // 未选中时图标
    val route: String                 // 路由名称，如"home"
)

data class QuestionItem(
    val id: String,
    val stem: String,
    val difficulty: Int,
    val views: Int
)

data class TodoItem(
    val id: Int,
    val title: String,
    val isDone: Boolean = false // 默认未完成
)