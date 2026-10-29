package com.hxb.myandroidapp_1021.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxb.myandroidapp_1021.models.TabItem

/**
 * 什么是Scaffold：Scaffold是Compose提供的“页面框架模板”
 *  paddingValues → Scaffold 自动算出的安全间距，你不用管具体数值，用它就行
 *  topBar：屏幕顶部的应用栏
 *  bottomBar：屏幕底部的应用栏
 *  floatingActionButton：悬浮操作按钮，通常位于右下角
 *  Scaffold 的价值：不用手动计算状态栏高度、底部导航栏高度、安全区域。Scaffold 全自动处理，你只需要用 paddingValues 就行。
 *  SecondaryScrollableTabRow 分类 Tab
 *  ModalBottomSheet 底部弹窗
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSheetDemo(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    // 记住当前选中的排序方式
    var selectedSort by remember { mutableStateOf("默认") }
    // 排序选项
    val sortOptions = listOf("默认", "浏览量", "难度", "推荐")
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .height(300.dp)
        ) {
            // === 顶部：重置 + 标题 + 完成 ===
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "重置",
                    color = Color.Gray,
                    modifier = Modifier.clickable { selectedSort = "默认" }
                )
                Text(
                    "筛选题目",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    "完成",
                    color = Color(0xFF1A73E8),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onConfirm(selectedSort) }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            // === 排序选项（用 FlowRow 自动换行排列）===
            Text("题目排序", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                sortOptions.forEach { sort ->
                    // 每个排序选项是一个圆角按钮
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (selectedSort == sort) Color(0xFF1A73E8)
                                else Color(0xFFF5F5F5)
                            )
                            .clickable { selectedSort = sort }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            sort,
                            fontSize = 13.sp,
                            color = if (selectedSort == sort) Color.White else Color.Black
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldStep03() {
    // 创建 Tab 列表
    val tabs = listOf(
        TabItem("首页", Icons.Filled.Home, Icons.Outlined.Home, "home"),
        TabItem("项目", Icons.Filled.AccessTimeFilled, Icons.Outlined.AccessTime, "project"),
        TabItem("我的", Icons.Filled.Person, Icons.Outlined.Person, "mine")
    )
    // 当前路由名称，定义状态变量
    var currentRoute by remember { mutableStateOf("home") }
    // 控制弹窗的显示和隐藏
    var showSheet by remember { mutableStateOf(false) }
    Scaffold(
        // 配置导航栏
        topBar = {
            TopAppBar(
                title = { Text("导航栏", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xff1a73e8),  // 背景色
                    titleContentColor = Color.White
                )
            )
        },
        // 配置底部的Tab栏
        bottomBar = {
            BottomNav(
                tabs = tabs,
                currentRoute = currentRoute,
                onTabClick = { route ->
                    currentRoute = route
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(color = Color.Red),
            contentAlignment = Alignment.TopStart
        ) {
            // 二级滚动Tab
            CategoryDemo()
            Button(onClick = {
                showSheet = true
            }) {
                Text("点击打开弹窗")
            }
            // 弹窗
            if (showSheet) {
                FilterSheetDemo(
                    onDismiss = { showSheet = false },
                    onConfirm = { showSheet = false })
            }
        }
    }
}

@Composable
fun CategoryDemo() {
    val categories = listOf("全部", "Android", "Java", "Kotlin", "网络", "算法", "数据库")
    var selectedIndex by remember { mutableIntStateOf(0) }
    SecondaryScrollableTabRow(
        selectedTabIndex = selectedIndex,
        edgePadding = 0.dp, // 二级Tab距离两端的间距
        indicator = {}, // 去除默认的指示器
        divider = {} // 底部分割线
    ) {
        // 需要索引和selectedItem
        categories.forEachIndexed { index, category ->
            Tab(
                selected = selectedIndex == index,
                text = {
                    // 自定义二级Tab
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 8.dp)
                    ) {
                        // 文字
                        Text(
                            category,
                            fontSize = 15.sp,
                            color = if (selectedIndex == index) Color.Black else Color.Gray,
                            fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Normal
                        )
                        // 选中的下划线
                        if (selectedIndex == index) {
                            Spacer(modifier = Modifier.height(5.dp))
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(2.dp)
                                    .background(color = Color.Black)
                            )
                        }
                    }
                },
                onClick = { selectedIndex = index }
            )
        }
    }
}

// 封装底部导航栏
@Composable
fun BottomNav(
    tabs: List<TabItem>,
    currentRoute: String,
    // 子组件切换，需要父组件去处理
    onTabClick: (String) -> Unit
) {
    NavigationBar() {
        tabs.forEach { tab ->
            NavigationBarItem(
                selected = currentRoute == tab.route,
                icon = {
                    Icon(
                        imageVector = if (currentRoute == tab.route) tab.selectedIcon else tab.unselectedIcon,
                        contentDescription = tab.name
                    )
                },
                label = {
                    Text(tab.name)
                },
                onClick = { onTabClick(tab.route) }
            )
        }
    }
}

// 上脚手架
// 忽略TopAppBar属性的警告
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldStep02() {
    // paddingValues包含了上下安全区的控制
    Scaffold(
        // 配置导航栏
        topBar = {
            TopAppBar(
                title = { Text("导航栏", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xff1a73e8),  // 背景色
                    titleContentColor = Color.White
                )
            )
        },
        // 配置底部的Tab栏
        bottomBar = {
            NavigationBar() {
                NavigationBarItem(
                    selected = true,
                    icon = {
                        Icon(Icons.Filled.Home, contentDescription = "首页")
                    },
                    label = {
                        Text("首页")
                    },
                    onClick = {}
                )
                NavigationBarItem(
                    selected = false,
                    icon = {
                        Icon(Icons.Filled.AccessTime, contentDescription = "消息")
                    },
                    label = {
                        Text("消息")
                    },
                    onClick = {}
                )
                NavigationBarItem(
                    selected = false,
                    icon = {
                        Icon(Icons.Filled.Person, contentDescription = "我的")
                    },
                    label = {
                        Text("我的")
                    },
                    onClick = {}
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {/* 处理点击事件 */ }
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(color = Color.Red),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "主题内容",
                fontSize = 20.sp
            )
        }
    }
}

// 无脚手架
@Composable
fun ScaffoldStep01() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Red),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "主题内容",
            fontSize = 20.sp
        )
    }
}