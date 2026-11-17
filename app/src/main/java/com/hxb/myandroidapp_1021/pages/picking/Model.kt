package com.hxb.myandroidapp_1021.pages.picking

import com.hxb.myandroidapp_1021.domain.picking.PickingTool
import com.hxb.myandroidapp_1021.domain.picking.PickingConfirmRequest as DomainPickingConfirmRequest
import com.hxb.myandroidapp_1021.domain.picking.PickingOrder as DomainPickingOrder
import com.hxb.myandroidapp_1021.domain.picking.PickingStatus as DomainPickingStatus


/**
 * 单据业务字段：单号、状态、车间/班组、借用人、未完成数量、工单计划日期、总数量、已完成数量、工单号及创建信息。
 * 依据设计文档“拣选单 / 工单归还”和 IF-001、IF-003；统计数量沿用现有页面接口约定。
 */
typealias PickingOrder = DomainPickingOrder

typealias PickingStatus = DomainPickingStatus

/**
 * 前端列表筛选条件；工班沿用单据现有车间/工班字段。
 */
data class PickingListFilter(
    val status: PickingStatus? = null,
    val workshop: String? = null,
    val startDate: String = "",
    val endDate: String = ""
)

/**
 * 前端列表状态，不作为数据库字段或后端响应对象；filter 为已应用条件，draftFilter 为抽屉草稿。
 */
data class PickingListUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val orders: List<PickingOrder> = emptyList(),
    val filteredOrders: List<PickingOrder> = emptyList(),
    val filter: PickingListFilter = PickingListFilter(),
    val draftFilter: PickingListFilter = PickingListFilter(),
    val showFilters: Boolean = false,
    val filterError: String? = null,
    val selectedOrderNo: String? = null,
    val closingOrderNo: String? = null,
    val isSubmitting: Boolean = false,
    val fromCache: Boolean = false,
    val errorMessage: String? = null
)

/**
 * 工具明细业务字段：行号、名称、物资编码、RFID、货位、单位、读取标记和读取时间。
 * location 对应拣选推荐货位 / 归还存放货位；isRead、readDate 为本次读签的本地暂存值，不能等同于最终借出或归还成功。
 */
data class PickingToolScanItem(
    val id: Int,
    val name: String,
    val itemCode: String,
    val rfid: String,
    val location: String,
    val unit: String,
    val warehouse: String = "",
    val condition: String = "",
    val completed: Boolean = false,
    val completedAt: String = "",
    val canProcess: Boolean = true,
    val blockedReason: String = "",
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
    val submitError: String? = null,
    val fromCache: Boolean = false,
    val errorMessage: String? = null
) {
    val readList: List<PickingToolScanItem> get() = tools.filter { it.isRead && !it.completed }
    val unreadList: List<PickingToolScanItem> get() = tools.filter { !it.isRead && !it.completed }
}

/**
 * 本批次拣选提交字段：单据号与已核对 RFID 清单。
 */
typealias PickingConfirmRequest = DomainPickingConfirmRequest

fun PickingTool.toScanItem() = PickingToolScanItem(
    id = id,
    name = name,
    itemCode = itemCode,
    rfid = rfid,
    location = location,
    unit = unit,
    warehouse = warehouse,
    condition = condition,
    canProcess = canProcess,
    blockedReason = blockedReason,
    completed = completed,
    completedAt = completedAt
)
