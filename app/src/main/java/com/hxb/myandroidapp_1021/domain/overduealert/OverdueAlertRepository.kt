package com.hxb.myandroidapp_1021.domain.overduealert

import com.hxb.myandroidapp_1021.domain.common.QueryData
import com.hxb.myandroidapp_1021.domain.common.Repository

interface OverdueAlertRepository : Repository {
    suspend fun alerts(): QueryData<List<OverdueAlert>>
}
