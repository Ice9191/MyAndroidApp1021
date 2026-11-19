package com.hxb.myandroidapp_1021.feature.overduealert.domain.repository

import com.hxb.myandroidapp_1021.feature.overduealert.domain.model.OverdueAlert

/**
 * 超期告警数据来源。
 * 告警由系统定时任务按未还清单的超期明细生成，PDA 只查询展示，不提供生成、催还或闭环操作。
 */
interface OverdueAlertRepository {
    /**
     * 查询当前超期告警。
     * TODO: 字段未确定：查询条件、分页及接口字段名需后端确认；当前返回全部告警。
     */
    suspend fun loadAlerts(): List<OverdueAlert>
}
