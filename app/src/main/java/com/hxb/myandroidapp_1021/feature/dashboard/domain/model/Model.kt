package com.hxb.myandroidapp_1021.feature.dashboard.domain.model

/**
 * 首页人员响应字段，依据文档“人员”“权限管理”。
 * TODO: 字段未确定：PDA 首页接口及 JSON 字段名；avatarUrl 文档未提供，暂沿用页面字段。
 */
data class DashboardData(val userName: String, val userRole: String, val avatarUrl: String)
