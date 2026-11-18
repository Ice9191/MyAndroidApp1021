package com.hxb.myandroidapp_1021.feature.dashboard.data.repository

import com.hxb.myandroidapp_1021.feature.dashboard.domain.model.DashboardData
import com.hxb.myandroidapp_1021.feature.dashboard.domain.repository.DashboardRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class MockDashboardRepository @Inject constructor() : DashboardRepository {
    private val mockDashboard = DashboardData(
        userName = "张三",
        userRole = "材料员",
        avatarUrl = "https://picsum.photos/800/300"
    )
    private val mockServerConnected = true

    /**
     * 模拟首页加载耗时，500 毫秒后返回演示人员信息。
     */
    override suspend fun loadDashboard(): DashboardData {
        delay(500.milliseconds)
        return mockDashboard
    }

    override suspend fun checkServerConnection(): Boolean = mockServerConnected
}
