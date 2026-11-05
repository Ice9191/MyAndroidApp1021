package com.hxb.myandroidapp_1021.components.index

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.hxb.myandroidapp_1021.components.index.components.OrderListPage
import com.hxb.myandroidapp_1021.components.index.models.DisplayField

// 送修申请单数据模型（列表展示 + 审批状态）
data class RepairRecord(
    val orderNo: String,
    val team: String,
    val person: String,
    val toolCount: Int,
    val applyDate: String,
    val status: String   // 已审批 / 未审批
)

/**
 * 送修申请单列表页（一级路由，从仪表盘「工具送修」进来）。
 * 和借出列表一样用 OrderListPage，只是：
 *  - 状态展示的是审批状态（已审批 / 未审批）
 *  - 右下角多一个「+」悬浮按钮，点它去 InspectionScanScreen 新建申请
 */
@Composable
fun RepairListScreen(
    onBackClick: () -> Unit = {},
    onAddApplyClick: () -> Unit = {}
) {
    // 模拟数据
    val orderList = listOf(
        RepairRecord("SX20261028001", "检修一班", "张伟", 3, "10-28 09:30", "已审批"),
        RepairRecord("SX20261028002", "维修二班", "李明", 2, "10-28 10:00", "未审批"),
        RepairRecord("SX20261028003", "电气班组", "王健", 4, "10-28 10:30", "已审批"),
        RepairRecord("SX20261028004", "运行一班", "刘洋", 1, "10-28 11:00", "未审批"),
        RepairRecord("SX20261028005", "热控班组", "陈强", 5, "10-28 11:15", "已审批"),
        RepairRecord("SX20261028006", "继保班", "赵磊", 3, "10-28 13:00", "未审批"),
        RepairRecord("SX20261028007", "综合班", "孙浩", 2, "10-28 14:00", "已审批"),
        RepairRecord("SX20261028008", "变电检修班", "周杰", 6, "10-28 14:30", "未审批"),
        RepairRecord("SX20261028009", "汽机本体班", "吴刚", 4, "10-28 15:00", "已审批"),
        RepairRecord("SX20261028010", "锅炉辅机班", "郑爽", 7, "10-28 15:30", "未审批")
    )

    OrderListPage(
        title = "送修",
        orders = orderList,
        searchPlaceholder = "请输入送修单号",
        orderNo = { it.orderNo },
        fields = { record ->
            listOf(
                DisplayField("班组", record.team),
                DisplayField("申请人", record.person),
                DisplayField("工具数量", "${record.toolCount}件"),
                DisplayField("申请日期", record.applyDate)
            )
        },
        statusText = { it.status },
        // 未审批用红色，已审批用主题蓝
        statusColor = {
            if (it.status == "未审批") Color(0xFFE53935) else Color(0xFF1E71C8)
        },
        listHeaderLabel = "送修申请单",
        searchMatcher = { record, query -> record.orderNo.contains(query, ignoreCase = true) },
        onBackClick = onBackClick,
        onAddClick = onAddApplyClick
    )
}
