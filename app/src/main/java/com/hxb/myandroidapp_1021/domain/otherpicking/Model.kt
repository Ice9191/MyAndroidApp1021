package com.hxb.myandroidapp_1021.domain.otherpicking

import kotlinx.serialization.Serializable

@Serializable
enum class SourceType(val label: String) {
    TEMPORARY("临时拣选"),
    INSPECTION("送检拣选"),
    REPAIR("送修拣选"),
    SCRAP("报废拣选"),
    RETURN("退货拣选")
}

@Serializable
enum class PickingStatus(val label: String) {
    PENDING("待拣选"),
    PARTIALLY("部分拣选"),
    COMPLETED("已拣选"),
    CLOSED("已关闭")
}

@Serializable
data class OtherPickingTask(
    val sourceNo: String,
    val sourceType: SourceType,
    val date: String,
    val tools: List<OtherPickingTool>,
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

@Serializable
data class OtherPickingTool(
    val rfid: String,
    val name: String,
    val location: String,
    val completed: Boolean = false,
    val warehouse: String,
    val canProcess: Boolean,
    val blockedReason: String = ""
)

@Serializable
data class OtherPickingRequest(
    val sourceNo: String,
    val sourceType: SourceType,
    val rfids: List<String>
)
