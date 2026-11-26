package com.hxb.myandroidapp_1021.feature.otherputaway.presentation

import com.hxb.myandroidapp_1021.feature.otherputaway.domain.model.OtherPutawayDetail
import com.hxb.myandroidapp_1021.feature.otherputaway.domain.model.OtherPutawayOrder
import com.hxb.myandroidapp_1021.feature.otherputaway.domain.model.PutawayDetailStatus
import com.hxb.myandroidapp_1021.core.ui.common.DisplayField

/**
 * 其他上架扫描明细；只保留待上架明细，已上架明细不进入扫描页。
 */
data class OtherPutawayToolScanItem(
    val lineNo: Int,
    val rfid: String,
    val itemCode: String,
    val description: String,
    val unit: String,
    val location: String,
    val isRead: Boolean = false
)

/**
 * 其他上架扫描页状态；selectedLineNo 与 draftLocation 只用于单条上架抽屉。
 */
data class OtherPutawayScanUiState(
    val order: OtherPutawayOrder? = null,
    val tools: List<OtherPutawayToolScanItem> = emptyList(),
    val selectedLineNo: Int? = null,
    val showConfirmDrawer: Boolean = false,
    val draftLocation: String = "",
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    val notificationMessage: String? = null,
    val notificationId: Int = 0
) {
    val readList: List<OtherPutawayToolScanItem>
        get() = tools.filter { it.isRead }

    val unreadList: List<OtherPutawayToolScanItem>
        get() = tools.filterNot { it.isRead }

    val selectedTool: OtherPutawayToolScanItem?
        get() = tools.firstOrNull { it.lineNo == selectedLineNo }
}

/** 卡片首行的小字标签：行号。 */
internal val OtherPutawayToolScanItem.lineLabel: String
    get() = "行号 ${lineNo.toString().padStart(2, '0')}"

/** 卡片第二行的标题：RFID 编码。 */
internal val OtherPutawayToolScanItem.cardTitle: String
    get() = rfid

/** 明细状态：扫描页只展示待上架明细，状态固定为待上架。 */
internal val OtherPutawayToolScanItem.statusLabel: String
    get() = PutawayDetailStatus.PENDING.label

/** 明细字段行：物资编码、物资描述、单位与货位。 */
internal fun OtherPutawayToolScanItem.displayFields(): List<DisplayField> = listOf(
    DisplayField("物资编码", itemCode),
    DisplayField("物资描述", description),
    DisplayField("单位", unit),
    DisplayField("货位", location.ifBlank { "-" })
)

/**
 * 待上架明细转换为扫描项；已上架明细不参与扫描。
 */
internal fun OtherPutawayDetail.toScanItem(): OtherPutawayToolScanItem = OtherPutawayToolScanItem(
    lineNo = lineNo,
    rfid = rfid,
    itemCode = itemCode,
    description = description,
    unit = unit,
    location = location
)
