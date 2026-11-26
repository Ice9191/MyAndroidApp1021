package com.hxb.myandroidapp_1021.feature.otherpicking.presentation

import com.hxb.myandroidapp_1021.feature.otherpicking.domain.model.OtherPickingOrder
import com.hxb.myandroidapp_1021.core.ui.common.DisplayField

/**
 * 单件器具的扫描展示状态；isRead 与 readDate 是本次人工拣选的本机状态，不回写业务模型。
 */
data class OtherPickingToolScanItem(
    val lineNo: Int,
    val rfid: String,
    val itemCode: String,
    val description: String,
    val unit: String,
    val batchNo: String,
    val warehouse: String,
    val location: String,
    val isRead: Boolean = false,
    val readDate: String = ""
)

/**
 * 其他拣选扫描页状态。
 */
data class OtherPickingScanUiState(
    val order: OtherPickingOrder? = null,
    val tools: List<OtherPickingToolScanItem> = emptyList(),
    val showConfirmDrawer: Boolean = false,
    val isSubmitting: Boolean = false,
    val submitSucceeded: Boolean = false,
    val submitError: String? = null
) {
    val readList: List<OtherPickingToolScanItem>
        get() = tools.filter { it.isRead }

    val unreadList: List<OtherPickingToolScanItem>
        get() = tools.filterNot { it.isRead }
}

/**
 * 扫描卡片首行：行号按两位展示，与拣选状态同排。
 */
internal val OtherPickingToolScanItem.lineLabel: String
    get() = "行号 ${lineNo.toString().padStart(2, '0')}"

/**
 * 扫描卡片标题：RFID 编码原样展示，不加前缀。
 */
internal val OtherPickingToolScanItem.cardTitle: String
    get() = rfid

/**
 * 明细字段按需求顺序展示，仓库与货位合并为一行。
 */
internal fun OtherPickingToolScanItem.displayFields(): List<DisplayField> = listOf(
    DisplayField("物资编码", itemCode),
    DisplayField("物资描述", description),
    DisplayField("单位", unit),
    DisplayField("批次号", batchNo),
    DisplayField("仓库货位", "$warehouse / $location")
)

/**
 * 明细状态文案。
 */
internal val OtherPickingToolScanItem.scanStatusLabel: String
    get() = if (isRead) "已拣选" else "未拣选"
