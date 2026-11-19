package com.hxb.myandroidapp_1021.feature.returns.presentation

import com.hxb.myandroidapp_1021.feature.returns.domain.model.ReturnDetail
import com.hxb.myandroidapp_1021.feature.returns.domain.model.ReturnDetailStatus
import com.hxb.myandroidapp_1021.feature.returns.domain.model.ReturnOrder
import com.hxb.myandroidapp_1021.commonmodel.DisplayField

/**
 * 归还上架扫描页状态；tools 包含当前单据未上架的明细（待上架、损坏、待送检），
 * readList 和 unreadList 供 Tab 过滤；损坏、待送检只能查看，不能上架。
 * 待上架明细的实际货位由系统推荐（初始非空），确认时允许人工调整。
 * TODO: 字段未确定，推荐货位接口确认后再改为服务端下发，当前由模拟数据提供。
 */
data class ReturnScanUiState(
    val isLoading: Boolean = false,
    val order: ReturnOrder? = null,
    val tools: List<ReturnToolScanItem> = emptyList(),
    val selectedLineId: Int? = null,
    val showConfirmDrawer: Boolean = false,
    val draftLocation: String = "",
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    val notificationMessage: String? = null,
    val notificationId: Int = 0
) {
    val readList: List<ReturnToolScanItem> get() = tools.filter { it.isRead }
    val unreadList: List<ReturnToolScanItem> get() = tools.filterNot { it.isRead }
    val selectedTool: ReturnToolScanItem? get() = tools.firstOrNull { it.lineNo == selectedLineId }
}

/**
 * 单条明细的扫描视图；isRead 表示本次核对，canPutaway 由明细状态判断，两者不能互相替代。
 * TODO: 字段未确定，接口 JSON 名称需与后端确认。
 */
data class ReturnToolScanItem(
    val lineNo: Int,
    val description: String,
    val itemCode: String,
    val rfid: String,
    val unit: String,
    val actualLocation: String = "",
    val status: ReturnDetailStatus = ReturnDetailStatus.PENDING,
    val isRead: Boolean = false,
    val readDate: String = ""
) {
    val canPutaway: Boolean get() = status == ReturnDetailStatus.PENDING
}

/**
 * 扫描卡片字段与上架单明细 ER 图一致；物资描述已作为标题，不重复展示。
 */
internal fun ReturnToolScanItem.displayFields(): List<DisplayField> = listOf(
    DisplayField("RFID编码", rfid),
    DisplayField("物资编码", itemCode),
    DisplayField("单位", unit),
    DisplayField("实际货位", actualLocation.ifBlank { "-" })
)

/**
 * 明细转扫描视图；已上架明细不进入扫描列表，损坏、待送检明细保留展示但不能上架。
 */
internal fun ReturnDetail.toScanItem(): ReturnToolScanItem = ReturnToolScanItem(
    lineNo = lineNo,
    description = description,
    itemCode = itemCode,
    rfid = rfid,
    unit = unit,
    actualLocation = actualLocation,
    status = status
)
