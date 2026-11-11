package com.hxb.myandroidapp_1021.ui.returns

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
    RETURNED("已归还"),
    UNRETURNED("待归还"),
    PARTIALLY_RETURNED("部分归还")
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
    val isRead: Boolean = false,
    val readDate: String = ""
)

/**
 * 前端扫描状态；单据和明细是接口数据，加载标记及筛选列表只供页面使用。
 */
data class ReturnScanUiState(
    val isLoading: Boolean = false,
    val order: ReturnOrder? = null,
    val tools: List<ReturnToolScanItem> = emptyList()
) {
    val readList: List<ReturnToolScanItem> get() = tools.filter { it.isRead }
    val unreadList: List<ReturnToolScanItem> get() = tools.filterNot { it.isRead }
}

/**
 * 前端确认页状态，不直接作为后端数据模型。
 */
data class ReturnConfirmUiState(
    val employeeId: String = "",
    val locationCode: String = "",
    val boundLocation: String = "",
    val isSubmitting: Boolean = false
) {
    val canConfirm: Boolean
        get() = employeeId.isNotBlank() && boundLocation.isNotBlank() && !isSubmitting
}

/**
 * 归还上架请求：单据号、归还人工号、绑定货位码和已核对 RFID 清单。
 * TODO: 字段未确定：提交接口字段名及货位绑定校验接口需后端确认；依据文档“归还上架”。
 */
@Serializable
data class ReturnConfirmRequest(
    val orderNo: String,
    val employeeId: String,
    val locationCode: String,
    val rfids: List<String>
)
