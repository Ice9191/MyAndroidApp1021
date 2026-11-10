package com.hxb.myandroidapp_1021.ui.inventory

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxb.myandroidapp_1021.data.models.DisplayField
import com.hxb.myandroidapp_1021.data.models.InventoryRecord
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryListScreen(
    onBackClick: () -> Unit = {},
    onStartInventoryClick: (InventoryRecord) -> Unit = {}
) {
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

    val themeBlue = Color(0xFF1E71C8)
    val textDark = Color(0xFF222222)
    val pageBg = Color(0xFFF5F6F8)
    val textGray = Color(0xFF888888)

    val focusManager = LocalFocusManager.current
    val searchState = rememberTextFieldState()
    val query = searchState.text.toString().trim()
    val visibleOrders = if (query.isEmpty()) {
        orderList
    } else {
        orderList.filter {
            it.orderNo.contains(query, ignoreCase = true)
        }
    }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val showScrollToTopButton by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 2 } // 向下滑过 2 个条目再显示回到顶部
    }
    val clearFocus = { focusManager.clearFocus() }

    Scaffold(
        modifier = Modifier.imePadding(),
        containerColor = pageBg,
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .height(75.dp)
                    .pointerInput(Unit) { detectTapGestures(onTap = { clearFocus() }) },
                title = {
                    Text(
                        text = "盘点",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.offset(x = (-16).dp)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            clearFocus()
                            onBackClick()
                        },
                        modifier = Modifier.offset(x = (-7).dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Back"
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
                .pointerInput(Unit) { detectTapGestures(onTap = { clearFocus() }) }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // === 1. 顶部搜索框 ===
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        state = searchState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp),
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = Color(0xFF333333)
                        ),
                        placeholder = {
                            Text(
                                text = "请输入盘点单号",
                                fontSize = 14.sp,
                                color = Color(0xFF999999)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color(0xFF999999),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        lineLimits = TextFieldLineLimits.SingleLine,
                        shape = RoundedCornerShape(4.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF333333),
                            unfocusedTextColor = Color(0xFF333333),
                            focusedContainerColor = Color(0xFFF5F6F8),
                            unfocusedContainerColor = Color(0xFFF5F6F8),
                            focusedBorderColor = themeBlue,
                            unfocusedBorderColor = Color.Transparent,
                            cursorColor = themeBlue
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                    )
                }

                // === 2. 列表 ===
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(pageBg)
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "盘点任务单", fontSize = 14.sp, color = textGray)
                            Text(
                                text = "共${visibleOrders.size}张",
                                fontSize = 14.sp,
                                color = textGray
                            )
                        }
                    }

                    if (visibleOrders.isEmpty()) {
                        item {
                            Text(
                                text = if (query.isEmpty()) "暂无数据" else "暂无匹配数据",
                                fontSize = 14.sp,
                                color = textGray,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White)
                                    .padding(16.dp)
                            )
                        }
                    } else {
                        items(visibleOrders) { order ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White)
                            ) {
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
                                        color = textDark,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = order.status,
                                            fontSize = 13.sp,
                                            color = if (order.status == "未盘点") Color(0xFFE53935) else themeBlue,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )

                                        Button(
                                            onClick = { onStartInventoryClick(order) },
                                            modifier = Modifier
                                                .height(30.dp)
                                                .wrapContentWidth(),
                                            shape = RoundedCornerShape(4.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = themeBlue)
                                        ) {
                                            Text(
                                                text = "开始盘点",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }

                                HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F0))

                                Column(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(
                                        DisplayField("班组", order.team),
                                        DisplayField("盘点人", order.person),
                                        DisplayField("工具数量", "${order.toolCount}件"),
                                        DisplayField("盘点日期", order.inventoryDate)
                                    ).forEach { field ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = field.label,
                                                fontSize = 13.sp,
                                                color = textGray,
                                                maxLines = 1,
                                                modifier = Modifier.alignByBaseline()
                                            )
                                            Text(
                                                text = field.value,
                                                fontSize = 13.sp,
                                                color = textDark,
                                                maxLines = 1,
                                                modifier = Modifier
                                                    .padding(start = 16.dp)
                                                    .alignByBaseline()
                                            )
                                        }
                                    }
                                }

                                HorizontalDivider(thickness = 8.dp, color = pageBg)
                            }
                        }
                    }
                }
            }

            // === 3. 右下角悬浮按钮：回到顶部 ===
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 32.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AnimatedVisibility(
                    visible = showScrollToTopButton,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
                ) {
                    SmallFloatingActionButton(
                        onClick = { coroutineScope.launch { listState.animateScrollToItem(0) } },
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
}
