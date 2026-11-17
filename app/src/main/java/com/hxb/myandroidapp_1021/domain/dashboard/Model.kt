package com.hxb.myandroidapp_1021.domain.dashboard

import kotlinx.serialization.Serializable

@Serializable
data class DashboardData(
    val userName: String,
    val userRole: String,
    val avatarUrl: String = "",
    val permissions: Set<String>
)
