package com.hxb.myandroidapp_1021.components.index

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxb.myandroidapp_1021.components.index.components.OrderListPage
import com.hxb.myandroidapp_1021.components.index.models.DisplayField

enum class ReturnStatus(val label: String) {
    PARTIAL("部分归还"),
    UNRETURNED("未归还")
}

enum class FilterType(val label: String) {
    ALL("全部"),
    PARTIAL("部分归还"),
    UNRETURNED("未归还")
}

// 归还业务的数据模型
data class ReturnOrder(
    val orderNo: String,
    val team: String,
    val person: String,
    val totalCount: Int,
    val returnedCount: Int,
    val unreturnedCount: Int,
    val status: ReturnStatus
)
@Composable
fun ReturnListScreen(onBackClick: () -> Unit = {}) {
    val themeBlue = Color(0xFF1E71C8)
    val textDark = Color(0xFF222222)

    // 模拟数据
    val orderList = listOf(
        ReturnOrder("JC20261027006", "检修一班", "张伟", 5, 2, 3, ReturnStatus.PARTIAL),
        ReturnOrder("JC20261027007", "维修二班", "李明", 3, 0, 3, ReturnStatus.UNRETURNED),
        ReturnOrder("JC20261027008", "电气班组", "王健", 6, 4, 2, ReturnStatus.PARTIAL),
        ReturnOrder("JC20261027009", "运行一班", "刘洋", 4, 1, 3, ReturnStatus.PARTIAL),
        ReturnOrder("JC20261027010", "热控班组", "陈强", 2, 0, 2, ReturnStatus.UNRETURNED),
        ReturnOrder("JC20261027011", "继保班", "赵磊", 8, 6, 2, ReturnStatus.PARTIAL),
        ReturnOrder("JC20261027012", "综合班", "孙浩", 3, 3, 0, ReturnStatus.PARTIAL),
        ReturnOrder("JC20261027013", "变电检修班", "周杰", 6, 2, 4, ReturnStatus.PARTIAL),
        ReturnOrder("JC20261027014", "汽机本体班", "吴刚", 5, 0, 5, ReturnStatus.UNRETURNED),
        ReturnOrder("JC20261027015", "锅炉辅机班", "郑爽", 7, 5, 2, ReturnStatus.PARTIAL)
    )

    var selectedFilter by remember { mutableStateOf(FilterType.ALL) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val filteredList = when (selectedFilter) {
        FilterType.ALL -> orderList
        FilterType.PARTIAL -> orderList.filter { it.status == ReturnStatus.PARTIAL }
        FilterType.UNRETURNED -> orderList.filter { it.status == ReturnStatus.UNRETURNED }
    }

    OrderListPage(
        title = "归还",
        orders = filteredList,
        searchPlaceholder = "请输入归还单号",
        orderNo = { it.orderNo },
        fields = { order ->
            listOf(
                DisplayField("班组", order.team),
                DisplayField("领用人", order.person),
                DisplayField("应还件数", "${order.totalCount}件"),
                DisplayField("已归还", "${order.returnedCount}件"),
                DisplayField("未归还", "${order.unreturnedCount}件")
            )
        },
        statusText = { it.status.label },
        actionText = "开始归还",
        listHeader = {
            Box {
                Row(
                    modifier = Modifier
                        .clickable { isDropdownExpanded = true }
                        .padding(vertical = 4.dp), // 加大点击区域
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedFilter.label,
                        fontSize = 14.sp,
                        color = textDark,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "展开筛选",
                        tint = textDark
                    )
                }
                DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false },
                    containerColor = Color.White
                ) {
                    FilterType.entries.forEach { filter ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = filter.label,
                                    fontSize = 14.sp,
                                    color = if (filter == selectedFilter) themeBlue else textDark
                                )
                            },
                            onClick = {
                                selectedFilter = filter
                                isDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        },
        searchMatcher = { order, query -> order.orderNo.contains(query, ignoreCase = true) },
        onBackClick = onBackClick
    )
}
