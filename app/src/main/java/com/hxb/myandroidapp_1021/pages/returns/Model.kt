package com.hxb.myandroidapp_1021.pages.returns

import kotlinx.serialization.Serializable

/**
 * 单据业务字段：单号、状态、车间/班组、归还人、未完成数量、归还时间、总数量、已完成数量、工单号及创建信息。
 * 依据设计文档“拣选单 / 工单归还”和 IF-001、IF-003；统计数量沿用现有页面接口约定。
 * TODO: 字段未确定：PDA 接口地址、JSON 字段名、状态编码、时间格式和数据库列名需后端确认。
 */
@Serializable
data class ReturnOrder(
    val orderNo: String,
    val status: ReturnStatus,
    val workshop: String,
    val returner: String,
    val pendingCount: Int,
    val returnedDate: String,
    val totalCount: Int,
    val returnedCount: Int,
    val workOrderNo: String = "",
    // TODO: 字段未确定，创建人和创建时间沿用现有页面，需确认 PDA 接口是否返回。
    val createdBy: String = "",
    val createdAt: String = ""
)

@Serializable
enum class ReturnStatus(val label: String) {
    RETURNED("已上架"),
    UNRETURNED("待上架"),
    PARTIALLY_RETURNED("部分上架")
}

/**
 * 前端列表状态，不作为数据库字段或后端响应对象。
 */
data class ReturnListUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val orders: List<ReturnOrder> = emptyList(),
    val filteredOrders: List<ReturnOrder> = emptyList(),
    val selectedStatus: ReturnStatus? = null
)

/**
 * 工具明细业务字段：行号、名称、物资编码、RFID、货位、单位、读取标记和读取时间。
 * location 对应拣选推荐货位 / 归还存放货位；isRead、readDate 为本次读签的本地暂存值，不能等同于最终借出或归还成功。
 * TODO: 字段未确定：行主键类型、货位字段名、读取结果是否需要独立提交及其接口字段需后端确认。
 */
@Serializable
data class ReturnToolScanItem(
    // TODO: 字段未确定，id 暂作明细行号，后端主键类型需确认。
    val id: Int,
    val name: String,
    val itemCode: String,
    val rfid: String,
    val location: String,
    val unit: String,
    // TODO: 字段未确定：推荐仓库、好坏件状态、本件上架状态及上架时间的接口字段待后端确认。
    val warehouse: String = "",
    val condition: String = "",
    val isPutaway: Boolean = false,
    val putawayAt: String = "",
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
    val warehouseOptions: List<String> = emptyList(),
    val locationOptions: List<String> = emptyList(),
    val isSubmitting: Boolean = false,
    val submitError: String? = null
) {
    val readList: List<ReturnToolScanItem> get() = tools.filter { it.isRead }
    val unreadList: List<ReturnToolScanItem> get() = tools.filterNot { it.isRead }
    val selectedTool: ReturnToolScanItem? get() = tools.firstOrNull { it.id == selectedLineId }
}

/**
 * 单条上架请求：单据号、明细行、RFID 与实际货位。
 * TODO: 字段未确定：提交接口字段名、行标识类型、上架人及货位校验接口需后端确认；依据文档“归还上架”。
 */
@Serializable
data class ReturnConfirmRequest(
    val orderNo: String,
    val lineNo: Int,
    val rfid: String,
    val locationCode: String
)
