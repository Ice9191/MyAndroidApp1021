package com.hxb.myandroidapp_1021.ui.navigation

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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import com.hxb.myandroidapp_1021.pages.session.SessionViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hxb.myandroidapp_1021.pages.dashboard.DashboardScreen
import com.hxb.myandroidapp_1021.pages.login.LoginScreen
import com.hxb.myandroidapp_1021.pages.overduealert.OverdueAlertListScreen
import com.hxb.myandroidapp_1021.pages.otherpicking.OtherPickingConfirmScreen
import com.hxb.myandroidapp_1021.pages.otherpicking.OtherPickingListScreen
import com.hxb.myandroidapp_1021.pages.otherpicking.OtherPickingListViewModel
import com.hxb.myandroidapp_1021.pages.otherpicking.OtherPickingScanScreen
import com.hxb.myandroidapp_1021.pages.otherpicking.OtherPickingScanViewModel
import com.hxb.myandroidapp_1021.pages.otherputaway.OtherPutawayConfirmScreen
import com.hxb.myandroidapp_1021.pages.otherputaway.OtherPutawayListScreen
import com.hxb.myandroidapp_1021.pages.otherputaway.OtherPutawayListViewModel
import com.hxb.myandroidapp_1021.pages.otherputaway.OtherPutawayScanScreen
import com.hxb.myandroidapp_1021.pages.otherputaway.OtherPutawayScanViewModel
import com.hxb.myandroidapp_1021.pages.picking.PickingListScreen
import com.hxb.myandroidapp_1021.pages.picking.PickingListViewModel
import com.hxb.myandroidapp_1021.pages.picking.PickingScanScreen
import com.hxb.myandroidapp_1021.pages.picking.PickingScanViewModel
import com.hxb.myandroidapp_1021.pages.putaway.PutawayConfirmScreen
import com.hxb.myandroidapp_1021.pages.putaway.PutawayListScreen
import com.hxb.myandroidapp_1021.pages.putaway.PutawayListViewModel
import com.hxb.myandroidapp_1021.pages.returns.ReturnListScreen
import com.hxb.myandroidapp_1021.pages.returns.ReturnListViewModel
import com.hxb.myandroidapp_1021.pages.returns.ReturnScanScreen
import com.hxb.myandroidapp_1021.pages.returns.ReturnScanViewModel
import com.hxb.myandroidapp_1021.pages.serverconfig.ServerConfigScreen
import com.hxb.myandroidapp_1021.pages.temp.TempBorrowScreen
import com.hxb.myandroidapp_1021.pages.temp.TempBorrowViewModel
import com.hxb.myandroidapp_1021.pages.temp.TempReturnScreen
import com.hxb.myandroidapp_1021.pages.temp.TempReturnViewModel
import com.hxb.myandroidapp_1021.pages.stockdetail.StockDetailListScreen

