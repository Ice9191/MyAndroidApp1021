package com.hxb.myandroidapp_1021.feature.putaway.domain.model

/**
 * 上架单状态；当前按页面展示的“待上架/部分上架/已上架/异常”建模。
 * TODO: 字段未确定：后端状态编码未提供，正式枚举值及映射需与后端确认。
 */
enum class PutawayOrderStatus(val label: String) {
    PENDING("待上架"),
    PARTIALLY("部分上架"),
    COMPLETED("已上架"),
    ABNORMAL("异常")
}

/**
 * 上架明细状态；当前按页面展示的“待上架/在库”建模，单件不存在部分完成状态。
 * TODO: 字段未确定：后端状态编码未提供，正式枚举值及映射需与后端确认。
 */
enum class PutawayDetailStatus(val label: String) {
    PENDING("待上架"),
    COMPLETED("在库")
}

/**
 * 文档“上架单功能表间关系”：入库单号、上架单号、单据状态、上架人、上架时间。
 * details 是接口聚合的上架明细，不是上架单表的物理列。
 * TODO: 字段未确定，PDA 接口的 JSON 名称和状态代码需与后端确认。
 */
data class PutawayOrder(
    val inboundOrderNo: String,
    val orderNo: String,
    val status: PutawayOrderStatus,
    val operator: String,
    val putawayTime: String,
    val details: List<PutawayDetail>
) {
    val completedCount: Int get() = details.count { it.isCompleted }
}

/**
 * 明细字段对应文档关系图；实际货位和库存状态属于库存数据，不在上架明细模型中。
 * TODO: 字段未确定，行号类型及明细状态编码需后端确认。
 */
data class PutawayDetail(
    val orderNo: String,
    val inboundLineNo: String,
    val lineNo: String,
    val status: PutawayDetailStatus,
    val batchNo: String,
    val rfid: String,
    val itemCode: String,
    val description: String,
    val unit: String,
    val warehouse: String
) {
    val isCompleted: Boolean get() = status == PutawayDetailStatus.COMPLETED
}

/**
 * 单件上架请求；操作人暂使用模拟登录人员。
 * TODO: 字段未确定，实际接口可能需要人员、仓库和货位主键，需后端确认。
 */
data class PutawayRequest(
    val orderNo: String,
    val lineNo: String,
    val rfid: String,
    val location: String,
    val operator: String
)

/**
 * 货位校验响应，仓库/货位业务含义来自文档基础档案。
 * TODO: 字段未确定，货位查询接口和主键待确认。
 */
data class PutawayLocation(val code: String, val warehouse: String)
