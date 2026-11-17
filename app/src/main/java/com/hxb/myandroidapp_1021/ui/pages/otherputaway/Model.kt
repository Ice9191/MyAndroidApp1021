package com.hxb.myandroidapp_1021.ui.pages.otherputaway

import com.hxb.myandroidapp_1021.domain.otherputaway.OtherPutawayTask
import com.hxb.myandroidapp_1021.domain.otherputaway.OtherPutawayTool
import com.hxb.myandroidapp_1021.domain.otherputaway.PutawayStatus as DomainPutawayStatus
import com.hxb.myandroidapp_1021.domain.otherputaway.SourceType as DomainSourceType

typealias SourceType = DomainSourceType

typealias PutawayStatus = DomainPutawayStatus

/**
 * 任务页面展示状态，与接口 DTO 分开维护。
 */
typealias OtherPutawayTaskUi = OtherPutawayTask

typealias OtherPutawayToolUi = OtherPutawayTool

data class OtherPutawayListUiState(
    val tasks: List<OtherPutawayTaskUi> = emptyList(),
    val query: String = "",
    val selectedSource: SourceType? = null,
    val selectedStatus: PutawayStatus? = null,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val fromCache: Boolean = false,
    val errorMessage: String? = null
) {
    val sourceOptions: List<String>
        get() = listOf("全部来源") + SourceType.entries.map { it.label }

    val statusOptions: List<String>
        get() = listOf("全部状态") + PutawayStatus.entries.map { it.label }

    val sourceIndex: Int
        get() = selectedSource?.let { SourceType.entries.indexOf(it) + 1 } ?: 0

    val statusIndex: Int
        get() = selectedStatus?.let { PutawayStatus.entries.indexOf(it) + 1 } ?: 0

    val visibleTasks: List<OtherPutawayTaskUi>
        get() = tasks.filter {
            it.sourceNo.contains(query.trim(), ignoreCase = true) &&
                    (selectedSource == null || it.sourceType == selectedSource) &&
                    (selectedStatus == null || it.status == selectedStatus)
        }
}

data class OtherPutawayScanUiState(
    val task: OtherPutawayTaskUi? = null,
    val rfid: String = "",
    val location: String = "",
    val readLocations: Map<String, String> = emptyMap(),
    val readRfids: Set<String> = emptySet(),
    val selectedTab: Int = 0,
    val message: String? = null,
    val notificationMessage: String? = null,
    val notificationId: Int = 0,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val fromCache: Boolean = false,
    val errorMessage: String? = null
)

data class OtherPutawayConfirmUiState(
    val showDialog: Boolean = false,
    val completionPending: Boolean = false
)
