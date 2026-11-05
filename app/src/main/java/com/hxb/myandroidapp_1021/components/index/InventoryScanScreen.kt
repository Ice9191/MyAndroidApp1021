package com.hxb.myandroidapp_1021.components.index

import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.hxb.myandroidapp_1021.R
import com.hxb.myandroidapp_1021.components.index.components.InfoCard
import com.hxb.myandroidapp_1021.components.index.components.ReadConfirmDialog
import com.hxb.myandroidapp_1021.components.index.components.ScanControlBar
import com.hxb.myandroidapp_1021.components.index.components.ScanTabRow
import com.hxb.myandroidapp_1021.components.index.components.TabsContent
import com.hxb.myandroidapp_1021.components.index.models.DisplayField
import com.hxb.myandroidapp_1021.components.index.models.InfoRow
import com.hxb.myandroidapp_1021.components.index.models.ScanColors
import com.hxb.myandroidapp_1021.components.index.models.ScanTab
import com.hxb.myandroidapp_1021.components.index.models.ScanTabsLabels
import com.hxb.myandroidapp_1021.components.index.models.ToolScanItem
import com.hxb.myandroidapp_1021.components.index.utils.toggleChecked
import com.hxb.myandroidapp_1021.utils.RfidScanner
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScanScreen(
    onBackClick: () -> Unit = {},
    onNextClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    val themeBlue = Color(0xFF1E71C8)
    val bgColor = Color(0xFFF5F6F8)
    val textGray = Color(0xFF888888)
    var selectedTab by remember { mutableStateOf(ScanTab.All) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val allTools = remember {
        listOf(
            ToolScanItem(
                id = "GJ-02021",
                name = "数字万用表",
                code = "GJ-02021",
                rfid = "E200 2021 F001"
            ),
            ToolScanItem(
                id = "GJ-03025",
                name = "钳形电流表",
                code = "GJ-03025",
                rfid = "E200 3025 F002"
            ),
            ToolScanItem(
                id = "GJ-04018",
                name = "绝缘电阻测试仪",
                code = "GJ-04018",
                rfid = "E200 4018 G001"
            )
        )
    }
    val readList = remember {
        mutableStateListOf<ToolScanItem>().apply {
            allTools.getOrNull(0)?.let { add(it.copy(pickDate = today)) }
        }
    }
    val unreadList = remember {
        mutableStateListOf<ToolScanItem>().apply {
            allTools.getOrNull(1)?.let { add(it) }
        }
    }
    val isNextEnabled = readList.isNotEmpty() && unreadList.isEmpty()
    val scanner = remember { RfidScanner() }
    val context = LocalContext.current
    val scanSound = remember(context) { MediaPlayer.create(context, R.raw.scan) }
    val isScanning by scanner.isScanning.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, scanner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> scanner.init()
                Lifecycle.Event.ON_PAUSE -> scanner.release()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            scanner.init()
        }
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            scanner.release()
        }
    }

    DisposableEffect(scanSound) {
        onDispose { scanSound?.release() }
    }

    LaunchedEffect(scanner, scanSound) {
        scanner.tagEvents.collect { epc ->
            val normalized = epc.replace(" ", "").uppercase()
            val existsInRead = readList.any { it.rfid.replace(" ", "").uppercase() == normalized }
            val existsInUnread =
                unreadList.any { it.rfid.replace(" ", "").uppercase() == normalized }
            if (!existsInRead && !existsInUnread) {
                unreadList.add(ToolScanItem(id = epc, rfid = epc))
                scanSound?.apply {
                    seekTo(0)
                    start()
                }
            }
        }
    }

    // 错误 Toast
    LaunchedEffect(Unit) {
        scanner.errorMessage.collect {
            if (it != null) Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                modifier = Modifier.height(75.dp),
                title = {
                    Text(
                        text = "盘点",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.offset(x = (-16).dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.offset(x = (-7).dp)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSettingsClick, modifier = Modifier.offset(x = (-4).dp)) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Setting",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = onHomeClick, modifier = Modifier.offset(x = (-4).dp)) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = themeBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // === 1. 顶部信息卡片 ===
            InfoCard(
                rows = listOf(
                    InfoRow(
                        left = DisplayField("班组", "检修一班"),
                        right = DisplayField("盘点人", "张伟")
                    ),
                    InfoRow(
                        left = DisplayField("已盘工器具", "${readList.size}件"),
                        right = DisplayField("盘点日期", today)
                    )
                )
            )
            HorizontalDivider(thickness = 8.dp, color = bgColor)

            // === 2. 读取控制区：开始读取 + 下一步 ===
            ScanControlBar(
                isScanning = isScanning,
                onScanClick = { if (isScanning) scanner.stopScan() else scanner.startScan() },
                nextEnabled = isNextEnabled,
                onNextClick = onNextClick
            )
            HorizontalDivider(thickness = 8.dp, color = bgColor)

            // === 3. 三个 Tab ===
            ScanTabRow(
                labels = listOf(
                    "全部 ${allTools.size}",
                    "已读取 ${readList.size}",
                    "未读取 ${unreadList.size}"
                ),
                selectedIndex = selectedTab.ordinal,
                onSelect = { selectedTab = ScanTab.entries[it] }
            )

            // === 4. Tab 内容 ===
            TabsContent(
                tab = selectedTab,
                allTools = allTools,
                readList = readList,
                unreadList = unreadList,
                colors = ScanColors(themeBlue, textGray, bgColor),
                labels = ScanTabsLabels(
                    date = "盘点日期",
                    readStatus = "已盘点",
                    unreadStatus = "未盘点"
                ),
                onToggle = { id, checked ->
                    toggleChecked(selectedTab, id, checked, readList, unreadList)
                },
                onActionClick = { showConfirmDialog = true }
            )
        }
    }

    // === 确认弹窗 ===
    ReadConfirmDialog(
        visible = showConfirmDialog,
        tab = selectedTab,
        readList = readList,
        unreadList = unreadList,
        today = today,
        onDismiss = { showConfirmDialog = false }
    )
}
