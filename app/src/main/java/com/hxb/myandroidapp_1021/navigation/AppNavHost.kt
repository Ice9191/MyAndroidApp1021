package com.hxb.myandroidapp_1021.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.hxb.myandroidapp_1021.pages.DashboardScreen
import com.hxb.myandroidapp_1021.pages.InspectionConfirmScreen
import com.hxb.myandroidapp_1021.pages.InspectionListScreen
import com.hxb.myandroidapp_1021.pages.InventoryListScreen
import com.hxb.myandroidapp_1021.pages.InventoryResultScreen
import com.hxb.myandroidapp_1021.pages.InventoryScanScreen
import com.hxb.myandroidapp_1021.pages.LoginScreen
import com.hxb.myandroidapp_1021.pages.PickingConfirmScreen
import com.hxb.myandroidapp_1021.pages.PickingListScreen
import com.hxb.myandroidapp_1021.pages.PickingScanScreen
import com.hxb.myandroidapp_1021.pages.RepairConfirmScreen
import com.hxb.myandroidapp_1021.pages.RepairListScreen
import com.hxb.myandroidapp_1021.pages.RepairScanScreen
import com.hxb.myandroidapp_1021.pages.ReturnConfirmScreen
import com.hxb.myandroidapp_1021.pages.ReturnListScreen
import com.hxb.myandroidapp_1021.pages.ReturnScanScreen
import com.hxb.myandroidapp_1021.pages.ScrapConfirmScreen
import com.hxb.myandroidapp_1021.pages.ScrapListScreen
import com.hxb.myandroidapp_1021.pages.ScrapOutCheckScreen
import com.hxb.myandroidapp_1021.pages.ScrapOutListScreen
import com.hxb.myandroidapp_1021.pages.ScrapScanScreen
import com.hxb.myandroidapp_1021.pages.ServerConfigScreen
import com.hxb.myandroidapp_1021.models.ScrapOrder
import com.hxb.myandroidapp_1021.pages.InspectionScanScreen
import com.hxb.myandroidapp_1021.routes.Dashboard
import com.hxb.myandroidapp_1021.routes.InspectionConfirm
import com.hxb.myandroidapp_1021.routes.InspectionList
import com.hxb.myandroidapp_1021.routes.InspectionScan
import com.hxb.myandroidapp_1021.routes.InventoryList
import com.hxb.myandroidapp_1021.routes.InventoryResult
import com.hxb.myandroidapp_1021.routes.InventoryScan
import com.hxb.myandroidapp_1021.routes.Login
import com.hxb.myandroidapp_1021.routes.PickingConfirm
import com.hxb.myandroidapp_1021.routes.PickingList
import com.hxb.myandroidapp_1021.routes.PickingScan
import com.hxb.myandroidapp_1021.routes.RepairConfirm
import com.hxb.myandroidapp_1021.routes.RepairList
import com.hxb.myandroidapp_1021.routes.RepairScan
import com.hxb.myandroidapp_1021.routes.ReturnConfirm
import com.hxb.myandroidapp_1021.routes.ReturnList
import com.hxb.myandroidapp_1021.routes.ReturnScan
import com.hxb.myandroidapp_1021.routes.ScrapConfirm
import com.hxb.myandroidapp_1021.routes.ScrapList
import com.hxb.myandroidapp_1021.routes.ScrapOutCheck
import com.hxb.myandroidapp_1021.routes.ScrapOutList
import com.hxb.myandroidapp_1021.routes.ScrapScan
import com.hxb.myandroidapp_1021.routes.ServerConfig
import com.hxb.myandroidapp_1021.routes.toInventoryScan
import com.hxb.myandroidapp_1021.routes.toPickingConfirm
import com.hxb.myandroidapp_1021.routes.toPickingScan
import com.hxb.myandroidapp_1021.routes.toReturnConfirm
import com.hxb.myandroidapp_1021.routes.toReturnScan
import com.hxb.myandroidapp_1021.routes.toScrapOutCheck

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    // 防止重复点击返回导致页面空白
    val safeBack: () -> Unit = {
        if (navController.previousBackStackEntry != null) {
            navController.popBackStack()
        }
    }

    NavHost(
        navController = navController,
        startDestination = Dashboard,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6F8)),
        // 新页面从右向左完全滑入
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth }, // 从屏幕最右侧开始
                animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
            )
        },
        // 旧页面向左缓慢移动
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth / 4 }, // 只向左移 1/4 屏幕，保持可见
                animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
            )
        },
        // 旧页面从左向右滑回原位
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth / 4 }, // 从左边 1/4 处滑回
                animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
            )
        },
        // 当前页面向右滑出屏幕
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth }, // 完全滑向屏幕右侧
                animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
            )
        }
    ) {
        // 首页
        composable<Dashboard>(
            enterTransition = { EnterTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            DashboardScreen(
                onLogoutClick = {
                    navController.navigate(Login) {
                        popUpTo<Dashboard> { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onSettingsClick = {
                    navController.navigate(ServerConfig) { launchSingleTop = true }
                },
                onMenuClick = { route ->
                    navController.navigate(route) { launchSingleTop = true }
                }
            )
        }
        // 登录页
        composable<Login>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Dashboard) {
                        popUpTo<Login> { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        // 服务器配置页
        composable<ServerConfig> {
            ServerConfigScreen(
                onBackClick = safeBack,
                onSaveClick = safeBack
            )
        }
        // 借出
        composable<PickingList> {
            PickingListScreen(
                onBackClick = safeBack,
                onStartPickClick = { order ->
                    navController.navigate(order.toPickingScan()) { launchSingleTop = true }
                }
            )
        }
        composable<PickingScan> { entry ->
            val route = entry.toRoute<PickingScan>()
            PickingScanScreen(
                order = route,
                onBackClick = safeBack,
                onNextClick = {
                    navController.navigate(route.toPickingConfirm()) {
                        launchSingleTop = true
                    }
                },
                onSettingsClick = {},
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        composable<PickingConfirm> { entry ->
            val route = entry.toRoute<PickingConfirm>()
            PickingConfirmScreen(
                order = route,
                onBackClick = safeBack,
                onConfirmClick = { navController.popBackStack<PickingList>(false) },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        // 归还
        composable<ReturnList> {
            ReturnListScreen(
                onBackClick = safeBack,
                onStartReturnClick = { order ->
                    navController.navigate(order.toReturnScan()) { launchSingleTop = true }
                }
            )
        }
        composable<ReturnScan> { entry ->
            val route = entry.toRoute<ReturnScan>()
            ReturnScanScreen(
                order = route,
                onBackClick = safeBack,
                onNextClick = {
                    navController.navigate(route.toReturnConfirm()) {
                        launchSingleTop = true
                    }
                },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        composable<ReturnConfirm> { entry ->
            val route = entry.toRoute<ReturnConfirm>()
            ReturnConfirmScreen(
                order = route,
                onBackClick = safeBack,
                onConfirmClick = { navController.popBackStack<ReturnList>(false) },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        // 工具送检
        composable<InspectionList> {
            InspectionListScreen(
                onBackClick = safeBack,
                onAddApplyClick = { navController.navigate(InspectionScan) }
            )
        }
        composable<InspectionScan> {
            InspectionScanScreen(
                onBackClick = safeBack,
                onNextClick = { navController.navigate(InspectionConfirm) },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        composable<InspectionConfirm> {
            InspectionConfirmScreen(
                onBackClick = safeBack,
                onConfirmClick = { navController.popBackStack<InspectionList>(false) },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        // 工具送修
        composable<RepairList> {
            RepairListScreen(
                onBackClick = safeBack,
                onAddApplyClick = { navController.navigate(RepairScan) }
            )
        }
        composable<RepairScan> {
            RepairScanScreen(
                onBackClick = safeBack,
                onNextClick = { navController.navigate(RepairConfirm) },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        composable<RepairConfirm> {
            RepairConfirmScreen(
                onBackClick = safeBack,
                onConfirmClick = { navController.popBackStack<RepairList>(false) },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        // 工具报废
        composable<ScrapList> {
            ScrapListScreen(
                onBackClick = safeBack,
                onAddApplyClick = { navController.navigate(ScrapScan) }
            )
        }
        composable<ScrapScan> {
            ScrapScanScreen(
                onBackClick = safeBack,
                onNextClick = { navController.navigate(ScrapConfirm) },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        composable<ScrapConfirm> {
            ScrapConfirmScreen(
                onBackClick = safeBack,
                onConfirmClick = { navController.popBackStack<ScrapList>(false) },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        // 报废出库
        composable<ScrapOutList> {
            ScrapOutListScreen(
                onBackClick = safeBack,
                onCheckClick = { order ->
                    navController.navigate(order.toScrapOutCheck())
                }
            )
        }
        composable<ScrapOutCheck> { entry ->
            val route = entry.toRoute<ScrapOutCheck>()
            ScrapOutCheckScreen(
                order = ScrapOrder(
                    orderNo = route.orderNo,
                    applicant = route.applicant,
                    warehouse = route.warehouse,
                    toolCount = route.toolCount,
                    outboundStatus = route.outboundStatus
                ),
                onBackClick = safeBack,
                onNextClick = { navController.popBackStack<ScrapOutList>(false) },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        // 盘点管理
        composable<InventoryList> {
            InventoryListScreen(
                onBackClick = safeBack,
                onStartInventoryClick = { record ->
                    navController.navigate(record.toInventoryScan())
                }
            )
        }
        composable<InventoryScan> { entry ->
            InventoryScanScreen(
                order = entry.toRoute<InventoryScan>(),
                onBackClick = safeBack,
                onNextClick = { navController.navigate(InventoryResult) },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        composable<InventoryResult> {
            InventoryResultScreen(
                onBackClick = safeBack,
                onConfirmClick = { navController.popBackStack<InventoryList>(false) },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
    }
}