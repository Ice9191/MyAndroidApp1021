package com.hxb.myandroidapp_1021.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxb.myandroidapp_1021.data.models.DisplayField
import com.hxb.myandroidapp_1021.data.models.InfoRow
import com.hxb.myandroidapp_1021.data.models.ScanTab
import com.hxb.myandroidapp_1021.data.models.ToolScanItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrapScanScreen(
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
                id = "GJ-01055",
                name = "安全帽",
                code = "GJ-01055",
                rfid = "E200 1055 C001"
            ),
            ToolScanItem(
                id = "GJ-01062",
                name = "绝缘绳",
                code = "GJ-01062",
                rfid = "E200 1062 C002"
            ),
            ToolScanItem(
                id = "GJ-01070",
                name = "绝缘手套",
                code = "GJ-01070",
                rfid = "E200 1070 C003"
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

    Scaffold(
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                modifier = Modifier.height(75.dp),
                title = {
                    Text(
                        text = "工具报废",
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
            val infoRows = listOf(
                InfoRow(
                    left = DisplayField("班组", "检修一班"),
                    right = DisplayField("申请人", "张伟")
                ),
                InfoRow(
                    left = DisplayField("已选工器具", "${readList.size}件"),
                    right = DisplayField("申请日期", today)
                )
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                infoRows.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${row.left.label}：", fontSize = 13.sp, color = textGray)
                            Text(
                                text = row.left.value,
                                fontSize = if (row.leftBold) 15.sp else 13.sp,
                                fontWeight = if (row.leftBold) FontWeight.Bold else FontWeight.Normal,
                                color = Color(0xFF222222)
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${row.right.label}：", fontSize = 13.sp, color = textGray)
                            Text(
                                text = row.right.value,
                                fontSize = 13.sp,
                                fontWeight = if (row.rightColor != null) FontWeight.Medium else FontWeight.Normal,
                                color = row.rightColor ?: Color(0xFF222222)
                            )
                        }
                    }
                }
            }
            HorizontalDivider(thickness = 8.dp, color = bgColor)

            // === 2. 读取控制区 ===
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {},
                    enabled = false,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "RFID 待开放",
                        fontSize = 14.sp,
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
            val tabLabels = listOf(
                "全部 ${allTools.size}",
                "已读取 ${readList.size}",
                "未读取 ${unreadList.size}"
            )
            SecondaryTabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = Color.White,
                contentColor = themeBlue,
                indicator = {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(selectedTab.ordinal),
                        color = themeBlue
                    )
                },
                divider = { HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F0)) }
            ) {
                tabLabels.forEachIndexed { index, label ->
                    Tab(
                        selected = selectedTab.ordinal == index,
                        onClick = { selectedTab = ScanTab.entries[index] },
                        text = {
                            Text(
                                text = label,
                                color = if (selectedTab.ordinal == index) themeBlue else textGray,
                                fontWeight = if (selectedTab.ordinal == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // === 4. Tab 内容 ===
            if (selectedTab == ScanTab.All) {
                LazyColumn(
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
                            val status = if (pickedItem != null) "已报废" else "未报废"
                            val statusColor = if (pickedItem != null) themeBlue else textGray
                            val statusField = DisplayField("状态", status)
                            listOf(
                                DisplayField("RFID", tool.rfid),
                                DisplayField("名称", tool.name),
                                statusField,
                                DisplayField("报废日期", pickedItem?.pickDate?.ifBlank { "-" } ?: "-")
                            ).forEach { field ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = field.label,
                                        fontSize = 13.sp,
                                        color = textGray,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = field.value,
                                        fontSize = 13.sp,
                                        color = if (field == statusField) statusColor else Color(0xFF222222),
                                        maxLines = 1,
                                        modifier = Modifier.padding(start = 16.dp)
                                    )
                                }
                            }
                        }
                        HorizontalDivider(thickness = 8.dp, color = bgColor)
                    }
                }
            } else {
                val sourceList = if (selectedTab == ScanTab.Read) readList else unreadList
                val actionLabel = if (selectedTab == ScanTab.Read) "取消读取" else "手动读取"
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    // === 1. 已选 N 件 + 操作按钮 ===
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "已选${sourceList.count { it.isChecked }}件",
                            fontSize = 14.sp,
                            color = textGray
                        )
                        Button(
                            onClick = { showConfirmDialog = true },
                            enabled = sourceList.any { it.isChecked },
                            modifier = Modifier.height(30.dp),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = themeBlue,
                                disabledContainerColor = Color(0xFFB0BEC5)
                            )
                        ) {
                            Text(actionLabel, fontSize = 13.sp, color = Color.White)
                        }
                    }

                    // === 2. 表头：（空） / RFID / 数量 ===
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F6F8))
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(28.dp)) // 第一列留给 checkbox，表头空着
                        Text(
                            text = "RFID",
                            modifier = Modifier.weight(1f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = textGray
                        )
                        Text(
                            text = "数量",
                            modifier = Modifier.width(48.dp),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = textGray,
                            textAlign = TextAlign.End
                        )
                    }
                    HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))

                    // === 3. 表格内容 ===
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(sourceList, key = { it.id }) { tool ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White)
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = tool.isChecked,
                                    onCheckedChange = { checked ->
                                        val index = sourceList.indexOfFirst { it.id == tool.id }
                                        if (index != -1) {
                                            sourceList[index] = sourceList[index].copy(isChecked = checked)
                                        }
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = themeBlue),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = tool.rfid, fontSize = 14.sp, color = Color(0xFF222222))
                                    if (tool.name.isNotBlank()) {
                                        Text(text = tool.name, fontSize = 12.sp, color = textGray)
                                    }
                                }
                                Text(
                                    text = "1",
                                    modifier = Modifier.width(48.dp),
                                    fontSize = 14.sp,
                                    color = Color(0xFF222222),
                                    textAlign = TextAlign.End
                                )
                            }
                            HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F0))
                        }
                    }
                }
            }
        }
    }

    // === 确认弹窗 ===
    if (showConfirmDialog) {
        val isManualRead = selectedTab == ScanTab.Unread // 未读取 -> 手动读取
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
                                        it.copy(isChecked = false, pickDate = today)
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
