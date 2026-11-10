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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.hxb.myandroidapp_1021.data.models.ScrapOrder
import com.hxb.myandroidapp_1021.ui.viewmodel.PickingScanState

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
            _root_ide_package_.com.hxb.myandroidapp_1021.ui.pages.DashboardScreen(
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
            _root_ide_package_.com.hxb.myandroidapp_1021.ui.pages.LoginScreen(
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
            _root_ide_package_.com.hxb.myandroidapp_1021.ui.pages.ServerConfigScreen(
                onBackClick = safeBack,
                onSaveClick = safeBack
            )
        }
        // 借出
        composable<PickingList> {
            _root_ide_package_.com.hxb.myandroidapp_1021.ui.pages.PickingListScreen(
                onBackClick = safeBack,
                onStartPickClick = {
                    navController.navigate(PickingScan) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable<PickingScan> { entry ->
            _root_ide_package_.com.hxb.myandroidapp_1021.ui.pages.PickingScanScreen(
                state = viewModel<PickingScanState>(entry),
                onManualClick = {
                    navController.navigate(PickingManual) {
                        launchSingleTop = true
                    }
                },
                onBackClick = safeBack,
                onNextClick = { navController.navigate(PickingConfirm) { launchSingleTop = true } },
                onSettingsClick = {},
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        composable<PickingManual> { entry ->
            val scanEntry = remember(entry) { navController.getBackStackEntry<PickingScan>() }
            _root_ide_package_.com.hxb.myandroidapp_1021.ui.pages.PickingByMan(
                state = viewModel<PickingScanState>(scanEntry),
                onBackClick = safeBack,
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        composable<PickingConfirm> { entry ->
            val scanEntry = remember(entry) { navController.getBackStackEntry<PickingScan>() }
            _root_ide_package_.com.hxb.myandroidapp_1021.ui.pages.PickingConfirmScreen(
                state = viewModel<PickingScanState>(scanEntry),
                onBackClick = safeBack,
                onConfirmClick = { navController.popBackStack<PickingList>(false) },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        // 归还
        composable<ReturnList> {
            _root_ide_package_.com.hxb.myandroidapp_1021.ui.pages.ReturnListScreen(
                onBackClick = safeBack,
                onStartReturnClick = { order ->
                    navController.navigate(order.toReturnScan()) { launchSingleTop = true }
                }
            )
        }
        composable<ReturnScan> { entry ->
            val route = entry.toRoute<ReturnScan>()
            _root_ide_package_.com.hxb.myandroidapp_1021.ui.pages.ReturnScanScreen(
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
            _root_ide_package_.com.hxb.myandroidapp_1021.ui.pages.ReturnConfirmScreen(
                order = route,
                onBackClick = safeBack,
                onConfirmClick = { navController.popBackStack<ReturnList>(false) },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        // 工具送检
        composable<InspectionList> {
            _root_ide_package_.com.hxb.myandroidapp_1021.ui.pages.InspectionListScreen(
                onBackClick = safeBack,
                onAddApplyClick = { navController.navigate(InspectionScan) }
            )
        }
        composable<InspectionScan> {
            _root_ide_package_.com.hxb.myandroidapp_1021.ui.pages.InspectionScanScreen(
                onBackClick = safeBack,
                onNextClick = { navController.navigate(InspectionConfirm) },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        composable<InspectionConfirm> {
            _root_ide_package_.com.hxb.myandroidapp_1021.ui.pages.InspectionConfirmScreen(
                onBackClick = safeBack,
                onConfirmClick = { navController.popBackStack<InspectionList>(false) },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        // 工具送修
        composable<RepairList> {
            _root_ide_package_.com.hxb.myandroidapp_1021.ui.pages.RepairListScreen(
                onBackClick = safeBack,
                onAddApplyClick = { navController.navigate(RepairScan) }
            )
        }
        composable<RepairScan> {
            _root_ide_package_.com.hxb.myandroidapp_1021.ui.pages.RepairScanScreen(
                onBackClick = safeBack,
                onNextClick = { navController.navigate(RepairConfirm) },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        composable<RepairConfirm> {
            _root_ide_package_.com.hxb.myandroidapp_1021.ui.pages.RepairConfirmScreen(
                onBackClick = safeBack,
                onConfirmClick = { navController.popBackStack<RepairList>(false) },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        // 工具报废
        composable<ScrapList> {
            _root_ide_package_.com.hxb.myandroidapp_1021.ui.pages.ScrapListScreen(
                onBackClick = safeBack,
                onAddApplyClick = { navController.navigate(ScrapScan) }
            )
        }
        composable<ScrapScan> {
            _root_ide_package_.com.hxb.myandroidapp_1021.ui.pages.ScrapScanScreen(
                onBackClick = safeBack,
                onNextClick = { navController.navigate(ScrapConfirm) },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        composable<ScrapConfirm> {
            _root_ide_package_.com.hxb.myandroidapp_1021.ui.pages.ScrapConfirmScreen(
                onBackClick = safeBack,
                onConfirmClick = { navController.popBackStack<ScrapList>(false) },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        // 报废出库
        composable<ScrapOutList> {
            _root_ide_package_.com.hxb.myandroidapp_1021.ui.pages.ScrapOutListScreen(
                onBackClick = safeBack,
                onCheckClick = { order ->
                    navController.navigate(order.toScrapOutCheck())
                }
            )
        }
        composable<ScrapOutCheck> { entry ->
            val route = entry.toRoute<ScrapOutCheck>()
            _root_ide_package_.com.hxb.myandroidapp_1021.ui.pages.ScrapOutCheckScreen(
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
            _root_ide_package_.com.hxb.myandroidapp_1021.ui.pages.InventoryListScreen(
                onBackClick = safeBack,
                onStartInventoryClick = { record ->
                    navController.navigate(record.toInventoryScan())
                }
            )
        }
        composable<InventoryScan> { entry ->
            _root_ide_package_.com.hxb.myandroidapp_1021.ui.pages.InventoryScanScreen(
                order = entry.toRoute<InventoryScan>(),
                onBackClick = safeBack,
                onNextClick = { navController.navigate(InventoryResult) },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        composable<InventoryResult> {
            _root_ide_package_.com.hxb.myandroidapp_1021.ui.pages.InventoryResultScreen(
                onBackClick = safeBack,
                onConfirmClick = { navController.popBackStack<InventoryList>(false) },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
    }
}
