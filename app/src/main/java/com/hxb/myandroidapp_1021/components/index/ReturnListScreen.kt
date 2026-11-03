package com.hxb.myandroidapp_1021.components.index

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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

enum class ReturnStatus(val label: String) {
    PARTIAL("部分归还"),
    UNRETURNED("未归还")
}

enum class FilterType(val label: String) {
    ALL("全部"),
    PARTIAL("部分归还"),
    UNRETURNED("未归还")
}

// 1. 归还业务的数据模型
data class ReturnOrder(
    val orderNo: String,
    val team: String,
    val person: String,
    val totalCount: Int,
    val returnedCount: Int,
    val unreturnedCount: Int,
    val status: ReturnStatus
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReturnListScreen(onBackClick: () -> Unit = {}) {
    val themeBlue = Color(0xFF1E71C8)
    val bgColor = Color(0xFFF5F6F8)
    val textDark = Color(0xFF222222)
    val textGray = Color(0xFF888888)
    var searchText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val showScrollToTopButton by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 2
        }
    }
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
    val filteredList = remember(selectedFilter, orderList) {
        when (selectedFilter) {
            FilterType.ALL -> orderList
            FilterType.PARTIAL -> orderList.filter { it.status == ReturnStatus.PARTIAL }
            FilterType.UNRETURNED -> orderList.filter { it.status == ReturnStatus.UNRETURNED }
        }
    }

    Scaffold(
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                modifier = Modifier.height(75.dp),
                title = {
                    Text(
                        "归还",
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
                // === 1. 顶部固定搜索框 ===
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .background(Color(0xFFF5F6F8), RoundedCornerShape(4.dp))
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF999999),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            BasicTextField(
                                value = searchText,
                                onValueChange = { searchText = it },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = TextStyle(fontSize = 14.sp, color = Color(0xFF333333)),
                                singleLine = true,
                                cursorBrush = SolidColor(themeBlue),
                                decorationBox = { innerTextField ->
                                    if (searchText.isEmpty()) {
                                        Text(
                                            text = "请输入归还单号",
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

                // === 2. 列表区域 ===
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState
                ) {
                    // 列表头部信息
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF5F6F8))
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 左侧下拉筛选区
                            Box {
                                Row(
                                    modifier = Modifier
                                        .clickable { isDropdownExpanded = true }
                                        .padding(vertical = 4.dp), // 增加点击区域
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

                                // 下拉菜单
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
                            Text(text = "共${orderList.size}张", fontSize = 14.sp, color = textGray)
                        }
                    }

                    // 订单列表
                    itemsIndexed(filteredList) { index, order ->
                        val displayFields = listOf(
                            DisplayField("班组", order.team),
                            DisplayField("领用人", order.person),
                            DisplayField("应还件数", "${order.totalCount}件"),
                            DisplayField("已归还", "${order.returnedCount}件"),
                            DisplayField("未归还", "${order.unreturnedCount}件")
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
                                        text = order.status.label,
                                        fontSize = 13.sp,
                                        color = themeBlue,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Button(
                                        onClick = { /* 开始归还逻辑 */ },
                                        modifier = Modifier
                                            .height(30.dp)
                                            .wrapContentWidth(),
                                        shape = RoundedCornerShape(4.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = themeBlue)
                                    ) {
                                        Text(
                                            "开始归还",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F0))

                            // 键值对展示区域
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
                                HorizontalDivider(thickness = 8.dp, color = bgColor)
                            }
                        }
                    }
                }
            }

            // === 3. 右下角回到顶部悬浮按钮 ===
            AnimatedVisibility(
                visible = showScrollToTopButton,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 32.dp)
            ) {
                SmallFloatingActionButton(
                    onClick = {
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