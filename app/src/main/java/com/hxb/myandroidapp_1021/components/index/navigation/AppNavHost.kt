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
import com.hxb.myandroidapp_1021.components.index.InspectionConfirmScreen
import com.hxb.myandroidapp_1021.components.index.InspectionListScreen
import com.hxb.myandroidapp_1021.components.index.InspectionScanScreen
import com.hxb.myandroidapp_1021.components.index.InventoryListScreen
import com.hxb.myandroidapp_1021.components.index.InventoryResultScreen
import com.hxb.myandroidapp_1021.components.index.InventoryScanScreen
import com.hxb.myandroidapp_1021.components.index.LoginScreen
import com.hxb.myandroidapp_1021.components.index.PickingConfirmScreen
import com.hxb.myandroidapp_1021.components.index.PickingListScreen
import com.hxb.myandroidapp_1021.components.index.PickingOrder
import com.hxb.myandroidapp_1021.components.index.PickingScanScreen
import com.hxb.myandroidapp_1021.components.index.RepairConfirmScreen
import com.hxb.myandroidapp_1021.components.index.RepairListScreen
import com.hxb.myandroidapp_1021.components.index.RepairScanScreen
import com.hxb.myandroidapp_1021.components.index.ReturnListScreen
import com.hxb.myandroidapp_1021.components.index.ReturnOrder
import com.hxb.myandroidapp_1021.components.index.ReturnScanScreen
import com.hxb.myandroidapp_1021.components.index.ReturnStatus
import com.hxb.myandroidapp_1021.components.index.ScrapConfirmScreen
import com.hxb.myandroidapp_1021.components.index.ScrapListScreen
import com.hxb.myandroidapp_1021.components.index.ScrapOrder
import com.hxb.myandroidapp_1021.components.index.ScrapOutCheckScreen
import com.hxb.myandroidapp_1021.components.index.ScrapOutListScreen
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
    // 送检·申请（读取工器具），二级路由
    const val INSPECT_APPLY = "inspection_apply"
    // 送检·填写信息，三级路由
    const val INSPECT_CONFIRM = "inspection_confirm"
    const val REPAIR = "repair"
    // 送修·申请（读取工器具），二级路由
    const val REPAIR_APPLY = "repair_apply"
    // 送修·填写信息，三级路由
    const val REPAIR_CONFIRM = "repair_confirm"
    const val SCRAP = "scrap"
    // 报废·申请（读取工器具），二级路由
    const val SCRAP_APPLY = "scrap_apply"
    // 报废·填写信息，三级路由
    const val SCRAP_CONFIRM = "scrap_confirm"
    const val SCRAP_OUT = "scrap_out"
    // 报废出库·核对，二级路由
    const val SCRAP_OUT_CHECK =
        "scrap_out_check/{orderNo}/{applicant}/{warehouse}/{toolCount}/{outboundStatus}"

    fun scrapOutCheck(order: ScrapOrder): String =
        "scrap_out_check" +
                "/${Uri.encode(order.orderNo)}" +
                "/${Uri.encode(order.applicant)}" +
                "/${Uri.encode(order.warehouse)}" +
                "/${order.toolCount}" +
                "/${Uri.encode(order.outboundStatus)}"
    const val INVENTORY = "inventory"
    // 盘点·读取盘点（二级路由）
    const val INVENTORY_SCAN = "inventory_scan"
    // 盘点·盘点结果（三级路由）
    const val INVENTORY_RESULT = "inventory_result"

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

    const val RETURN_SCAN =
        "return_scan/{orderNo}/{status}/{team}/{person}/{totalCount}/{returnedCount}/{unreturnedCount}"

    fun returnScan(order: ReturnOrder): String =
        "return_scan" +
                "/${Uri.encode(order.orderNo)}" +
                "/${Uri.encode(order.status.name)}" +
                "/${Uri.encode(order.team)}" +
                "/${Uri.encode(order.person)}" +
                "/${order.totalCount}" +
                "/${order.returnedCount}" +
                "/${order.unreturnedCount}"
}

private val RETURN_ARGS = listOf(
    navArgument("orderNo") { type = NavType.StringType },
    navArgument("status") { type = NavType.StringType },
    navArgument("team") { type = NavType.StringType },
    navArgument("person") { type = NavType.StringType },
    navArgument("totalCount") { type = NavType.IntType },
    navArgument("returnedCount") { type = NavType.IntType },
    navArgument("unreturnedCount") { type = NavType.IntType }
)

