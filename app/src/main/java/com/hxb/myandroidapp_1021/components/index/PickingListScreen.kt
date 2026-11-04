package com.hxb.myandroidapp_1021.components.index

import androidx.compose.runtime.Composable
import com.hxb.myandroidapp_1021.components.index.components.OrderListPage
import com.hxb.myandroidapp_1021.components.index.models.DisplayField

// 定义拣选单数据模型
data class PickingOrder(
    val orderNo: String,
    val team: String,
    val person: String,
    val toolCount: Int,
    val expectedTime: String,
    val status: String
)

@Composable
fun PickingListScreen(
    onBackClick: () -> Unit = {},
    onStartPickClick: (PickingOrder) -> Unit = {}
) {
    // 模拟数据
    val orderList = listOf(
        PickingOrder("JC20261028001", "检修一班", "张伟", 3, "10-30 09:30", "待处理"),
        PickingOrder("JC20261028002", "维修二班", "李明", 2, "10-30 10:00", "待处理"),
        PickingOrder("JC20261028003", "电气班组", "王健", 4, "10-30 10:30", "待处理"),
        PickingOrder("JC20261028001", "检修一班", "张伟", 3, "10-30 09:30", "待处理"),
        PickingOrder("JC20261028002", "维修二班", "李明", 2, "10-30 10:00", "待处理"),
        PickingOrder("JC20261028003", "电气班组", "王健", 4, "10-30 10:30", "待处理"),
        PickingOrder("JC20261028004", "运行一班", "刘洋", 5, "10-30 11:00", "待处理"),
        PickingOrder("JC20261028005", "热控班组", "陈强", 1, "10-30 11:15", "待处理"),
        PickingOrder("JC20261028006", "继保班", "赵磊", 8, "10-30 13:00", "待处理"),
        PickingOrder("JC20261028007", "综合班", "孙浩", 2, "10-30 14:00", "待处理"),
        PickingOrder("JC20261028008", "变电检修班", "周杰", 6, "10-30 14:30", "待处理"),
        PickingOrder("JC20261028009", "汽机本体班", "吴刚", 4, "10-30 15:00", "待处理"),
        PickingOrder("JC20261028010", "锅炉辅机班", "郑爽", 7, "10-30 15:30", "待处理"),
        PickingOrder("JC20261028011", "检修一班", "张伟", 9, "10-30 16:00", "待处理"),
        PickingOrder("JC20261028011", "检修一班", "张伟", 9, "10-30 16:00", "待处理")
    )

    OrderListPage(
        title = "借出",
        orders = orderList,
        searchPlaceholder = "请输入拣选单号",
        orderNo = { it.orderNo },
        fields = { order ->
            listOf(
                DisplayField("班组", order.team),
                DisplayField("领用人", order.person),
                DisplayField("工具数量", "${order.toolCount}件"),
                DisplayField("期望领用", order.expectedTime)
            )
        },
        statusText = { it.status },
        actionText = "开始拣选",
        listHeaderLabel = "待处理拣选单",
        searchMatcher = { order, query -> order.orderNo.contains(query, ignoreCase = true) },
        onBackClick = onBackClick,
        onActionClick = { order -> onStartPickClick(order) }
    )
}