@Composable
fun AppNavHost(sessionViewModel: SessionViewModel = hiltViewModel()) {
    val session by sessionViewModel.state.collectAsStateWithLifecycle()
    if (!session.loaded) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) { CircularProgressIndicator() }
        return
    }
    val navController = rememberNavController()
    val initialDestination: Any = remember { if (session.user == null) Login else Dashboard }
    val currentEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(session.user, currentEntry) {
        val destination = currentEntry?.destination ?: return@LaunchedEffect
        if (session.user == null && !destination.hasRoute<Login>() && !destination.hasRoute<ServerConfig>()) {
            navController.navigate(Login) {
                popUpTo(navController.graph.id) {
                    inclusive = false
                }; launchSingleTop = true
            }
        }
    }
    // 防止重复点击返回导致页面空白
    val safeBack: () -> Unit = {
        if (navController.previousBackStackEntry != null) {
            navController.popBackStack()
        }
    }

    NavHost(
        navController = navController,
        startDestination = initialDestination,
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
                onSettingsClick = {
                    navController.navigate(ServerConfig) {
                        launchSingleTop = true
                    }
                },
                onLoginSuccess = {
                    navController.navigate(Dashboard) {
                        popUpTo<Login> { inclusive = true }
                        launchSingleTop = true
                    }
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
                onBackClick = safeBack
            )
        }
        // 借出
        composable<PickingList> { entry ->
            val scanViewModel = hiltViewModel<PickingScanViewModel>(entry)
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
                viewModel = hiltViewModel<PickingScanViewModel>(listEntry),
                flowViewModel = hiltViewModel<PickingListViewModel>(listEntry),
                onBackClick = safeBack,
                onSubmittedClick = { navController.popBackStack<PickingList>(false) },
                onSettingsClick = {
                    navController.navigate(ServerConfig) {
                        launchSingleTop = true
                    }
                },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        // 归还
        composable<ReturnList> { entry ->
            val scanViewModel = hiltViewModel<ReturnScanViewModel>(entry)
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
                viewModel = hiltViewModel<ReturnScanViewModel>(listEntry),
                flowViewModel = hiltViewModel<ReturnListViewModel>(listEntry),
                onBackClick = safeBack,
                onSettingsClick = {
                    navController.navigate(ServerConfig) {
                        launchSingleTop = true
                    }
                },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        // 临时借还与其他作业共享首页作用域的任务列表，返回首页保留进度，退出登录后随首页销毁。
        composable<TempBorrow> { entry ->
            val dashboardEntry = remember(entry) { navController.getBackStackEntry<Dashboard>() }
            val borrowViewModel = hiltViewModel<TempBorrowViewModel>(dashboardEntry)
            val listViewModel = hiltViewModel<OtherPickingListViewModel>(dashboardEntry)
            TempBorrowScreen(
                viewModel = borrowViewModel,
                onBackClick = safeBack,
                onHomeClick = {
                    borrowViewModel.showList(); navController.popBackStack<Dashboard>(
                    false
                )
                },
                onPickingClick = {
                    borrowViewModel.uiState.value.selectedOrderNo?.let {
                        listViewModel.selectSource(
                            it
                        )
                    }
                    navController.navigate(OtherPickingList) { launchSingleTop = true }
                }
            )
        }
        composable<TempReturn> { entry ->
            val dashboardEntry = remember(entry) { navController.getBackStackEntry<Dashboard>() }
            TempReturnScreen(
                viewModel = hiltViewModel<TempReturnViewModel>(dashboardEntry),
                onBackClick = safeBack,
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        // 其他作业的核对记录仍保存在列表作用域，不通过路由传递业务数据。
        composable<OtherPickingList> { entry ->
            val dashboardEntry = remember(entry) { navController.getBackStackEntry<Dashboard>() }
            OtherPickingListScreen(
                viewModel = hiltViewModel<OtherPickingListViewModel>(dashboardEntry),
                flowViewModel = hiltViewModel<OtherPickingScanViewModel>(entry),
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
                flowViewModel = hiltViewModel<OtherPickingScanViewModel>(listEntry),
                listViewModel = hiltViewModel<OtherPickingListViewModel>(dashboardEntry),
                onBackClick = safeBack,
                onComplete = { navController.popBackStack<OtherPickingList>(false) },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
        composable<OtherPutawayList> { entry ->
            val dashboardEntry = remember(entry) { navController.getBackStackEntry<Dashboard>() }
            OtherPutawayListScreen(
                viewModel = hiltViewModel<OtherPutawayListViewModel>(dashboardEntry),
                flowViewModel = hiltViewModel<OtherPutawayScanViewModel>(entry),
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
                flowViewModel = hiltViewModel<OtherPutawayScanViewModel>(listEntry),
                listViewModel = hiltViewModel<OtherPutawayListViewModel>(dashboardEntry),
                onBackClick = safeBack,
                onComplete = { navController.popBackStack<OtherPutawayList>(false) },
                onHomeClick = { navController.popBackStack<Dashboard>(false) }
            )
        }
    }
}
