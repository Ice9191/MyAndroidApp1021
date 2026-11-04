package com.hxb.myandroidapp_1021.components.index

import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Home
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
import com.hxb.myandroidapp_1021.components.index.components.ToolCheckListTab
import com.hxb.myandroidapp_1021.components.index.components.ToolCheckRow
import com.hxb.myandroidapp_1021.components.index.models.DisplayField
import com.hxb.myandroidapp_1021.utils.RfidScanner
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ToolScanItem(
    val id: String,
    val name: String = "",
    val code: String = "",
    val rfid: String,
    var isChecked: Boolean = false,
    val pickDate: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickingScanScreen(
    order: PickingOrder? = null,
    onBackClick: () -> Unit = {},
    onNextClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    val orderNo = order?.orderNo.orEmpty()
    val status = order?.status.orEmpty()
    val team = order?.team.orEmpty()
    val person = order?.person.orEmpty()
    val toolCount = order?.toolCount ?: 0
    val expectedTime = order?.expectedTime.orEmpty()
    val themeBlue = Color(0xFF1E71C8)
    val bgColor = Color(0xFFF5F6F8)
    val textDark = Color(0xFF222222)
    val textGray = Color(0xFF888888)

    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0=全部 1=已读取 2=未读取
    var showConfirmDialog by remember { mutableStateOf(false) }
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    // 全部工器具：数量就是上一页传进来的「工具数量」
    val allTools = remember(orderNo, toolCount) { buildTools(orderNo, toolCount) }

    // 演示数据：第 1 件当成已拣选、第 2 件当成刚扫到还没确认。接接口后删掉这两段即可
    val readList = remember(orderNo) {
        mutableStateListOf<ToolScanItem>().apply {
            allTools.getOrNull(0)?.let { add(it.copy(pickDate = today)) }
        }
    }
    val unreadList = remember(orderNo) {
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
                        text = "借出·拣选",
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
            // === 1. 顶部信息卡片（3 行，左右 spaceBetween，内容全部来自上一页） ===
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                InfoLine(
                    left = DisplayField("拣选单号", orderNo),
                    right = DisplayField("状态", status),
                    leftBold = true,
                    rightColor = themeBlue,
                    textDark = textDark,
                    textGray = textGray
                )
                InfoLine(
                    left = DisplayField("班组", team),
                    right = DisplayField("领用人", person),
                    textDark = textDark,
                    textGray = textGray
                )
                InfoLine(
                    left = DisplayField("工具数量", "${toolCount}件"),
                    right = DisplayField("期望领用", expectedTime),
                    textDark = textDark,
                    textGray = textGray
                )
            }
            HorizontalDivider(thickness = 8.dp, color = bgColor)

            // === 2. 读取控制区：开始读取 + 下一步 ===
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { if (isScanning) scanner.stopScan() else scanner.startScan() },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isScanning) Color(0xFFE53935) else themeBlue
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
                Button(
                    onClick = onNextClick,
                    enabled = isNextEnabled,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = themeBlue,
                        disabledContainerColor = Color(0xFFB0BEC5)
                    )
                ) {
                    Text(
                        text = "下一步",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            HorizontalDivider(thickness = 8.dp, color = bgColor)

            // === 3. 三个 Tab ===
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
                divider = { HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F0)) }
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Text(
                            text = "全部 ${allTools.size}",
                            color = if (selectedTabIndex == 0) themeBlue else textGray,
                            fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Text(
                            text = "已读取 ${readList.size}",
                            color = if (selectedTabIndex == 1) themeBlue else textGray,
                            fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = {
                        Text(
                            text = "未读取 ${unreadList.size}",
                            color = if (selectedTabIndex == 2) themeBlue else textGray,
                            fontWeight = if (selectedTabIndex == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }

            // === 4. Tab 内容 ===
            when (selectedTabIndex) {
                // 全部：每件工器具一行卡片：RFID / 名称 / 状态 / 拣选日期
                0 -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(bgColor)
                ) {
                    items(allTools, key = { it.id }) { tool ->
                        val pickedItem = readList.firstOrNull { it.rfid == tool.rfid }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            ToolField("RFID", tool.rfid, textDark, textGray)
                            ToolField("名称", tool.name, textDark, textGray)
                            ToolField(
                                "状态",
                                if (pickedItem != null) "已拣选" else "未拣选",
                                textDark,
                                textGray,
                                valueColor = if (pickedItem != null) themeBlue else textGray
                            )
                            ToolField(
                                "拣选日期",
                                pickedItem?.pickDate?.ifBlank { "-" } ?: "-",
                                textDark,
                                textGray
                            )
                        }
                        HorizontalDivider(thickness = 8.dp, color = bgColor)
                    }
                }

                // 已读取 / 未读取：共用一个表格组件
                else -> {
                    val isReadTab = selectedTabIndex == 1
                    val sourceList = if (isReadTab) readList else unreadList
                    ToolCheckListTab(
                        rows = sourceList.map {
                            ToolCheckRow(
                                id = it.id,
                                rfid = it.rfid,
                                subtitle = it.name,
                                quantity = 1,
                                checked = it.isChecked
                            )
                        },
                        selectedCount = sourceList.count { it.isChecked },
                        actionLabel = if (isReadTab) "取消读取" else "手动读取",
                        actionEnabled = sourceList.any { it.isChecked },
                        onActionClick = { showConfirmDialog = true },
                        onCheckedChange = { row, checked ->
                            val index = sourceList.indexOfFirst { it.id == row.id }
                            if (index != -1) {
                                sourceList[index] = sourceList[index].copy(isChecked = checked)
                            }
                        }
                    )
                }
            }
        }
    }

    // === 确认弹窗 ===
    if (showConfirmDialog) {
        val isManualRead = selectedTabIndex == 2 // 未读取 -> 手动读取
        val actionLabel = if (isManualRead) "手动读取" else "取消读取"
        val targetCount = if (isManualRead) {
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
                        if (isManualRead) {
                            // 未读取 -> 已读取，记录拣选日期
                            val selectedItems = unreadList.filter { it.isChecked }
                            if (selectedItems.isNotEmpty()) {
                                readList.addAll(
                                    selectedItems.map {
                                        it.copy(
                                            isChecked = false,
                                            pickDate = today
                                        )
                                    }
                                )
                                unreadList.removeAll(selectedItems)
                            }
                        } else {
                            // 已读取 -> 未读取，清掉拣选日期
                            val selectedItems = readList.filter { it.isChecked }
                            if (selectedItems.isNotEmpty()) {
                                unreadList.addAll(
                                    selectedItems.map { it.copy(isChecked = false, pickDate = "") }
                                )
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
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("取消", color = textGray)
                }
            }
        )
    }
}

