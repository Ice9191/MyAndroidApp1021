package com.hxb.myandroidapp_1021.components.index

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import com.hxb.myandroidapp_1021.components.index.ToolScanItem
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults

@Composable
fun ScrapCheckScreen(onBackClick: () -> Unit = {}, onConfirmClick: () -> Unit = {}) {
    val themeBlue = Color(0xFF1E71C8)
    val bgColor = Color(0xFFF5F6F8)
    var selectedTab by remember { mutableIntStateOf(0) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    // shortcut: 仅演示已审批报废单的核对状态，RFID及出库接口待接入。
    val readList = remember {
        mutableStateListOf(ToolScanItem("GJ-01055", "安全帽", "GJ-01055", "E200 1055 C001"))
    }
    val unreadList = remember {
        mutableStateListOf(ToolScanItem("GJ-01062", "绝缘绳", "GJ-01062", "E200 1062 C002"))
    }
    val manuallyConfirmed = remember { mutableStateListOf<String>() }
    val canConfirm = readList.size == 2 && unreadList.isEmpty()

    Scaffold(
        containerColor = bgColor,
        topBar = { ApplicationTopBar("报废出库核对", onBackClick) },
        bottomBar = {
            VerificationButton(
                "确认出库",
                onClick = { showConfirmDialog = true },
                enabled = canConfirm
            )
        }
    ) { paddingValues ->
        LazyColumn(Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            item {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "报废单号：BF20261027003",
                            modifier = Modifier.weight(1f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF222222)
                        )
                        Text("审批通过", fontSize = 13.sp, color = themeBlue)
                    }
                    Text("申请人：李明 · 中心工具库", fontSize = 13.sp, color = Color(0xFF222222))
                    Text("工具数量：2件 · 待出库", fontSize = 13.sp, color = Color(0xFF222222))
                }
                HorizontalDivider(thickness = 8.dp, color = bgColor)
                VerificationButton("RFID读取（占位）", onClick = {}, enabled = false)
                HorizontalDivider(thickness = 8.dp, color = bgColor)
                VerificationTabs(
                    listOf("已读取 ${readList.size}", "未读取 ${unreadList.size}"),
                    selectedTab
                ) { selectedTab = it }
            }
            if (selectedTab == 1) {
                item {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "已选${unreadList.count { it.isChecked }}件",
                            fontSize = 14.sp,
                            color = Color(0xFF888888)
                        )
                        Button(
                            onClick = {
                                val selected = unreadList.filter { it.isChecked }
                                manuallyConfirmed.addAll(selected.map { it.id })
                                readList.addAll(selected.map { it.copy(isChecked = false) })
                                unreadList.removeAll(selected)
                            },
                            enabled = unreadList.any { it.isChecked },
                            modifier = Modifier.height(30.dp),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = themeBlue,
                                disabledContainerColor = Color(0xFFB0BEC5)
                            )
                        ) { Text("确认", fontSize = 13.sp, color = Color.White) }
                    }
                }
                items(unreadList, key = { it.id }) { tool ->
                    VerificationToolRow(tool, "待核对") { checked ->
                        val index = unreadList.indexOf(tool)
                        if (index != -1) unreadList[index] = tool.copy(isChecked = checked)
                    }
                }
            } else {
                items(readList, key = { it.id }) { tool ->
                    VerificationToolRow(
                        tool,
                        if (tool.id in manuallyConfirmed) "人工确认" else "RFID读取"
                    )
                }
            }
            item {
                Spacer(Modifier
                    .height(20.dp)
                    .fillMaxWidth()
                    .background(Color.White))
            }
        }
    }
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false }, containerColor = Color.White,
            title = { Text("确认报废出库") },
            text = { Text("2件工具已全部核对。当前仅演示页面，不提交出库记录。") },
            confirmButton = {
                TextButton(onClick = { showConfirmDialog = false; onConfirmClick() }) {
                    Text(
                        "确认",
                        color = themeBlue
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text(
                        "取消",
                        color = Color(0xFF888888)
                    )
                }
            }
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun ScrapCheckScreenPreview() {
    ScrapCheckScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ApplicationTopBar(title: String, onBackClick: () -> Unit) {
    TopAppBar(
        modifier = Modifier.height(75.dp),
        title = {
            Text(
                title, fontSize = 18.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.offset(x = (-16).dp)
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick, modifier = Modifier.offset(x = (-7).dp)) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "返回")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF1E71C8), titleContentColor = Color.White,
            navigationIconContentColor = Color.White
        )
    )
}

@Composable
private fun VerificationButton(text: String, onClick: () -> Unit, enabled: Boolean = true) {
    Column(Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp, vertical = 12.dp)) {
        Button(
            onClick = onClick, enabled = enabled, modifier = Modifier.fillMaxWidth().height(44.dp),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E71C8), disabledContainerColor = Color(0xFFB0BEC5))
        ) {
            Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VerificationTabs(labels: List<String>, selectedIndex: Int, onSelect: (Int) -> Unit) {
    val themeBlue = Color(0xFF1E71C8)
    SecondaryTabRow(
        selectedTabIndex = selectedIndex, containerColor = Color.White, contentColor = themeBlue,
        indicator = {
            TabRowDefaults.SecondaryIndicator(Modifier.tabIndicatorOffset(selectedIndex), color = themeBlue)
        }
    ) {
        labels.forEachIndexed { index, label ->
            Tab(selected = selectedIndex == index, onClick = { onSelect(index) }, text = {
                Text(label, color = if (selectedIndex == index) themeBlue else Color(0xFF888888),
                    fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Normal)
            })
        }
    }
}

@Composable
private fun VerificationToolRow(tool: ToolScanItem, source: String, onCheckedChange: ((Boolean) -> Unit)? = null) {
    Row(
        Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onCheckedChange != null) {
            Checkbox(checked = tool.isChecked, onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF1E71C8)))
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(tool.name, modifier = Modifier.weight(1f), fontSize = 14.sp,
                    fontWeight = FontWeight.Bold, color = Color(0xFF222222))
                Text(source, fontSize = 12.sp, color = Color(0xFF1E71C8))
            }
            Text(tool.code, fontSize = 13.sp, color = Color(0xFF888888))
            Text("RFID：${tool.rfid}", fontSize = 13.sp, color = Color(0xFF888888))
        }
    }
    HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F0))
}
