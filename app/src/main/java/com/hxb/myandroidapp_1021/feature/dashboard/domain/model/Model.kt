package com.hxb.myandroidapp_1021.feature.dashboard.domain.model

import kotlinx.serialization.Serializable

/**
 * GET /user-info 返回的当前人员资料和首页菜单权限。
 */
@Serializable
data class DashboardData(
    val userName: String,
    val userRole: String,
    val expiresAt: String,
    val menuPermissions: List<MenuSectionPermission>
)

@Serializable
data class MenuSectionPermission(
    val title: String,
    val allowed: Boolean,
    val items: List<MenuItemPermission>
)

@Serializable
data class MenuItemPermission(
    val route: String,
    val title: String,
    val allowed: Boolean
)
