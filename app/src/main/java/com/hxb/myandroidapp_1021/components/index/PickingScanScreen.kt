package com.hxb.myandroidapp_1021.components.index

import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.hxb.myandroidapp_1021.components.index.components.ManualRfidInput
import com.hxb.myandroidapp_1021.utils.RfidScanner

data class ToolScanItem(
    val id: String,
    val name: String = "",
    val code: String = "",
    val rfid: String,
    var isChecked: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickingScanScreen(
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val themeBlue = Color(0xFF1E71C8)
    val bgColor = Color(0xFFF5F6F8)
    val textDark = Color(0xFF222222)
    val textGray = Color(0xFF888888)
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    val readList = remember { mutableStateListOf<ToolScanItem>() }
    val unreadList = remember { mutableStateListOf<ToolScanItem>() }
    val isNextEnabled = readList.isNotEmpty() && unreadList.isEmpty()
    val hasSelectedRead = readList.any { it.isChecked }
    val infoFields = listOf(
        DisplayField("班组", "检修一班"),
        DisplayField("领用人", "张伟"),
        DisplayField("工具数量", "${readList.size + unreadList.size}件"),
        DisplayField("期望领用", "10-30 09:30")
    )
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
            val existsInRead = readList.any {
                it.rfid.replace(" ", "").uppercase() == normalized
            }
            val existsInUnread = unreadList.any {
                it.rfid.replace(" ", "").uppercase() == normalized
            }
            if (!existsInRead && !existsInUnread) {
                unreadList.add(
                    ToolScanItem(
                        id = epc,
                        rfid = epc
                    )
                )
                scanSound?.apply {
                    seekTo(0)
                    start()
                }
            }
        }
    }

    // 错误Toast
    LaunchedEffect(Unit) {
        scanner.errorMessage.collect {
            if (it != null) Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                modifier = Modifier.height(75.dp),
                title = {
                    Text(
                        text = "借出·拣选",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.offset(x = (-16).dp)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.offset(x = (-7).dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier.offset(x = (-4).dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Setting",
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
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Button(
                    onClick = { /* 下一步逻辑 */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isNextEnabled) themeBlue else Color(0xFFB0BEC5),
                        disabledContainerColor = Color(0xFFB0BEC5)
                    ),
                    enabled = isNextEnabled
                ) {
                    Text(
                        text = "下一步",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // === 1. 顶部信息卡片 ===
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "JC20261028001",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = textDark
                        )
                        Text(text = "待处理", fontSize = 14.sp, color = themeBlue)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        infoFields.forEach { field ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${field.label}：",
                                    fontSize = 13.sp,
                                    color = textGray,
                                    maxLines = 1,
                                    modifier = Modifier.width(65.dp)
                                )
                                Text(
                                    text = field.value,
                                    fontSize = 13.sp,
                                    color = textDark,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
                HorizontalDivider(thickness = 8.dp, color = bgColor)
            }

            // === 2. 读取控制区 ===
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            if (isScanning) scanner.stopScan() else scanner.startScan()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isScanning) Color(
                                0xFFE53935
                            ) else themeBlue
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.QrCodeScanner,
                            contentDescription = "Scan",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isScanning) "停止读取" else "开始读取",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                HorizontalDivider(thickness = 8.dp, color = bgColor)
            }

            // === 3. Tab 切换栏 ===
            item {
                SecondaryTabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.White,
                    contentColor = themeBlue,
                    indicator = {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(selectedTabIndex),
                            color = themeBlue
                        )
                    },
                    divider = {
                        HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F0))
                    }
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = {
                            Text(
                                "已读取 ${readList.size}",
                                fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTabIndex == 0) themeBlue else textGray
                            )
                        }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = {
                            Text(
                                "未读取 ${unreadList.size}",
                                fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTabIndex == 1) themeBlue else textGray
                            )
                        }
                    )
                }
            }

            // === 4. 列表内容切换 ===
            if (selectedTabIndex == 0) {
                // --- 已读取列表 ---
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "已选${readList.count { it.isChecked }}件",
                            fontSize = 14.sp,
                            color = textGray
                        )
                        Button(
                            onClick = { showConfirmDialog = true },
                            modifier = Modifier
                                .height(30.dp)
                                .wrapContentWidth(),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (hasSelectedRead) themeBlue else Color(
                                    0xFFB0BEC5
                                )
                            ),
                            enabled = hasSelectedRead
                        ) {
                            Text("取消读取", fontSize = 13.sp, color = Color.White)
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F6F8))
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "RFID",
                            modifier = Modifier.weight(1f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = textGray
                        )
                        Text(
                            text = "数量/件",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = textGray
                        )
                    }
                    HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))
                }

                items(readList, key = { it.id }) { item ->
                    Column(modifier = Modifier.background(Color.White)) {
                        ReadItemRow(
                            item = item,
                            themeBlue = themeBlue,
                            textDark = textDark,
                            onCheckedChange = { isChecked ->
                                val index = readList.indexOf(item)
                                if (index != -1) readList[index] =
                                    item.copy(isChecked = isChecked)
                            }
                        )
                        HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F0))
                    }
                }
            } else {
                // --- 未读取列表 ---
                item {
                    ManualRfidInput(
                        existingRfids = (readList + unreadList).map { it.rfid },
                        selectedCount = unreadList.count { it.isChecked },
                        readLabel = "手动读取",
                        onRead = { showConfirmDialog = true }
                    ) { rfid ->
                        unreadList.add(ToolScanItem(id = rfid, rfid = rfid, isChecked = true))
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F6F8))
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "RFID",
                            modifier = Modifier.weight(1f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = textGray
                        )
                        Text(
                            text = "数量/件",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = textGray
                        )
                    }
                    HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))
                }

                items(unreadList, key = { it.id }) { item ->
                    Column(modifier = Modifier.background(Color.White)) {
                        UnreadItemRow(
                            item = item,
                            themeBlue = themeBlue,
                            textDark = textDark,
                            onCheckedChange = { isChecked ->
                                val index = unreadList.indexOf(item)
                                if (index != -1) unreadList[index] =
                                    item.copy(isChecked = isChecked)
                            }
                        )
                        HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F0))
                    }
                }
            }


            item {
                Spacer(
                    modifier = Modifier
                        .height(20.dp)
                        .fillMaxWidth()
                        .background(Color.White)
                )
            }
        }
    }

    // 确认弹窗
    if (showConfirmDialog) {
        val isReadAction = selectedTabIndex == 1
        val actionLabel = if (isReadAction) "手动读取" else "取消读取"
        val targetCount = if (isReadAction) {
            unreadList.count { it.isChecked }
        } else {
            readList.count { it.isChecked }
        }
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("确认操作") },
            text = { Text("是否${actionLabel}选中的 $targetCount 件工具？") },
            containerColor = Color.White,
            confirmButton = {
                TextButton(
                    onClick = {
                        if (isReadAction) {
                            // 未读取 -> 已读取
                            val selectedItems = unreadList.filter { it.isChecked }
                            if (selectedItems.isNotEmpty()) {
                                readList.addAll(selectedItems.map { it.copy(isChecked = false) })
                                unreadList.removeAll(selectedItems)
                            }
                        } else {
                            // 已读取 -> 未读取
                            val selectedItems = readList.filter { it.isChecked }
                            if (selectedItems.isNotEmpty()) {
                                unreadList.addAll(selectedItems.map { it.copy(isChecked = false) })
                                readList.removeAll(selectedItems)
                            }
                        }
                        showConfirmDialog = false
                    }
                ) {
                    Text("确认", color = themeBlue)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmDialog = false }
                ) {
                    Text("取消", color = textGray)
                }
            }
        )
    }
}

// ================= 列表项组件 =================

// 已读取列表
@Composable
fun ReadItemRow(
    item: ToolScanItem,
    themeBlue: Color,
    textDark: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.isChecked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = themeBlue),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = item.rfid,
            modifier = Modifier.weight(1f),
            fontSize = 14.sp,
            color = textDark
        )
        Text(
            text = "1",
            fontSize = 14.sp,
            color = textDark
        )
    }
}

// 未读取列表
@Composable
fun UnreadItemRow(
    item: ToolScanItem,
    themeBlue: Color,
    textDark: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.isChecked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = themeBlue),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = item.rfid,
            modifier = Modifier.weight(1f),
            fontSize = 14.sp,
            color = textDark
        )
        Text(
            text = "1",
            fontSize = 14.sp,
            color = textDark
        )
    }
}
