package com.hxb.myandroidapp_1021.pages

import androidx.compose.runtime.Composable
import com.hxb.myandroidapp_1021.components.OrderListPage
import com.hxb.myandroidapp_1021.models.DisplayField
import com.hxb.myandroidapp_1021.models.OrderCardSpec
import com.hxb.myandroidapp_1021.models.OrderListTexts
import com.hxb.myandroidapp_1021.models.ScrapOrder

@Composable
fun ScrapOutListScreen(
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
    // 只展示审批通过的报废单
    val approvedOrders = orders.filter { it.isApproved }

    OrderListPage(
        title = "报废单列表",
        orders = approvedOrders,
        card = OrderCardSpec(
            orderNo = { it.orderNo },
            fields = { order ->
                listOf(
                    DisplayField("申请人", order.applicant),
                    DisplayField("所属库房", order.warehouse),
                    DisplayField("工具数量", "${order.toolCount}件"),
                    DisplayField("出库状态", order.outboundStatus)
                )
            },
            statusText = { "审批通过" },
            actionText = "出库核对",
            onActionClick = { onCheckClick(it) },
            searchMatcher = { order, query ->
                order.orderNo.contains(query, ignoreCase = true) ||
                        order.applicant.contains(query) ||
                        order.warehouse.contains(query)
            }
        ),
        texts = OrderListTexts(
            searchPlaceholder = "搜索报废单号、申请人或库房",
            headerLabel = "审批通过的报废单",
            emptyText = "暂无审批通过的报废单",
            noResultText = "暂无匹配的报废单"
        ),
        onBackClick = onBackClick
    )
}
