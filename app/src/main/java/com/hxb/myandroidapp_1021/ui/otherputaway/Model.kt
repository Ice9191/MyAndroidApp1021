package com.hxb.myandroidapp_1021.ui.otherputaway

enum class SourceType(val label: String) {
    TEMPORARY("临时归还"),
    INSPECTION("送检归还"),
    REPAIR("送修归还"),
    SCRAP("报废归还"),
    RETURN("退货归还")
}

enum class PutawayStatus(val label: String) {
    PENDING("待上架"),
    PARTIALLY("部分上架"),
    COMPLETED("已上架"),
    CLOSED("已关闭")
}

/**
 * 任务页面展示状态，与接口 DTO 分开维护。
 * TODO: 字段未确定，其他任务接口字段名、主键及状态类型需后端确认后建立业务模型。
 */
data class OtherPutawayTaskUi(
    val sourceNo: String,
    val sourceType: SourceType,
    val date: String,
    val tools: List<OtherPutawayToolUi>,
    // 关闭状态独立于完成进度，仅用于页面展示，不推定接口字段。
    val closed: Boolean = false
) {
    val completedCount: Int get() = tools.count { it.completed }
    val status: PutawayStatus
        get() = if (closed) PutawayStatus.CLOSED else when (completedCount) {
            0 -> PutawayStatus.PENDING
            tools.size -> PutawayStatus.COMPLETED
            else -> PutawayStatus.PARTIALLY
        }
}

data class OtherPutawayToolUi(
    val rfid: String,
    val name: String,
    val location: String,
    val completed: Boolean = false
)

data class OtherPutawayListUiState(
    val tasks: List<OtherPutawayTaskUi> = emptyList(),
    val query: String = "",
    val selectedSource: SourceType? = null,
    val selectedStatus: PutawayStatus? = null
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
    val notificationId: Int = 0
)

data class OtherPutawayConfirmUiState(
    val showDialog: Boolean = false,
    val completionPending: Boolean = false
)
