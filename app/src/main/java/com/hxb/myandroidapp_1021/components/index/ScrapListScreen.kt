package com.hxb.myandroidapp_1021.components.index

import androidx.compose.runtime.Composable
import com.hxb.myandroidapp_1021.components.index.components.OrderListPage
import com.hxb.myandroidapp_1021.components.index.models.DisplayField

// 报废单数据模型
data class ScrapOrder(
    val orderNo: String,
    val applicant: String,
    val warehouse: String,
    val toolCount: Int,
    val isApproved: Boolean = true,
    val outboundStatus: String = "待出库"
)

@Composable
fun ScrapListScreen(
    onBackClick: () -> Unit = {},
    onCheckClick: (ScrapOrder) -> Unit = {},
    orders: List<ScrapOrder> = listOf(
        ScrapOrder("BF20261027003", "李明", "中心工具库", 2),
        ScrapOrder("BF20261028001", "张伟", "中心工具库", 3),
        ScrapOrder("BF20261029002", "王健", "电气工具库", 4),
        ScrapOrder("BF20261030001", "刘洋", "运行工具库", 1),
        ScrapOrder("BF20261031002", "陈强", "中心工具库", 5)
    )
) {
    // 业务过滤：只展示审批通过的报废单
    val approvedOrders = orders.filter { it.isApproved }

    OrderListPage(
        title = "报废单列表",
        orders = approvedOrders,
        searchPlaceholder = "搜索报废单号、申请人或库房",
        orderNo = { it.orderNo },
        fields = { order ->
            listOf(
                DisplayField("申请人", order.applicant),
                DisplayField("所属库房", order.warehouse),
                DisplayField("工具数量", "${order.toolCount}件"),
                DisplayField("出库状态", order.outboundStatus)
            )
        },
        statusText = { "审批通过" }, // 列表里只剩审批通过的单据
        actionText = "出库核对",
        onActionClick = { onCheckClick(it) },
        listHeaderLabel = "审批通过的报废单",
        searchMatcher = { order, query ->
            order.orderNo.contains(query, ignoreCase = true) ||
                    order.applicant.contains(query) ||
                    order.warehouse.contains(query)
        },
        emptyText = "暂无审批通过的报废单",
        noResultText = "暂无匹配的报废单",
        onBackClick = onBackClick
    )
}
