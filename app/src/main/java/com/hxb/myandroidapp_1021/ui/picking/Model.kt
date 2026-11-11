package com.hxb.myandroidapp_1021.ui.picking

import kotlinx.serialization.Serializable

/**
 * 单据业务字段：单号、状态、车间/班组、借用人、未完成数量、工单计划日期、总数量、已完成数量、工单号及创建信息。
 * 依据设计文档“拣选单 / 工单归还”和 IF-001、IF-003；统计数量沿用现有页面接口约定。
 * TODO: 字段未确定：PDA 接口地址、JSON 字段名、状态编码、时间格式和数据库列名需后端确认。
 */
@Serializable
data class PickingOrder(
    val orderNo: String,
    val status: PickingStatus,
    val workshop: String,
    val borrower: String,
    val pendingCount: Int,
    val plannedDate: String,
    val totalCount: Int,
    val pickedCount: Int,
    val workOrderNo: String = "",
    // TODO: 字段未确定，创建人和创建时间沿用现有页面，需确认 PDA 接口是否返回。
    val createdBy: String = "",
    val createdAt: String = ""
)

@Serializable
enum class PickingStatus(val label: String) {
    PICKED("已拣选"),
    UNPICKED("未拣选"),
    PARTIALLY_PICKED("部分拣选"),
    CLOSED("已关闭")
}

/**
 * 前端列表状态，不作为数据库字段或后端响应对象。
 */
data class PickingListUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val orders: List<PickingOrder> = emptyList(),
    val filteredOrders: List<PickingOrder> = emptyList(),
    val selectedStatus: PickingStatus? = null
)

/**
 * 工具明细业务字段：行号、名称、物资编码、RFID、货位、单位、读取标记和读取时间。
 * location 对应拣选推荐货位 / 归还存放货位；isRead、readDate 为本次读签的本地暂存值，不能等同于最终借出或归还成功。
 * TODO: 字段未确定：行主键类型、货位字段名、读取结果是否需要独立提交及其接口字段需后端确认。
 */
@Serializable
data class PickingToolScanItem(
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
data class PickingScanUiState(
    val isLoading: Boolean = false,
    val order: PickingOrder? = null,
    val tools: List<PickingToolScanItem> = emptyList()
) {
    val readList: List<PickingToolScanItem> get() = tools.filter { it.isRead }
    val unreadList: List<PickingToolScanItem> get() = tools.filterNot { it.isRead }
}

/**
 * 确认页元数据：文档“借用交接”中的经办人和出库时间。
 */
@Serializable
data class PickingConfirmData(val handler: String, val outboundTime: String)

/**
 * 出库请求字段：单据号、领料人工号、已核对 RFID 清单。
 * TODO: 字段未确定：提交接口字段名、人员标识使用账号或工号、RFID 与行主键的关联方式需后端确认。
 */
@Serializable
data class PickingConfirmRequest(
    val orderNo: String,
    val employeeId: String,
    val rfids: List<String>
)

/**
 * 前端确认页状态，不作为数据库字段。
 */
data class PickingConfirmUiState(
    val isLoading: Boolean = true,
    val employeeId: String = "",
    val handler: String = "",
    val outboundTime: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
)
