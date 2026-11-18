package com.hxb.myandroidapp_1021.feature.dashboard.domain.repository

import com.hxb.myandroidapp_1021.feature.dashboard.domain.model.DashboardData

interface DashboardRepository {
    /**
     * 返回首页展示的登录人信息。
     */
    suspend fun loadDashboard(): DashboardData

    /**
     * 返回当前服务器连接状态。
     */
    suspend fun checkServerConnection(): Boolean
}
