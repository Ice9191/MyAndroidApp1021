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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hxb.myandroidapp_1021.ui.dashboard.DashboardScreen
import com.hxb.myandroidapp_1021.ui.login.LoginScreen
import com.hxb.myandroidapp_1021.ui.otherpicking.OtherPickingConfirmScreen
import com.hxb.myandroidapp_1021.ui.otherpicking.OtherPickingListScreen
import com.hxb.myandroidapp_1021.ui.otherpicking.OtherPickingListViewModel
import com.hxb.myandroidapp_1021.ui.otherpicking.OtherPickingScanScreen
import com.hxb.myandroidapp_1021.ui.otherpicking.OtherPickingScanViewModel
import com.hxb.myandroidapp_1021.ui.otherputaway.OtherPutawayConfirmScreen
import com.hxb.myandroidapp_1021.ui.otherputaway.OtherPutawayListScreen
import com.hxb.myandroidapp_1021.ui.otherputaway.OtherPutawayListViewModel
import com.hxb.myandroidapp_1021.ui.otherputaway.OtherPutawayScanScreen
import com.hxb.myandroidapp_1021.ui.otherputaway.OtherPutawayScanViewModel
import com.hxb.myandroidapp_1021.ui.picking.PickingConfirmScreen
import com.hxb.myandroidapp_1021.ui.picking.PickingListScreen
import com.hxb.myandroidapp_1021.ui.picking.PickingManualScreen
import com.hxb.myandroidapp_1021.ui.picking.PickingScanScreen
import com.hxb.myandroidapp_1021.ui.picking.PickingScanViewModel
import com.hxb.myandroidapp_1021.ui.putaway.PutawayConfirmScreen
import com.hxb.myandroidapp_1021.ui.putaway.PutawayListScreen
import com.hxb.myandroidapp_1021.ui.putaway.PutawayListViewModel
import com.hxb.myandroidapp_1021.ui.returns.ReturnConfirmScreen
import com.hxb.myandroidapp_1021.ui.returns.ReturnListScreen
import com.hxb.myandroidapp_1021.ui.returns.ReturnManualScreen
import com.hxb.myandroidapp_1021.ui.returns.ReturnScanScreen
import com.hxb.myandroidapp_1021.ui.returns.ReturnScanViewModel
import com.hxb.myandroidapp_1021.ui.serverconfig.ServerConfigScreen
import com.hxb.myandroidapp_1021.ui.temp.TempBorrowScreen
import com.hxb.myandroidapp_1021.ui.temp.TempBorrowViewModel
import com.hxb.myandroidapp_1021.ui.temp.TempReturnScreen
import com.hxb.myandroidapp_1021.ui.temp.TempReturnViewModel

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
        // 上架
        composable<PutawayList> { entry ->
            val flowViewModel = viewModel<PutawayListViewModel>(entry)
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
                flowViewModel = viewModel<PutawayListViewModel>(listEntry),
                onBackClick = safeBack
            )
        }
        // 借出
        composable<PickingList> { entry ->
            val scanViewModel = viewModel<PickingScanViewModel>(entry)
            PickingListScreen(
                scanViewModel = scanViewModel,
                onBackClick = safeBack,
                onStartPickClick = {
                    navController.navigate(PickingScan) { launchSingleTop = true }
                }
            )
        }
        composable<PickingScan> { entry ->
            val listEntry = remember(entry) { navController.getBackStackEntry<PickingList>() }
            PickingScanScreen(
                viewModel = viewModel<PickingScanViewModel>(listEntry),
                onManualClick = {
                    navController.navigate(PickingManual) {
                        launchSingleTop = true
                    }
                },
                onBackClick = safeBack,
                onNextClick = { navController.navigate(PickingConfirm) { launchSingleTop = true } },
                onSettingsClick = {
                    navController.navigate(ServerConfig) {
                        launchSingleTop = true
                    }
                },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        composable<PickingManual> { entry ->
            val listEntry = remember(entry) { navController.getBackStackEntry<PickingList>() }
            PickingManualScreen(
                viewModel = viewModel<PickingScanViewModel>(listEntry),
                onBackClick = safeBack,
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        composable<PickingConfirm> { entry ->
            val listEntry = remember(entry) { navController.getBackStackEntry<PickingList>() }
            PickingConfirmScreen(
                scanViewModel = viewModel<PickingScanViewModel>(listEntry),
                onBackClick = safeBack,
                onConfirmClick = { navController.popBackStack<PickingList>(false) },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        // 归还
        composable<ReturnList> { entry ->
            val scanViewModel = viewModel<ReturnScanViewModel>(entry)
            ReturnListScreen(
                scanViewModel = scanViewModel,
                onBackClick = safeBack,
                onStartReturnClick = {
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
                onSettingsClick = {
                    navController.navigate(ServerConfig) {
                        launchSingleTop = true
                    }
                },
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
                scanViewModel = viewModel<ReturnScanViewModel>(listEntry),
                onBackClick = safeBack,
                onConfirmClick = { navController.popBackStack<ReturnList>(false) },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        // 临时借还与其他作业共享首页作用域的任务列表，返回首页保留进度，退出登录后随首页销毁。
        composable<TempBorrow> { entry ->
            val dashboardEntry = remember(entry) { navController.getBackStackEntry<Dashboard>() }
            val borrowViewModel = viewModel<TempBorrowViewModel>(dashboardEntry)
            val listViewModel = viewModel<OtherPickingListViewModel>(dashboardEntry)
            val taskState by listViewModel.uiState.collectAsStateWithLifecycle()
            val borrowState by borrowViewModel.uiState.collectAsStateWithLifecycle()
            LaunchedEffect(borrowState.orders) {
                borrowViewModel.pickingTasks().forEach(listViewModel::upsertTemporaryTask)
            }
            LaunchedEffect(taskState.tasks) { borrowViewModel.syncPicking(taskState.tasks) }
            TempBorrowScreen(
                viewModel = borrowViewModel,
                onBackClick = safeBack,
                onHomeClick = { navController.popBackStack<Dashboard>(false) },
                onPickingClick = {
                    borrowViewModel.pickingTask()?.let { task ->
                        listViewModel.openTemporaryTask(task)
                        navController.navigate(OtherPickingList) { launchSingleTop = true }
                    }
                }
            )
        }
        composable<TempReturn> { entry ->
            val dashboardEntry = remember(entry) { navController.getBackStackEntry<Dashboard>() }
            val borrowViewModel = viewModel<TempBorrowViewModel>(dashboardEntry)
            val returnViewModel = viewModel<TempReturnViewModel>(dashboardEntry)
            val listViewModel = viewModel<OtherPutawayListViewModel>(dashboardEntry)
            val borrowState by borrowViewModel.uiState.collectAsStateWithLifecycle()
            val returnState by returnViewModel.uiState.collectAsStateWithLifecycle()
            LaunchedEffect(borrowState.orders) { returnViewModel.syncBorrowedOrders(borrowState.orders) }
            LaunchedEffect(returnState.orders) {
                returnViewModel.putawayTasks().forEach(listViewModel::upsertTemporaryTask)
            }
            TempReturnScreen(
                viewModel = returnViewModel,
                onBackClick = safeBack,
                onHomeClick = { navController.popBackStack<Dashboard>(false) },
                onPutawayClick = {
                    returnViewModel.putawayTask()?.let { task ->
                        listViewModel.openTemporaryTask(task)
                        navController.navigate(OtherPutawayList) { launchSingleTop = true }
                    }
                }
            )
        }
        // 其他作业的核对记录仍保存在列表作用域，不通过路由传递业务数据。
        composable<OtherPickingList> { entry ->
            val dashboardEntry = remember(entry) { navController.getBackStackEntry<Dashboard>() }
            OtherPickingListScreen(
                viewModel = viewModel<OtherPickingListViewModel>(dashboardEntry),
                flowViewModel = viewModel<OtherPickingScanViewModel>(entry),
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
                viewModel = viewModel<OtherPickingScanViewModel>(listEntry),
                onBackClick = safeBack,
                onNextClick = {
                    navController.navigate(OtherPickingConfirm) {
                        launchSingleTop = true
                    }
                },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        composable<OtherPickingConfirm> { entry ->
            val listEntry = remember(entry) { navController.getBackStackEntry<OtherPickingList>() }
            val dashboardEntry = remember(entry) { navController.getBackStackEntry<Dashboard>() }
            OtherPickingConfirmScreen(
                flowViewModel = viewModel<OtherPickingScanViewModel>(listEntry),
                listViewModel = viewModel<OtherPickingListViewModel>(dashboardEntry),
                onBackClick = safeBack,
                onComplete = { navController.popBackStack<OtherPickingList>(false) },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        composable<OtherPutawayList> { entry ->
            val dashboardEntry = remember(entry) { navController.getBackStackEntry<Dashboard>() }
            OtherPutawayListScreen(
                viewModel = viewModel<OtherPutawayListViewModel>(dashboardEntry),
                flowViewModel = viewModel<OtherPutawayScanViewModel>(entry),
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
                viewModel = viewModel<OtherPutawayScanViewModel>(listEntry),
                onBackClick = safeBack,
                onNextClick = {
                    navController.navigate(OtherPutawayConfirm) {
                        launchSingleTop = true
                    }
                },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        composable<OtherPutawayConfirm> { entry ->
            val listEntry = remember(entry) { navController.getBackStackEntry<OtherPutawayList>() }
            val dashboardEntry = remember(entry) { navController.getBackStackEntry<Dashboard>() }
            OtherPutawayConfirmScreen(
                flowViewModel = viewModel<OtherPutawayScanViewModel>(listEntry),
                listViewModel = viewModel<OtherPutawayListViewModel>(dashboardEntry),
                onBackClick = safeBack,
                onComplete = { navController.popBackStack<OtherPutawayList>(false) },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
    }
}
