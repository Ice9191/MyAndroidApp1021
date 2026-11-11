package com.hxb.myandroidapp_1021.ui.otherpicking

/**
 * 页面草稿展示数据，不是接口 DTO。
 * TODO: 字段未确定，其他任务接口字段名、主键及状态类型需后端确认后建立业务模型。
 */
data class OtherPickingTaskUi(
    val sourceNo: String,
    val sourceType: String,
    val date: String,
    val tools: List<OtherPickingToolUi>
) {
    val completedCount: Int get() = tools.count { it.completed }
    val status: String get() = when (completedCount) {
        0 -> "待拣选"
        tools.size -> "已拣选"
        else -> "部分拣选"
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
    val sourceIndex: Int = 0,
    val statusIndex: Int = 0
) {
    val sourceOptions = listOf("全部来源", "临时", "送检", "送修", "报废", "退货")
    val statusOptions = listOf("全部状态", "待拣选", "部分拣选", "已拣选")
    val visibleTasks: List<OtherPickingTaskUi> get() = tasks.filter {
        it.sourceNo.contains(query.trim(), ignoreCase = true) &&
            (sourceIndex == 0 || it.sourceType == sourceOptions[sourceIndex]) &&
            (statusIndex == 0 || it.status == statusOptions[statusIndex])
    }
}

data class OtherPickingScanUiState(
    val task: OtherPickingTaskUi? = null,
    val rfid: String = "",
    val readRfids: Set<String> = emptySet(),
    val selectedTab: Int = 0,
    val message: String? = null
)

data class OtherPickingConfirmUiState(val showDialog: Boolean = false)