private fun buildReturnOrder(args: Bundle?): ReturnOrder = ReturnOrder(
    orderNo = args?.getString("orderNo").orEmpty(),
    team = args?.getString("team").orEmpty(),
    person = args?.getString("person").orEmpty(),
    totalCount = args?.getInt("totalCount") ?: 0,
    returnedCount = args?.getInt("returnedCount") ?: 0,
    unreturnedCount = args?.getInt("unreturnedCount") ?: 0,
    status = runCatching {
        ReturnStatus.valueOf(args?.getString("status").orEmpty())
    }.getOrDefault(ReturnStatus.PARTIAL)
)

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
                onConfirmClick = { navController.popBackStack(Routes.PICKING, false) }
            )
        }
        // 归还
        composable(Routes.RETURN_TOOL) {
            ReturnListScreen(
                onBackClick = safeBack,
                onStartReturnClick = { order ->
                    navController.navigate(Routes.returnScan(order))
                }
            )
        }
        composable(
            route = Routes.RETURN_SCAN,
            arguments = RETURN_ARGS
        ) { entry ->
            ReturnScanScreen(
                order = buildReturnOrder(entry.arguments),
                onBackClick = safeBack,
                onCompleteClick = { navController.popBackStack(Routes.RETURN_TOOL, false) },
                onHomeClick = { navController.popBackStack(Routes.DASHBOARD, false) }
            )
        }
        // 工具送检·申请单列表（一级路由）
        composable(Routes.INSPECT) {
            InspectionListScreen(
                onBackClick = safeBack,
                onAddApplyClick = { navController.navigate(Routes.INSPECT_APPLY) }
            )
        }
        // 工具送检·申请（读取工器具，二级路由）
        composable(Routes.INSPECT_APPLY) {
            InspectionScanScreen(
                onBackClick = safeBack,
                onNextClick = { navController.navigate(Routes.INSPECT_CONFIRM) },
                onHomeClick = { navController.popBackStack(Routes.DASHBOARD, false) }
            )
        }
        // 工具送检·填写信息（三级路由）
        composable(Routes.INSPECT_CONFIRM) {
            InspectionConfirmScreen(
                onBackClick = safeBack,
                onConfirmClick = { navController.popBackStack(Routes.INSPECT, false) }
            )
        }
        // 工具送修·申请单列表（一级路由）
        composable(Routes.REPAIR) {
            RepairListScreen(
                onBackClick = safeBack,
                onAddApplyClick = { navController.navigate(Routes.REPAIR_APPLY) }
            )
        }
        // 工具送修·申请（读取工器具，二级路由）
        composable(Routes.REPAIR_APPLY) {
            RepairScanScreen(
                onBackClick = safeBack,
                onNextClick = { navController.navigate(Routes.REPAIR_CONFIRM) },
                onHomeClick = { navController.popBackStack(Routes.DASHBOARD, false) }
            )
        }
        // 工具送修·填写信息（三级路由）
        composable(Routes.REPAIR_CONFIRM) {
            RepairConfirmScreen(
                onBackClick = safeBack,
                onConfirmClick = { navController.popBackStack(Routes.REPAIR, false) }
            )
        }
        // 工具报废·申请单列表（一级路由）
        composable(Routes.SCRAP) {
            ScrapListScreen(
                onBackClick = safeBack,
                onAddApplyClick = { navController.navigate(Routes.SCRAP_APPLY) }
            )
        }
        // 工具报废·申请（读取工器具，二级路由）
        composable(Routes.SCRAP_APPLY) {
            ScrapScanScreen(
                onBackClick = safeBack,
                onNextClick = { navController.navigate(Routes.SCRAP_CONFIRM) },
                onHomeClick = { navController.popBackStack(Routes.DASHBOARD, false) }
            )
        }
        // 工具报废·填写信息（三级路由）
        composable(Routes.SCRAP_CONFIRM) {
            ScrapConfirmScreen(
                onBackClick = safeBack,
                onConfirmClick = { navController.popBackStack(Routes.SCRAP, false) }
            )
        }
        // 报废出库·出库单列表
        composable(Routes.SCRAP_OUT) {
            ScrapOutListScreen(
                onBackClick = safeBack,
                onCheckClick = { order ->
                    navController.navigate(Routes.scrapOutCheck(order))
                }
            )
        }
        // 报废出库·核对（读取并核对要出库的工器具）
        composable(
            route = Routes.SCRAP_OUT_CHECK,
            arguments = listOf(
                navArgument("orderNo") { type = NavType.StringType },
                navArgument("applicant") { type = NavType.StringType },
                navArgument("warehouse") { type = NavType.StringType },
                navArgument("toolCount") { type = NavType.IntType },
                navArgument("outboundStatus") { type = NavType.StringType }
            )
        ) { entry ->
            val args = entry.arguments
            ScrapOutCheckScreen(
                order = ScrapOrder(
                    orderNo = args?.getString("orderNo").orEmpty(),
                    applicant = args?.getString("applicant").orEmpty(),
                    warehouse = args?.getString("warehouse").orEmpty(),
                    toolCount = args?.getInt("toolCount") ?: 0,
                    outboundStatus = args?.getString("outboundStatus").orEmpty()
                ),
                onBackClick = safeBack,
                onNextClick = { navController.popBackStack(Routes.SCRAP_OUT, false) },
                onHomeClick = { navController.popBackStack(Routes.DASHBOARD, false) }
            )
        }
        // 盘点管理·盘点任务单列表（一级路由）
        composable(Routes.INVENTORY) {
            InventoryListScreen(
                onBackClick = safeBack,
                onAddApplyClick = { navController.navigate(Routes.INVENTORY_SCAN) }
            )
        }
        // 盘点管理·读取盘点（二级路由）
        composable(Routes.INVENTORY_SCAN) {
            InventoryScanScreen(
                onBackClick = safeBack,
                onNextClick = { navController.navigate(Routes.INVENTORY_RESULT) },
                onHomeClick = { navController.popBackStack(Routes.DASHBOARD, false) }
            )
        }
        // 盘点管理·盘点结果（三级路由）
        composable(Routes.INVENTORY_RESULT) {
            InventoryResultScreen(
                onBackClick = safeBack,
                onConfirmClick = { navController.popBackStack(Routes.INVENTORY, false) }
            )
        }
    }
}