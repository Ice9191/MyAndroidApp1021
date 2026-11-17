package com.hxb.myandroidapp_1021.ui.pages.returns

import com.hxb.myandroidapp_1021.domain.returns.ReturnTool
import com.hxb.myandroidapp_1021.domain.returns.ReturnConfirmRequest as DomainReturnConfirmRequest
import com.hxb.myandroidapp_1021.domain.returns.ReturnOrder as DomainReturnOrder
import com.hxb.myandroidapp_1021.domain.returns.ReturnStatus as DomainReturnStatus


/**
 * 单据业务字段：单号、状态、车间/班组、归还人、未完成数量、归还时间、总数量、已完成数量、工单号及创建信息。
 * 依据设计文档“拣选单 / 工单归还”和 IF-001、IF-003；统计数量沿用现有页面接口约定。
 */
typealias ReturnOrder = DomainReturnOrder

typealias ReturnStatus = DomainReturnStatus

/**
 * 前端列表状态，不作为数据库字段或后端响应对象。
 */
data class ReturnListUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val orders: List<ReturnOrder> = emptyList(),
    val filteredOrders: List<ReturnOrder> = emptyList(),
    val selectedStatus: ReturnStatus? = null,
    val isSubmitting: Boolean = false,
    val fromCache: Boolean = false,
    val errorMessage: String? = null
)

/**
 * 工具明细业务字段：行号、名称、物资编码、RFID、货位、单位、读取标记和读取时间。
 * location 对应拣选推荐货位 / 归还存放货位；isRead、readDate 为本次读签的本地暂存值，不能等同于最终借出或归还成功。
 */
data class ReturnToolScanItem(
    val id: Int,
    val name: String,
    val itemCode: String,
    val rfid: String,
    val location: String,
    val unit: String,
    val warehouse: String = "",
    val condition: String = "",
    val isPutaway: Boolean = false,
    val putawayAt: String = "",
    val canProcess: Boolean = true,
    val blockedReason: String = "",
    val isRead: Boolean = false,
    val readDate: String = ""
) {
    // 坏件按文档不再推荐上架，需转送修或送检流程
    val isBadCondition: Boolean get() = condition == "坏件"
}

/**
 * 前端扫描状态；单据和明细是接口数据，加载标记及筛选列表只供页面使用。
 */
/**
 * 前端扫描状态；selectedLineId 与 draftLocation 只服务单条上架抽屉，不提交业务数据。
 */
data class ReturnScanUiState(
    val isLoading: Boolean = false,
    val order: ReturnOrder? = null,
    val tools: List<ReturnToolScanItem> = emptyList(),
    val selectedLineId: Int? = null,
    val showConfirmDrawer: Boolean = false,
    val draftLocation: String = "",
    val draftWarehouse: String = "",
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    val fromCache: Boolean = false,
    val errorMessage: String? = null
) {
    val readList: List<ReturnToolScanItem> get() = tools.filter { it.isRead && !it.isPutaway }
    val unreadList: List<ReturnToolScanItem> get() = tools.filter { !it.isRead && !it.isPutaway }
    val selectedTool: ReturnToolScanItem? get() = tools.firstOrNull { it.id == selectedLineId }
}

/**
 * 单条上架请求：单据号、明细行、RFID 与实际货位。
 */
typealias ReturnConfirmRequest = DomainReturnConfirmRequest

fun ReturnTool.toScanItem() = ReturnToolScanItem(
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
    isPutaway = completed,
    putawayAt = completedAt
)
