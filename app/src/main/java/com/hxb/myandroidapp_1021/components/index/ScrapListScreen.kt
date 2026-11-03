package com.hxb.myandroidapp_1021.components.index

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxb.myandroidapp_1021.components.index.components.ApplicationTopBar

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
    // shortcut: 当前使用示例报废单，接入后台列表接口时传入实际数据。
    orders: List<ScrapOrder> = listOf(
        ScrapOrder("BF20261027003", "李明", "中心工具库", 2),
        ScrapOrder("BF20261028001", "张伟", "中心工具库", 3),
        ScrapOrder("BF20261029002", "王健", "电气工具库", 4),
        ScrapOrder("BF20261030001", "刘洋", "运行工具库", 1),
        ScrapOrder("BF20261031002", "陈强", "中心工具库", 5)
    )
) {
    val themeBlue = Color(0xFF1E71C8)
    val bgColor = Color(0xFFF5F6F8)
    val textDark = Color(0xFF222222)
    val textGray = Color(0xFF888888)
    var searchText by remember { mutableStateOf("") }
    val query = searchText.trim()
    val visibleOrders = orders.filter { order ->
        order.isApproved && (
                order.orderNo.contains(query, ignoreCase = true) ||
                        order.applicant.contains(query) || order.warehouse.contains(query)
                )
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        containerColor = bgColor,
        topBar = { ApplicationTopBar("报废单列表", onBackClick) }
    ) { paddingValues ->
        Column(Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .background(bgColor, RoundedCornerShape(4.dp))
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "搜索",
                        tint = Color(0xFF999999),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    BasicTextField(
                        value = searchText, onValueChange = { searchText = it },
                        modifier = Modifier.weight(1f), singleLine = true,
                        textStyle = TextStyle(fontSize = 14.sp, color = textDark),
                        cursorBrush = SolidColor(themeBlue),
                        decorationBox = { innerTextField ->
                            if (searchText.isEmpty()) {
                                Text(
                                    "搜索报废单号、申请人或库房",
                                    fontSize = 14.sp,
                                    color = Color(0xFF999999)
                                )
                            }
                            innerTextField()
                        }
                    )
                }
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("审批通过的报废单", fontSize = 14.sp, color = textGray)
                Text("共${visibleOrders.size}张", fontSize = 14.sp, color = textGray)
            }
            LazyColumn(Modifier.fillMaxSize()) {
                if (visibleOrders.isEmpty()) {
                    item {
                        Text(
                            if (query.isEmpty()) "暂无审批通过的报废单" else "暂无匹配的报废单",
                            fontSize = 14.sp, color = textGray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .padding(16.dp)
                        )
                    }
                }
                items(visibleOrders, key = { it.orderNo }) { order ->
                    Column(Modifier
                        .fillMaxWidth()
                        .background(Color.White)) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                order.orderNo,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = textDark,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Text("审批通过", fontSize = 13.sp, color = themeBlue)
                            Button(
                                onClick = { onCheckClick(order) },
                                modifier = Modifier.height(30.dp),
                                shape = RoundedCornerShape(4.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = themeBlue)
                            ) {
                                Text(
                                    "出库核对",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                        }
                        HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F0))
                        Column(
                            Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                DisplayField("申请人", order.applicant),
                                DisplayField("所属库房", order.warehouse),
                                DisplayField("工具数量", "${order.toolCount}件"),
                                DisplayField("出库状态", order.outboundStatus)
                            ).forEach { field ->
                                Row(
                                    Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "${field.label}：",
                                        fontSize = 13.sp,
                                        color = textGray,
                                        modifier = Modifier.width(65.dp)
                                    )
                                    Text(
                                        field.value,
                                        fontSize = 13.sp,
                                        color = textDark,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                    HorizontalDivider(thickness = 8.dp, color = bgColor)
                }
                item {
                    Spacer(Modifier
                        .height(20.dp)
                        .fillMaxWidth()
                        .background(Color.White))
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun ScrapListScreenPreview() {
    ScrapListScreen()
}
