package com.hxb.myandroidapp_1021.components.index.navigation

import android.net.Uri
import android.os.Bundle
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hxb.myandroidapp_1021.components.index.DashboardScreen
import com.hxb.myandroidapp_1021.components.index.InspectionScanScreen
import com.hxb.myandroidapp_1021.components.index.InventoryScanScreen
import com.hxb.myandroidapp_1021.components.index.LoginScreen
import com.hxb.myandroidapp_1021.components.index.PickingConfirmScreen
import com.hxb.myandroidapp_1021.components.index.PickingListScreen
import com.hxb.myandroidapp_1021.components.index.PickingOrder
import com.hxb.myandroidapp_1021.components.index.PickingScanScreen
import com.hxb.myandroidapp_1021.components.index.RepairScanScreen
import com.hxb.myandroidapp_1021.components.index.ReturnListScreen
import com.hxb.myandroidapp_1021.components.index.ScrapListScreen
import com.hxb.myandroidapp_1021.components.index.ScrapScanScreen
import com.hxb.myandroidapp_1021.components.index.ServerConfigScreen

object Routes {
    const val DASHBOARD = "dashboard"
    const val LOGIN = "login"
    const val SERVER_CONFIG = "server_config"

    // 一级路由
    const val PICKING = "picking"
    const val RETURN_TOOL = "return_tool"
    const val INSPECT = "inspect"
    const val REPAIR = "repair"
    const val SCRAP = "scrap"
    const val SCRAP_OUT = "scrap_out"
    const val INVENTORY = "inventory"

    // 二级路由
    const val PICKING_SCAN =
        "picking_scan/{orderNo}/{status}/{team}/{person}/{toolCount}/{expectedTime}"

    // 三级路由
    const val PICKING_CONFIRM =
        "picking_confirm/{orderNo}/{status}/{team}/{person}/{toolCount}/{expectedTime}"

    fun pickingScan(order: PickingOrder): String = orderRoute("picking_scan", order)

    fun pickingConfirm(order: PickingOrder): String = orderRoute("picking_confirm", order)

    private fun orderRoute(prefix: String, order: PickingOrder): String =
        prefix +
                "/${Uri.encode(order.orderNo)}" +
                "/${Uri.encode(order.status)}" +
                "/${Uri.encode(order.team)}" +
                "/${Uri.encode(order.person)}" +
                "/${order.toolCount}" +
                "/${Uri.encode(order.expectedTime)}"
}

private val ORDER_ARGS = listOf(
    navArgument("orderNo") { type = NavType.StringType },
    navArgument("status") { type = NavType.StringType },
    navArgument("team") { type = NavType.StringType },
    navArgument("person") { type = NavType.StringType },
    navArgument("toolCount") { type = NavType.IntType },
    navArgument("expectedTime") { type = NavType.StringType }
)

private fun buildPickingOrder(args: Bundle?): PickingOrder = PickingOrder(
    orderNo = args?.getString("orderNo").orEmpty(),
    team = args?.getString("team").orEmpty(),
    person = args?.getString("person").orEmpty(),
    toolCount = args?.getInt("toolCount") ?: 0,
    expectedTime = args?.getString("expectedTime").orEmpty(),
    status = args?.getString("status").orEmpty()
)

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    val safeBack: () -> Unit = {
        if (navController.previousBackStackEntry != null) {
            navController.popBackStack()
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.DASHBOARD,
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
        composable(
            Routes.DASHBOARD,
            enterTransition = { EnterTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            DashboardScreen(
                onLogoutClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.DASHBOARD) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onSettingsClick = {
                    navController.navigate(Routes.SERVER_CONFIG) { launchSingleTop = true }
                },
                onMenuClick = { route ->
                    navController.navigate(route) { launchSingleTop = true }
                }
            )
        }
        // 登录页
        composable(
            Routes.LOGIN,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        // 服务器配置页
        composable(Routes.SERVER_CONFIG) {
            ServerConfigScreen(
                onBackClick = safeBack,
                onSaveClick = safeBack
            )
        }
        // 借出
        composable(Routes.PICKING) {
            PickingListScreen(
                onBackClick = safeBack,
                onStartPickClick = { order ->
                    navController.navigate(Routes.pickingScan(order))
                }
            )
        }
        composable(
            route = Routes.PICKING_SCAN,
            arguments = ORDER_ARGS
        ) { entry ->
            val order = buildPickingOrder(entry.arguments)
            PickingScanScreen(
                order = order,
                onBackClick = safeBack,
                onNextClick = { navController.navigate(Routes.pickingConfirm(order)) },
                onSettingsClick = {
                    // TODO设置页跳转
                }
            )
        }
        composable(
            route = Routes.PICKING_CONFIRM,
            arguments = ORDER_ARGS
        ) { entry ->
            PickingConfirmScreen(
                order = buildPickingOrder(entry.arguments),
                onBackClick = safeBack,
                // 确认出库后回到借出列表
                onConfirmClick = { navController.popBackStack(Routes.PICKING, false) }
            )
        }
        // 归还
        composable(Routes.RETURN_TOOL) {
            ReturnListScreen(
                onBackClick = safeBack
            )
        }
        // 工具送检
        composable(Routes.INSPECT) {
            InspectionScanScreen(
                onBackClick = safeBack
            )
        }
        // 工具送修
        composable(Routes.REPAIR) {
            RepairScanScreen(
                onBackClick = safeBack
            )
        }
        // 工具报废
        composable(Routes.SCRAP) {
            ScrapScanScreen(
                onBackClick = safeBack,
                onCompleteClick = safeBack
            )
        }
        // 报废出库
        composable(Routes.SCRAP_OUT) {
            ScrapListScreen(
                onBackClick = safeBack
            )
        }
        // 盘点管理
        composable(Routes.INVENTORY) {
            InventoryScanScreen(
                onBackClick = safeBack
            )
        }
    }
}