package com.hxb.myandroidapp_1021.models

import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

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

@Serializable
data class ApiResponse<T>(
    val code: Int,  // 业务状态码
    val data: T? = null,  // 实际数据
    val message: String? = null   // 提示消息
)

@Serializable
data class LoginParams(
    val username: String,
    val password: String
)

@Serializable
data class User(
    val id: String = "",
    val username: String = "",
    val token: String = "",
    val nickName: String? = null,   // 可空，JSON中可能没有这个字段
    val clockingNumbers: Int? = null    // 打卡次数
)

// 分页数据包装
@Serializable
data class PageData<T>(
    val total: Int,       // 总条数
    val pageTotal: Int,   // 总页数
    val rows: List<T>     // 当前页数据列表
)

// 题目列表项
@Serializable
data class PageQuestionItem(
    val id: String,
    val stem: String,             // 题目标题
    val difficulty: Int = 0,      // 难度
    val views: Int = 0            // 浏览量
)