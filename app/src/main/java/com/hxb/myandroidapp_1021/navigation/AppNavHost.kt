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
import com.hxb.myandroidapp_1021.ui.dashboard.DashboardScreen
import com.hxb.myandroidapp_1021.ui.inspection.InspectionConfirmScreen
import com.hxb.myandroidapp_1021.ui.inspection.InspectionListScreen
import com.hxb.myandroidapp_1021.ui.inspection.InspectionScanScreen
import com.hxb.myandroidapp_1021.ui.inventory.InventoryFlowViewModel
import com.hxb.myandroidapp_1021.ui.inventory.InventoryListScreen
import com.hxb.myandroidapp_1021.ui.inventory.InventoryResultScreen
import com.hxb.myandroidapp_1021.ui.inventory.InventoryScanScreen
import com.hxb.myandroidapp_1021.ui.login.LoginScreen
import com.hxb.myandroidapp_1021.ui.picking.PickingConfirmScreen
import com.hxb.myandroidapp_1021.ui.picking.PickingListScreen
import com.hxb.myandroidapp_1021.ui.picking.PickingManualScreen
import com.hxb.myandroidapp_1021.ui.picking.PickingScanScreen
import com.hxb.myandroidapp_1021.ui.picking.PickingScanViewModel
import com.hxb.myandroidapp_1021.ui.repair.RepairConfirmScreen
import com.hxb.myandroidapp_1021.ui.repair.RepairListScreen
import com.hxb.myandroidapp_1021.ui.repair.RepairScanScreen
import com.hxb.myandroidapp_1021.ui.returns.ReturnConfirmScreen
import com.hxb.myandroidapp_1021.ui.returns.ReturnListScreen
import com.hxb.myandroidapp_1021.ui.returns.ReturnManualScreen
import com.hxb.myandroidapp_1021.ui.returns.ReturnScanScreen
import com.hxb.myandroidapp_1021.ui.returns.ReturnScanViewModel
import com.hxb.myandroidapp_1021.ui.scrap.ScrapConfirmScreen
import com.hxb.myandroidapp_1021.ui.scrap.ScrapListScreen
import com.hxb.myandroidapp_1021.ui.scrap.ScrapScanScreen
import com.hxb.myandroidapp_1021.ui.scrapout.ScrapOutCheckScreen
import com.hxb.myandroidapp_1021.ui.scrapout.ScrapOutFlowViewModel
import com.hxb.myandroidapp_1021.ui.scrapout.ScrapOutListScreen
import com.hxb.myandroidapp_1021.ui.serverconfig.ServerConfigScreen

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
                onStartPickClick = {
                    navController.navigate(PickingScan) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable<PickingScan> { entry ->
            PickingScanScreen(
                viewModel = viewModel<PickingScanViewModel>(entry),
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
            PickingManualScreen(
                viewModel = viewModel<PickingScanViewModel>(scanEntry),
                onBackClick = safeBack,
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        composable<PickingConfirm> {
            PickingConfirmScreen(
                onBackClick = safeBack,
                onConfirmClick = { navController.popBackStack<PickingList>(false) },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        // 归还
        composable<ReturnList> { entry ->
            val scanViewModel = viewModel<ReturnScanViewModel>(entry)
            ReturnListScreen(
                onBackClick = safeBack,
                onStartReturnClick = { order ->
                    scanViewModel.selectOrder(order)
                    navController.navigate(ReturnScan) { launchSingleTop = true }
                }
            )
        }
        composable<ReturnScan> { entry ->
            val listEntry = remember(entry) { navController.getBackStackEntry<ReturnList>() }
            ReturnScanScreen(
                viewModel = viewModel<ReturnScanViewModel>(listEntry),
                onManualClick = { navController.navigate(ReturnManual) { launchSingleTop = true } },
                onBackClick = safeBack,
                onNextClick = { navController.navigate(ReturnConfirm) { launchSingleTop = true } },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        composable<ReturnManual> { entry ->
            val listEntry = remember(entry) { navController.getBackStackEntry<ReturnList>() }
            ReturnManualScreen(
                viewModel = viewModel<ReturnScanViewModel>(listEntry),
                onBackClick = safeBack,
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        composable<ReturnConfirm> { entry ->
            val listEntry = remember(entry) { navController.getBackStackEntry<ReturnList>() }
            ReturnConfirmScreen(
                viewModel = viewModel<ReturnScanViewModel>(listEntry),
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
        composable<ScrapOutList> { entry ->
            val flowViewModel = viewModel<ScrapOutFlowViewModel>(entry)
            ScrapOutListScreen(
                onBackClick = safeBack,
                onCheckClick = { order ->
                    flowViewModel.selectOrder(order)
                    navController.navigate(ScrapOutCheck)
                }
            )
        }
        composable<ScrapOutCheck> { entry ->
            val listEntry = remember(entry) { navController.getBackStackEntry<ScrapOutList>() }
            ScrapOutCheckScreen(
                viewModel = viewModel<ScrapOutFlowViewModel>(listEntry),
                onBackClick = safeBack,
                onNextClick = { navController.popBackStack<ScrapOutList>(false) },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        // 盘点管理
        composable<InventoryList> { entry ->
            val flowViewModel = viewModel<InventoryFlowViewModel>(entry)
            InventoryListScreen(
                onBackClick = safeBack,
                onStartInventoryClick = { record ->
                    flowViewModel.selectOrder(record)
                    navController.navigate(InventoryScan)
                }
            )
        }
        composable<InventoryScan> { entry ->
            val listEntry = remember(entry) { navController.getBackStackEntry<InventoryList>() }
            InventoryScanScreen(
                viewModel = viewModel<InventoryFlowViewModel>(listEntry),
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
