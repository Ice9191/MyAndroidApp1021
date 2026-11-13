package com.hxb.myandroidapp_1021.ui.otherpicking

enum class SourceType(val label: String) {
    TEMPORARY("临时拣选"),
    INSPECTION("送检拣选"),
    REPAIR("送修拣选"),
    SCRAP("报废拣选"),
    RETURN("退货拣选")
}

enum class PickingStatus(val label: String) {
    PENDING("待拣选"),
    PARTIALLY("部分拣选"),
    COMPLETED("已拣选"),
    CLOSED("已关闭")
}

/**
 * 任务页面展示状态，与接口 DTO 分开维护。
 * TODO: 字段未确定，其他任务接口字段名、主键及状态类型需后端确认后建立业务模型。
 */
data class OtherPickingTaskUi(
    val sourceNo: String,
    val sourceType: SourceType,
    val date: String,
    val tools: List<OtherPickingToolUi>,
    // 关闭状态独立于完成进度，仅用于页面展示，不推定接口字段。
    val closed: Boolean = false
) {
    val completedCount: Int get() = tools.count { it.completed }
    val status: PickingStatus
        get() = if (closed) PickingStatus.CLOSED else when (completedCount) {
            0 -> PickingStatus.PENDING
            tools.size -> PickingStatus.COMPLETED
            else -> PickingStatus.PARTIALLY
        }
}

data class OtherPickingToolUi(
    val rfid: String,
    val name: String,
    val location: String,
    val completed: Boolean = false
)

data class OtherPickingListUiState(
    val tasks: List<OtherPickingTaskUi> = emptyList(),
    val query: String = "",
    val selectedSource: SourceType? = null,
    val selectedStatus: PickingStatus? = null
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
    val notificationId: Int = 0
)

data class OtherPickingConfirmUiState(
    val showDialog: Boolean = false,
    val completionPending: Boolean = false
)