@Composable
private fun InfoLine(
    left: DisplayField,
    right: DisplayField,
    textDark: Color,
    textGray: Color,
    leftBold: Boolean = false,
    rightColor: Color? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("${left.label}：", fontSize = 13.sp, color = textGray)
            Text(
                text = left.value,
                fontSize = if (leftBold) 15.sp else 13.sp,
                fontWeight = if (leftBold) FontWeight.Bold else FontWeight.Normal,
                color = textDark
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("${right.label}：", fontSize = 13.sp, color = textGray)
            Text(
                text = right.value,
                fontSize = 13.sp,
                fontWeight = if (rightColor != null) FontWeight.Medium else FontWeight.Normal,
                color = rightColor ?: textDark
            )
        }
    }
}

@Composable
private fun ToolField(
    label: String,
    value: String,
    textDark: Color,
    textGray: Color,
    valueColor: Color = textDark
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = textGray,
            maxLines = 1
        )
        Text(
            text = value,
            fontSize = 13.sp,
            color = valueColor,
            maxLines = 1,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

private fun buildTools(orderNo: String, count: Int): List<ToolScanItem> {
    val names = listOf(
        "绝缘扳手", "数字万用表", "游标卡尺", "绝缘手套", "验电器",
        "接地线", "安全帽", "力矩扳手", "钳形电流表", "绝缘靴"
    )
    return List(count.coerceAtLeast(0)) { index ->
        val no = (index + 1).toString().padStart(3, '0')
        ToolScanItem(
            id = "$orderNo-$no",
            name = names[index % names.size],
            code = "GJ-$no",
            rfid = "E280 1160 6000 0200 ${(index + 1).toString().padStart(4, '0')}"
        )
    }
}

// ================= 兼容旧调用：归还扫码页仍在用这个行组件 =================

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
        Text(text = "1", fontSize = 14.sp, color = textDark)
    }
}
