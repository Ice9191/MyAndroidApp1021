package com.hxb.myandroidapp_1021.feature.dashboard.domain.repository

import com.hxb.myandroidapp_1021.feature.dashboard.domain.model.DashboardData

interface DashboardRepository {
    suspend fun loadDashboard(): DashboardData
}
