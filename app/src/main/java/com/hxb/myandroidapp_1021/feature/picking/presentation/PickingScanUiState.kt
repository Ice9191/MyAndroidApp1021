package com.hxb.myandroidapp_1021.feature.picking.presentation

import com.hxb.myandroidapp_1021.feature.picking.domain.model.PickingOrder
import com.hxb.myandroidapp_1021.core.ui.common.DisplayField

/**
 * 拣选作业页单件器具的展示状态：业务字段来自 [com.hxb.myandroidapp_1021.feature.picking.domain.model.PickingDetail]，
 * isRead、readDate 为本次读签的本地状态，不能等同于后端明细状态。
 */
data class PickingToolScanItem(
    val lineNo: Int,
    val description: String,
    val itemCode: String,
    val rfid: String,
    val batchNo: String,
    val warehouse: String,
    val location: String,
    val unit: String,
    val isRead: Boolean = false,
    val readDate: String = ""
)

/**
 * 前端扫描状态；单据和明细是接口数据，加载标记及筛选列表只供页面使用。
 */
data class PickingScanUiState(
    val isLoading: Boolean = false,
    val order: PickingOrder? = null,
    val tools: List<PickingToolScanItem> = emptyList(),
    val showConfirmDrawer: Boolean = false,
    val isSubmitting: Boolean = false,
    val submitSucceeded: Boolean = false,
    val submitError: String? = null
) {
    val readList: List<PickingToolScanItem> get() = tools.filter { it.isRead }
    val unreadList: List<PickingToolScanItem> get() = tools.filterNot { it.isRead }
}

/**
 * 拣选作业卡片的统一展示字段，扫描页和人工拣选页共用，避免两处内容不一致。
 */
internal fun PickingToolScanItem.displayFields(): List<DisplayField> = listOf(
    DisplayField("RFID编码", rfid),
    DisplayField("批次号", batchNo),
    DisplayField("仓库", warehouse),
    DisplayField("推荐货位", location),
    DisplayField("单位", unit)
)

/**
 * 卡片右上角状态文字，跟随本次读签状态显示为已拣选/未拣选。
 */
internal val PickingToolScanItem.scanStatusLabel: String
    get() = if (isRead) "已拣选" else "未拣选"
