package com.hxb.myandroidapp_1021.domain.dashboard

import com.hxb.myandroidapp_1021.domain.common.QueryData
import com.hxb.myandroidapp_1021.domain.common.Repository

interface DashboardRepository : Repository {
    suspend fun profile(): QueryData<DashboardData>
}
