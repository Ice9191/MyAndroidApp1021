package com.hxb.myandroidapp_1021.components.index

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// 定义拣选单数据模型
data class PickingOrder(
    val orderNo: String,
    val team: String,
    val person: String,
    val toolCount: Int,
    val expectedTime: String,
    val status: String
)

data class DisplayField(val label: String, val value: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickingListScreen(onBackClick: () -> Unit = {}) {
    val themeBlue = Color(0xFF1E71C8)
    val bgColor = Color(0xFFF5F6F8)
    val textDark = Color(0xFF222222)
    val textGray = Color(0xFF888888)
    var searchText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val showScrollToTopButton by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 2 // 列表向下滑动了2个条目后，显示回到顶部按钮
        }
    }
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

    Scaffold(
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                modifier = Modifier.height(75.dp),
                title = {
                    Text(
                        "借出",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.offset(x = (-16).dp)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.offset(x = (-7).dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = themeBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // === 1. 顶部搜索框 ===
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // 整个搜索框的容器
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .background(Color(0xFFF5F6F8), RoundedCornerShape(4.dp))
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 左侧放大镜图标
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF999999),
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // 真正输入框
                        Box(modifier = Modifier.weight(1f)) {
                            BasicTextField(
                                value = searchText,
                                onValueChange = { searchText = it },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = TextStyle(
                                    fontSize = 14.sp,
                                    color = Color(0xFF333333)
                                ),
                                singleLine = true,
                                cursorBrush = SolidColor(themeBlue),
                                decorationBox = { innerTextField ->
                                    if (searchText.isEmpty()) {
                                        Text(
                                            text = "请输入拣选单号",
                                            fontSize = 14.sp,
                                            color = Color(0xFF999999)
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState
                ) {
                    // === 2. 列表头部信息 ===
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF5F6F8))
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "待处理拣选单", fontSize = 14.sp, color = textGray)
                            Text(text = "共${orderList.size}张", fontSize = 14.sp, color = textGray)
                        }
                    }

                    // === 3. 订单列表 ===
                    itemsIndexed(orderList) { index, order ->
                        val displayFields = listOf(
                            DisplayField("班组", order.team),
                            DisplayField("领用人", order.person),
                            DisplayField("工具数量", "${order.toolCount}件"),
                            DisplayField("期望领用", order.expectedTime)
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                        ) {
                            // 第一行：单号 + 状态 + 按钮
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = order.orderNo,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textDark
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = order.status,
                                        fontSize = 13.sp,
                                        color = themeBlue,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Button(
                                        onClick = { /* 开始拣选逻辑 */ },
                                        modifier = Modifier
                                            .height(30.dp)
                                            .wrapContentWidth(),
                                        shape = RoundedCornerShape(4.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = themeBlue)
                                    ) {
                                        Text(
                                            "开始拣选",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(
                                thickness = 1.dp,
                                color = Color(0xFFF0F0F0)
                            )

                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                displayFields.forEach { field ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${field.label}：",
                                            fontSize = 13.sp,
                                            color = textGray,
                                            maxLines = 1,
                                            modifier = Modifier.width(65.dp)
                                        )
                                        Text(
                                            text = field.value,
                                            fontSize = 13.sp,
                                            color = textDark,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }

                            if (index != orderList.lastIndex) {
                                HorizontalDivider(
                                    thickness = 8.dp,
                                    color = bgColor
                                )
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = showScrollToTopButton,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }), // 从下往上渐显
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }), // 往下渐隐
                modifier = Modifier
                    .align(Alignment.BottomEnd) // 对齐到右下角
                    .padding(end = 16.dp, bottom = 32.dp) // 距离右边和下边的距离
            ) {
                SmallFloatingActionButton(
                    onClick = {
                        // 点击回到顶部
                        coroutineScope.launch {
                            listState.animateScrollToItem(0)
                        }
                    },
                    containerColor = themeBlue,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "回到顶部"
                    )
                }
            }
        }
    }
}