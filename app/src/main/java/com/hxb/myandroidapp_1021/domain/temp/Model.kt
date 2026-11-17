package com.hxb.myandroidapp_1021.domain.temp

import kotlinx.serialization.Serializable

@Serializable
data class TempTool(
    val rfid: String,
    val itemCode: String,
    val name: String,
    val unit: String,
    val location: String
)

@Serializable
data class TempBorrowLine(
    val lineNo: Int,
    val tool: TempTool,
    val borrowed: Boolean = false,
    val borrowedAt: String = "",
    val picked: Boolean = false
)

@Serializable
data class TempBorrowOrder(
    val orderNo: String,
    val workOrderNo: String,
    val borrower: String,
    val workshop: String,
    val team: String,
    val remark: String,
    val createdBy: String,
    val createdAt: String,
    val lines: List<TempBorrowLine>
) {
    // 依据单据状态图使用“草稿”，不沿用正文中与图不一致的“待确认”单据初态。
    val status: String get() = if (lines.isNotEmpty() && lines.all { it.borrowed }) "已借出" else "草稿"
}

@Serializable
data class TempReturnLine(
    val lineNo: Int,
    val tool: TempTool,
    val borrowOrderNo: String,
    val borrower: String,
    val borrowedAt: String,
    val returned: Boolean = false,
    val returnedAt: String = "",
    val location: String = ""
)

@Serializable
data class TempReturnOrder(
    val orderNo: String,
    val workOrderNo: String,
    val returner: String,
    val location: String,
    val remark: String,
    val createdBy: String,
    val createdAt: String,
    val lines: List<TempReturnLine>,
    val returnerEmployeeNo: String = ""
) {
    val status: String get() = if (lines.isNotEmpty() && lines.all { it.returned }) "已归还" else "待确认"
}

@Serializable
data class TempBorrowRequest(
    val requestId: String,
    val borrowerEmployeeNo: String,
    val workOrderNo: String,
    val workshop: String,
    val team: String,
    val remark: String,
    val rfids: List<String>
)

@Serializable
data class TempBorrowConfirmRequest(val orderNo: String, val lineNo: Int, val rfid: String)

@Serializable
data class TempReturnConfirmRequest(
    val orderNo: String,
    val lineNo: Int,
    val rfid: String,
    val borrowOrderNo: String,
    val returnerEmployeeNo: String,
    val workOrderNo: String,
    val location: String,
    val remark: String
)
