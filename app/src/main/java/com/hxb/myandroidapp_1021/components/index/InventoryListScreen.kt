package com.hxb.myandroidapp_1021.components.index

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.hxb.myandroidapp_1021.components.index.components.OrderListPage
import com.hxb.myandroidapp_1021.components.index.models.DisplayField

// 盘点任务单数据模型（列表展示 + 盘点状态）
data class InventoryRecord(
    val orderNo: String,
    val team: String,
    val person: String,
    val toolCount: Int,
    val inventoryDate: String,
    val status: String   // 已盘点 / 未盘点
)

/**
 * 盘点任务单列表页（一级路由，从仪表盘「盘点单」进来）。
 * 和送检 / 送修 / 报废列表一样用 OrderListPage，只是：
 *  - 状态展示的是盘点状态（已盘点 / 未盘点）
 *  - 右下角多一个「+」悬浮按钮，点它去 InventoryScanScreen 新建一次盘点
 */
@Composable
fun InventoryListScreen(
    onBackClick: () -> Unit = {},
    onAddApplyClick: () -> Unit = {}
) {
    // 模拟数据
    val orderList = listOf(
        InventoryRecord("PD20261028001", "检修一班", "张伟", 3, "10-28 09:30", "已盘点"),
        InventoryRecord("PD20261028002", "维修二班", "李明", 2, "10-28 10:00", "未盘点"),
        InventoryRecord("PD20261028003", "电气班组", "王健", 4, "10-28 10:30", "已盘点"),
        InventoryRecord("PD20261028004", "运行一班", "刘洋", 1, "10-28 11:00", "未盘点"),
        InventoryRecord("PD20261028005", "热控班组", "陈强", 5, "10-28 11:15", "已盘点"),
        InventoryRecord("PD20261028006", "继保班", "赵磊", 3, "10-28 13:00", "未盘点"),
        InventoryRecord("PD20261028007", "综合班", "孙浩", 2, "10-28 14:00", "已盘点"),
        InventoryRecord("PD20261028008", "变电检修班", "周杰", 6, "10-28 14:30", "未盘点"),
        InventoryRecord("PD20261028009", "汽机本体班", "吴刚", 4, "10-28 15:00", "已盘点"),
        InventoryRecord("PD20261028010", "锅炉辅机班", "郑爽", 7, "10-28 15:30", "未盘点")
    )

    OrderListPage(
        title = "盘点",
        orders = orderList,
        searchPlaceholder = "请输入盘点单号",
        orderNo = { it.orderNo },
        fields = { record ->
            listOf(
                DisplayField("班组", record.team),
                DisplayField("盘点人", record.person),
                DisplayField("工具数量", "${record.toolCount}件"),
                DisplayField("盘点日期", record.inventoryDate)
            )
        },
        statusText = { it.status },
        // 未盘点用红色，已盘点用主题蓝
        statusColor = {
            if (it.status == "未盘点") Color(0xFFE53935) else Color(0xFF1E71C8)
        },
        listHeaderLabel = "盘点任务单",
        searchMatcher = { record, query -> record.orderNo.contains(query, ignoreCase = true) },
        onBackClick = onBackClick,
        onAddClick = onAddApplyClick
    )
}
