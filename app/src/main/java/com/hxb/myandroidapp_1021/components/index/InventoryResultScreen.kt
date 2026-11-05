package com.hxb.myandroidapp_1021.components.index

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** 盘点结果里的一件工器具：工具信息 + 盘点结果（已盘 / 盘亏） */
data class InventoryResultItem(
    val tool: ToolScanItem,
    val result: String
)

/**
 * 盘点管理·盘点结果页。
 * 和送检 / 送修 / 报废的填写页一致的三段式：上面是盘点信息（两端对齐，不带冒号），
 * 下面是一张工器具表格（工器具信息 / 盘点结果），最底下是提交按钮。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryResultScreen(
    items: List<InventoryResultItem> = listOf(
        InventoryResultItem(
            ToolScanItem("GJ-02021", "数字万用表", "GJ-02021", "E200 2021 F001"),
            "已盘"
        ),
        InventoryResultItem(
            ToolScanItem("GJ-03025", "钳形电流表", "GJ-03025", "E200 3025 F002"),
            "已盘"
        ),
        InventoryResultItem(
            ToolScanItem("GJ-04018", "绝缘电阻测试仪", "GJ-04018", "E200 4018 G001"),
            "盘亏"
        )
    ),
    onBackClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {}
) {
    val themeBlue = Color(0xFF1E71C8)
    val bgColor = Color(0xFFF5F6F8)
    val textDark = Color(0xFF222222)
    val textGray = Color(0xFF888888)
    var showConfirmDialog by remember { mutableStateOf(false) }

    val bookCount = items.size
    val actualCount = items.count { it.result == "已盘" }
    val shortage = items.count { it.result == "盘亏" }
    val surplus = items.count { it.result == "盘盈" }
    val canSubmit = items.isNotEmpty()

    // 顶部信息行：左边键、右边值，两端对齐
    val infoRows = listOf(
        "盘点单号" to "PD20261028002",
        "盘点状态" to "已完成",
        "班组" to "检修一班",
        "盘点人" to "张伟",
        "账面数量" to "${bookCount}件",
        "实盘数量" to "${actualCount}件",
        "盘点差异" to (if (shortage > 0) "盘亏${shortage}件" else if (surplus > 0) "盘盈${surplus}件" else "无差异")
    )

    Scaffold(
        modifier = Modifier.imePadding(),
        containerColor = bgColor,
        topBar = { InventoryTopBar("盘点结果", onBackClick) },
        bottomBar = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Button(
                    onClick = { showConfirmDialog = true },
                    enabled = canSubmit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = themeBlue,
                        disabledContainerColor = Color(0xFFB0BEC5)
                    )
                ) {
                    Text(
                        "提交盘点结果",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // === 1. 盘点信息（一行一对，左键右值，不带冒号） ===
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                infoRows.forEach { (label, value) ->
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
                            color = if (label == "盘点差异" && shortage > 0) {
                                Color(0xFFE53935)
                            } else {
                                textDark
                            },
                            maxLines = 1,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }

            // === 2. 工器具表格（和送检确认页的表格一致） ===
            Column(modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(bgColor)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "工器具信息",
                        modifier = Modifier.weight(1f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = textGray
                    )
                    Text(
                        text = "盘点结果",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = textGray
                    )
                }
                HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))

                items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.tool.rfid,
                                fontSize = 14.sp,
                                lineHeight = 16.sp,
                                color = textDark
                            )
                            Text(
                                text = item.tool.name,
                                fontSize = 13.sp,
                                lineHeight = 14.sp,
                                color = textGray,
                                modifier = Modifier.padding(top = 1.dp)
                            )
                        }
                        Text(
                            text = item.result,
                            fontSize = 14.sp,
                            lineHeight = 16.sp,
                            color = if (item.result == "已盘") themeBlue else Color(0xFFE53935)
                        )
                    }
                    HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F0))
                }
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            containerColor = Color.White,
            title = { Text("确认提交盘点结果") },
            text = {
                Text(
                    "共${bookCount}件工具\n账面数量：${bookCount}件\n实盘数量：${actualCount}件\n" +
                            "盘点差异：" + (if (shortage > 0) "盘亏${shortage}件" else if (surplus > 0) "盘盈${surplus}件" else "无差异")
                )
            },
            confirmButton = {
                TextButton(onClick = { showConfirmDialog = false; onConfirmClick() }) {
                    Text("确认", color = themeBlue)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("取消", color = Color(0xFF888888))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InventoryTopBar(title: String, onBackClick: () -> Unit) {
    TopAppBar(
        modifier = Modifier.height(75.dp),
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.offset(x = (-16).dp)
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick, modifier = Modifier.offset(x = (-7).dp)) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "返回")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF1E71C8),
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White
        )
    )
}
