package com.hxb.myandroidapp_1021.ui.pages.otherpicking

import com.hxb.myandroidapp_1021.domain.otherpicking.OtherPickingTask
import com.hxb.myandroidapp_1021.domain.otherpicking.OtherPickingTool
import com.hxb.myandroidapp_1021.domain.otherpicking.PickingStatus as DomainPickingStatus
import com.hxb.myandroidapp_1021.domain.otherpicking.SourceType as DomainSourceType

typealias SourceType = DomainSourceType

typealias PickingStatus = DomainPickingStatus

/**
 * 任务页面展示状态，与接口 DTO 分开维护。
 */
typealias OtherPickingTaskUi = OtherPickingTask

typealias OtherPickingToolUi = OtherPickingTool

data class OtherPickingListUiState(
    val tasks: List<OtherPickingTaskUi> = emptyList(),
    val query: String = "",
    val selectedSource: SourceType? = null,
    val selectedStatus: PickingStatus? = null,
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val fromCache: Boolean = false,
    val errorMessage: String? = null
) {
    val sourceOptions: List<String>
        get() = listOf("全部来源") + SourceType.entries.map { it.label }

    val statusOptions: List<String>
        get() = listOf("全部状态") + PickingStatus.entries.map { it.label }

    val sourceIndex: Int
        get() = selectedSource?.let { SourceType.entries.indexOf(it) + 1 } ?: 0

    val statusIndex: Int
        get() = selectedStatus?.let { PickingStatus.entries.indexOf(it) + 1 } ?: 0

    val visibleTasks: List<OtherPickingTaskUi>
        get() = tasks.filter {
            it.sourceNo.contains(query.trim(), ignoreCase = true) &&
                    (selectedSource == null || it.sourceType == selectedSource) &&
                    (selectedStatus == null || it.status == selectedStatus)
        }
}

data class OtherPickingScanUiState(
    val task: OtherPickingTaskUi? = null,
    val rfid: String = "",
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

data class OtherPickingConfirmUiState(
    val showDialog: Boolean = false,
    val completionPending: Boolean = false
)
