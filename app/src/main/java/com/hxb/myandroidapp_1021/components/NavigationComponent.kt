package com.hxb.myandroidapp_1021.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hxb.myandroidapp_1021.models.TabItem
import com.hxb.myandroidapp_1021.routes.Routes

/**
 * Navigation Compose 导航
 *  Tab 切换都是用状态管理模拟的，正式项目中用 Navigation Compose 实现页面路由管理。
 */
@Composable
fun BottomNav(
    currentRoute: String,          // 当前选中的路由
    onTabClick: (String) -> Unit   // 点击Tab的回调
) {
    val tabs = listOf(
        TabItem("首页", Icons.Filled.Home, Icons.Outlined.Home, Routes.HOME),
        TabItem("项目", Icons.Filled.Work, Icons.Outlined.Work, Routes.PROJECT),
        TabItem("我的", Icons.Filled.Person, Icons.Outlined.Person, Routes.MINE)
    )
    NavigationBar {
        tabs.forEach { tab ->
            NavigationBarItem(
                selected = currentRoute == tab.route, // 是否选中
                onClick = { onTabClick(tab.route) },  // 点击事件
                icon = {
                    Icon(
                        imageVector = if (currentRoute == tab.route)
                            tab.selectedIcon else tab.unselectedIcon,
                        contentDescription = tab.name
                    )
                },
                label = { Text(tab.name, fontSize = 10.sp) }
            )
        }
    }
}

@Composable
fun NavTest() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Scaffold(
        // 底部导航栏
        bottomBar = {
            BottomNav(
                currentRoute = currentRoute ?: "",
                onTabClick = { route ->
                    navController.navigate(route) {
                        // Tab 点击时需要切换页面，并保存/恢复状态，三步
                        // popUpTo(起始页) { saveState = true } → 弹出中间页面到起始页，但保存当前Tab的状态
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // launchSingleTop = true → 如果已经在当前Tab，不重复创建
                        launchSingleTop = true
                        // restoreState = true → 恢复之前保存的状态
                        restoreState = true
                    }
                }
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            // 上下的安全边距
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Routes.HOME) { HomePage(navController) }
            composable(Routes.PROJECT) { ProjectPage() }
            composable(Routes.MINE) { MinePage() }
            composable(Routes.LOGIN) { LoginPage(navController) }
            composable(Routes.QUESTION) { navBackStackEntry ->
                // 获取路由传递的参数
                val res = navBackStackEntry.arguments?.getString("itemId")
                QuestionPage(navController, res)
            }
        }
    }
}

@Composable
fun NavDemo() {
    // 创建并记住 NavController
    val navController = rememberNavController()
    // 获取当前页面对应的路径
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    // 当前页面的路由名
    val currentRoute = navBackStackEntry?.destination?.route
    println("666-当前页面的路径是：$currentRoute")
    // 最外层的 NavHost - 页面容器
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        // 注册页面
        // composable("路由") { 页面组件 }
        composable(Routes.HOME) { HomePage(navController) }
        composable(Routes.PROJECT) { ProjectPage() }
        composable(Routes.MINE) { MinePage() }
        composable(Routes.LOGIN) { LoginPage(navController) }
        // navBackStackEntry: 里面存储着当前页面的路由信息
        composable(Routes.QUESTION) { navBackStackEntry ->
            // 获取路由传递的参数
            val res = navBackStackEntry.arguments?.getString("itemId")
            QuestionPage(navController, res)
        }
    }
}

// 主tab页面
@Composable
fun HomePage(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Gray)
            .padding(top = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("首页", fontSize = 25.sp)
        Button(onClick = {
            navController.navigate(Routes.LOGIN)
        }) {
            Text("跳转登录页")
        }
        Button(onClick = {
            // 跳转到问题详情页，并传递参数
            navController.navigate(Routes.question("666"))
        }) {
            Text("跳转问题详情页")
        }
    }
}

@Composable
fun ProjectPage() {
    val myList = listOf(
        "Item 1", "Item 2", "Item 3", "Item 4", "Item 5",
        "Item 6", "Item 7", "Item 8", "Item 9", "Item 10",
        "Item 11", "Item 12", "Item 13", "Item 14", "Item 15"
    )
    /*Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Green)
            .padding(top = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("项目", fontSize = 25.sp)
    }*/
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(myList) {
            Text(text = it, modifier = Modifier.height(120.dp))
        }
    }
}

@Composable
fun MinePage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Blue)
            .padding(top = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("我的", fontSize = 25.sp)
    }
}

// 非主tab页面
@Composable
fun LoginPage(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Yellow)
            .padding(top = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("登录", fontSize = 25.sp)
        Button(onClick = {
            navController.popBackStack()
        }) {
            Text("返回")
        }
    }
}

@Composable
fun QuestionPage(navController: NavController, itemId: String?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Gray)
            .padding(top = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("问题详情页，传递过来的参数是：$itemId", fontSize = 25.sp)
        Button(onClick = {
            navController.popBackStack()
        }) {
            Text("返回")
        }
    }
}