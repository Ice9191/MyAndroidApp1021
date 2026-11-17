package com.hxb.myandroidapp_1021.domain.otherputaway

import kotlinx.serialization.Serializable

@Serializable
enum class SourceType(val label: String) {
    // 临时归还已确认直接在库，本模块仅处理送检、送修返回上架。
    INSPECTION("送检归还"),

    // 用户 2026-11-17 确认方案 A：报废、退货为终态，重新入库使用新 RFID，不作为归还上架来源。
    REPAIR("送修归还")
}

@Serializable
enum class PutawayStatus(val label: String) {
    PENDING("待上架"),
    PARTIALLY("部分上架"),
    COMPLETED("已上架"),
    CLOSED("已关闭")
}

@Serializable
data class OtherPutawayTask(
    val sourceNo: String,
    val sourceType: SourceType,
    val date: String,
    val tools: List<OtherPutawayTool>,
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

@Serializable
data class OtherPutawayTool(
    val rfid: String,
    val name: String,
    val location: String,
    val completed: Boolean = false,
    val warehouse: String,
    val canProcess: Boolean,
    val blockedReason: String = ""
)

@Serializable
data class OtherPutawayRequest(
    val sourceNo: String,
    val sourceType: SourceType,
    val items: List<PutawayBinding>
)

@Serializable
data class PutawayBinding(val rfid: String, val location: String)
