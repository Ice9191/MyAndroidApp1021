package com.hxb.myandroidapp_1021.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hxb.myandroidapp_1021.feature.dashboard.presentation.DashboardScreen
import com.hxb.myandroidapp_1021.feature.login.presentation.LoginScreen
import com.hxb.myandroidapp_1021.feature.overduealert.presentation.OverdueAlertListScreen
import com.hxb.myandroidapp_1021.feature.otherpicking.presentation.OtherPickingListScreen
import com.hxb.myandroidapp_1021.feature.otherpicking.presentation.OtherPickingListViewModel
import com.hxb.myandroidapp_1021.feature.otherpicking.presentation.OtherPickingScanScreen
import com.hxb.myandroidapp_1021.feature.otherpicking.presentation.OtherPickingScanViewModel
import com.hxb.myandroidapp_1021.feature.otherputaway.presentation.OtherPutawayListScreen
import com.hxb.myandroidapp_1021.feature.otherputaway.presentation.OtherPutawayListViewModel
import com.hxb.myandroidapp_1021.feature.otherputaway.presentation.OtherPutawayScanScreen
import com.hxb.myandroidapp_1021.feature.otherputaway.presentation.OtherPutawayScanViewModel
import com.hxb.myandroidapp_1021.feature.picking.presentation.PickingListScreen
import com.hxb.myandroidapp_1021.feature.picking.presentation.PickingListViewModel
import com.hxb.myandroidapp_1021.feature.picking.presentation.PickingScanScreen
import com.hxb.myandroidapp_1021.feature.picking.presentation.PickingScanViewModel
import com.hxb.myandroidapp_1021.feature.putaway.presentation.PutawayConfirmScreen
import com.hxb.myandroidapp_1021.feature.putaway.presentation.PutawayListScreen
import com.hxb.myandroidapp_1021.feature.putaway.presentation.PutawayListViewModel
import com.hxb.myandroidapp_1021.feature.returns.presentation.ReturnListScreen
import com.hxb.myandroidapp_1021.feature.returns.presentation.ReturnListViewModel
import com.hxb.myandroidapp_1021.feature.returns.presentation.ReturnScanScreen
import com.hxb.myandroidapp_1021.feature.returns.presentation.ReturnScanViewModel
import com.hxb.myandroidapp_1021.feature.serverconfig.presentation.ServerConfigScreen
import com.hxb.myandroidapp_1021.feature.tempborrow.presentation.TempBorrowConfirmScreen
import com.hxb.myandroidapp_1021.feature.tempborrow.presentation.TempBorrowScreen
import com.hxb.myandroidapp_1021.feature.tempborrow.presentation.TempBorrowSearchToolsScreen
import com.hxb.myandroidapp_1021.feature.tempborrow.presentation.TempBorrowViewModel
import com.hxb.myandroidapp_1021.feature.tempreturn.presentation.TempReturnConfirmScreen
import com.hxb.myandroidapp_1021.feature.tempreturn.presentation.TempReturnScreen
import com.hxb.myandroidapp_1021.feature.tempreturn.presentation.TempReturnSearchToolsScreen
import com.hxb.myandroidapp_1021.feature.tempreturn.presentation.TempReturnViewModel
import com.hxb.myandroidapp_1021.feature.stockdetail.presentation.StockDetailListScreen

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
            .background(MaterialTheme.colorScheme.background),
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
                },
                onSettingsClick = {
                    navController.navigate(ServerConfig) { launchSingleTop = true }
                }
            )
        }
        // 超期告警仅查询展示，不在 PDA 配置参数或处理告警。
        composable<OverdueAlertList> {
            OverdueAlertListScreen(onBackClick = safeBack)
        }
        composable<StockDetailList> {
            StockDetailListScreen(onBackClick = safeBack)
        }
        // 服务器配置页
        composable<ServerConfig> {
            ServerConfigScreen(
                onBackClick = safeBack,
                onSaveClick = safeBack
            )
        }
        // 上架
        composable<PutawayList> { entry ->
            val flowViewModel = hiltViewModel<PutawayListViewModel>(entry)
            PutawayListScreen(
                flowViewModel = flowViewModel,
                onBackClick = safeBack,
                onStartPutawayClick = {
                    navController.navigate(PutawayConfirm) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable<PutawayConfirm> { entry ->
            val listEntry = remember(entry) { navController.getBackStackEntry<PutawayList>() }
            PutawayConfirmScreen(
                flowViewModel = hiltViewModel<PutawayListViewModel>(listEntry),
                onBackClick = safeBack,
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        // 借出
        composable<PickingList> { entry ->
            PickingListScreen(
                flowViewModel = hiltViewModel<PickingListViewModel>(entry),
                scanViewModel = hiltViewModel<PickingScanViewModel>(entry),
                onBackClick = safeBack,
                onStartPickClick = {
                    navController.navigate(PickingScan) { launchSingleTop = true }
                }
            )
        }
        composable<PickingScan> { entry ->
            val listEntry = remember(entry) { navController.getBackStackEntry<PickingList>() }
            PickingScanScreen(
                viewModel = hiltViewModel<PickingScanViewModel>(listEntry),
                flowViewModel = hiltViewModel<PickingListViewModel>(listEntry),
                onBackClick = safeBack,
                onSubmittedClick = { navController.popBackStack<PickingList>(false) },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        // 归还
        composable<ReturnList> { entry ->
            ReturnListScreen(
                flowViewModel = hiltViewModel<ReturnListViewModel>(entry),
                scanViewModel = hiltViewModel<ReturnScanViewModel>(entry),
                onBackClick = safeBack,
                onStartReturnClick = {
                    navController.navigate(ReturnScan) { launchSingleTop = true }
                }
            )
        }
        composable<ReturnScan> { entry ->
            val listEntry = remember(entry) { navController.getBackStackEntry<ReturnList>() }
            ReturnScanScreen(
                viewModel = hiltViewModel<ReturnScanViewModel>(listEntry),
                flowViewModel = hiltViewModel<ReturnListViewModel>(listEntry),
                onBackClick = safeBack,
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        // 临时借还与其他作业共享首页作用域的任务列表，返回首页保留进度，退出登录后随首页销毁。
        composable<TempBorrow> { entry ->
            val dashboardEntry = remember(entry) { navController.getBackStackEntry<Dashboard>() }
            val borrowViewModel = hiltViewModel<TempBorrowViewModel>(dashboardEntry)
            TempBorrowScreen(
                viewModel = borrowViewModel,
                onBackClick = safeBack,
                onSearchToolsClick = {
                    borrowViewModel.onToolQueryChange("")
                    navController.navigate(TempBorrowSearchTools) {
                        launchSingleTop = true
                    }
                },
                onSelectOrder = { orderNo ->
                    borrowViewModel.selectOrder(orderNo)
                    navController.navigate(TempBorrowConfirm) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable<TempBorrowSearchTools> { entry ->
            val dashboardEntry = remember(entry) { navController.getBackStackEntry<Dashboard>() }
            val borrowViewModel = hiltViewModel<TempBorrowViewModel>(dashboardEntry)
            TempBorrowSearchToolsScreen(
                viewModel = borrowViewModel,
                onBackClick = safeBack,
                onHomeClick = {
                    borrowViewModel.showList()
                    navController.popBackStack<Dashboard>(false)
                }
            )
        }
        composable<TempBorrowConfirm> { entry ->
            val dashboardEntry = remember(entry) { navController.getBackStackEntry<Dashboard>() }
            val borrowViewModel = hiltViewModel<TempBorrowViewModel>(dashboardEntry)
            TempBorrowConfirmScreen(
                flowViewModel = borrowViewModel,
                onBackClick = safeBack,
                onHomeClick = {
                    borrowViewModel.showList()
                    navController.popBackStack<Dashboard>(false)
                }
            )
        }
        composable<TempReturn> { entry ->
            val dashboardEntry = remember(entry) { navController.getBackStackEntry<Dashboard>() }
            val borrowViewModel = hiltViewModel<TempBorrowViewModel>(dashboardEntry)
            val returnViewModel = hiltViewModel<TempReturnViewModel>(dashboardEntry)
            val borrowState by borrowViewModel.uiState.collectAsStateWithLifecycle()
            LaunchedEffect(borrowState.orders) {
                returnViewModel.syncBorrowedTools(borrowState.orders)
            }
            TempReturnScreen(
                viewModel = returnViewModel,
                onBackClick = safeBack,
                onSearchToolsClick = {
                    returnViewModel.onToolQueryChange("")
                    navController.navigate(TempReturnSearchTools) {
                        launchSingleTop = true
                    }
                },
                onSelectOrder = { orderNo ->
                    returnViewModel.selectOrder(orderNo)
                    navController.navigate(TempReturnConfirm) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable<TempReturnSearchTools> { entry ->
            val dashboardEntry = remember(entry) { navController.getBackStackEntry<Dashboard>() }
            val returnViewModel = hiltViewModel<TempReturnViewModel>(dashboardEntry)
            TempReturnSearchToolsScreen(
                viewModel = returnViewModel,
                onBackClick = safeBack,
                onHomeClick = {
                    returnViewModel.showList()
                    navController.popBackStack<Dashboard>(false)
                }
            )
        }
        composable<TempReturnConfirm> { entry ->
            val dashboardEntry = remember(entry) { navController.getBackStackEntry<Dashboard>() }
            val returnViewModel = hiltViewModel<TempReturnViewModel>(dashboardEntry)
            TempReturnConfirmScreen(
                flowViewModel = returnViewModel,
                onBackClick = safeBack,
                onHomeClick = {
                    returnViewModel.showList()
                    navController.popBackStack<Dashboard>(false)
                }
            )
        }
        // 其他作业的核对记录保存在列表作用域；临时借出/归还生成的临时任务进入列表时由首页作用域的借还页面写入。
        composable<OtherPickingList> { entry ->
            val dashboardEntry = remember(entry) { navController.getBackStackEntry<Dashboard>() }
            val listViewModel = hiltViewModel<OtherPickingListViewModel>(entry)
            val borrowViewModel = hiltViewModel<TempBorrowViewModel>(dashboardEntry)
            val taskState by listViewModel.uiState.collectAsStateWithLifecycle()
            LaunchedEffect(Unit) {
                borrowViewModel.pickingTasks().forEach(listViewModel::upsertTemporaryOrder)
            }
            LaunchedEffect(taskState.orders) { borrowViewModel.syncPicking(taskState.orders) }
            OtherPickingListScreen(
                viewModel = listViewModel,
                scanViewModel = hiltViewModel<OtherPickingScanViewModel>(entry),
                onBackClick = safeBack,
                onStartClick = {
                    navController.navigate(OtherPickingScan) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable<OtherPickingScan> { entry ->
            val listEntry = remember(entry) { navController.getBackStackEntry<OtherPickingList>() }
            OtherPickingScanScreen(
                viewModel = hiltViewModel<OtherPickingScanViewModel>(listEntry),
                listViewModel = hiltViewModel<OtherPickingListViewModel>(listEntry),
                onBackClick = safeBack,
                onSubmittedClick = { navController.popBackStack<OtherPickingList>(false) },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        composable<OtherPutawayList> { entry ->
            val dashboardEntry = remember(entry) { navController.getBackStackEntry<Dashboard>() }
            val listViewModel = hiltViewModel<OtherPutawayListViewModel>(entry)
            val returnViewModel = hiltViewModel<TempReturnViewModel>(dashboardEntry)
            val taskState by listViewModel.uiState.collectAsStateWithLifecycle()
            LaunchedEffect(Unit) {
                returnViewModel.putawayTasks().forEach(listViewModel::upsertTemporaryOrder)
            }
            LaunchedEffect(taskState.orders) { returnViewModel.syncPutaway(taskState.orders) }
            OtherPutawayListScreen(
                viewModel = listViewModel,
                scanViewModel = hiltViewModel<OtherPutawayScanViewModel>(entry),
                onBackClick = safeBack,
                onStartClick = {
                    navController.navigate(OtherPutawayScan) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable<OtherPutawayScan> { entry ->
            val listEntry = remember(entry) { navController.getBackStackEntry<OtherPutawayList>() }
            OtherPutawayScanScreen(
                viewModel = hiltViewModel<OtherPutawayScanViewModel>(listEntry),
                listViewModel = hiltViewModel<OtherPutawayListViewModel>(listEntry),
                onBackClick = safeBack,
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
    }
}
