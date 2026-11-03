package com.hxb.myandroidapp_1021.components.index

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxb.myandroidapp_1021.components.index.components.ApplicationTopBar
import com.hxb.myandroidapp_1021.components.index.components.VerificationButton
import com.hxb.myandroidapp_1021.components.index.components.VerificationTabs
import com.hxb.myandroidapp_1021.components.index.components.VerificationToolRow

@Composable
fun InventoryScanScreen(onBackClick: () -> Unit = {}, onReturnToListClick: () -> Unit = {}) {
    val themeBlue = Color(0xFF1E71C8)
    val bgColor = Color(0xFFF5F6F8)
    var selectedTab by remember { mutableIntStateOf(0) }
    var showResult by remember { mutableStateOf(false) }
    // shortcut: 当前盘点记录均为示例，RFID读取和后台比对接口待接入。
    val readTools = remember {
        listOf(
            ToolScanItem("GJ-02021", "绝缘电阻表", "GJ-02021", "E200 2021 F001"),
            ToolScanItem("GJ-03025", "钳形表", "GJ-03025", "E200 3025 F002")
        ) + (3..18).map { index ->
            ToolScanItem(
                "PD-DEMO-$index",
                "示例工具$index",
                "GJ-${2000 + index}",
                "E200 20${index.toString().padStart(2, '0')} F${index.toString().padStart(3, '0')}"
            )
        }
    }
    val pendingTools = remember {
        listOf(
            ToolScanItem("GJ-02036", "绝缘扳手", "GJ-02036", "E200 2036 F024"),
            ToolScanItem("GJ-02037", "验电器", "GJ-02037", "E200 2037 F019"),
            ToolScanItem("GJ-02038", "接地线", "GJ-02038", "E200 2038 F020"),
            ToolScanItem("GJ-02039", "绝缘手套", "GJ-02039", "E200 2039 F021"),
            ToolScanItem("GJ-02040", "安全带", "GJ-02040", "E200 2040 F022"),
            ToolScanItem("GJ-02041", "绝缘靴", "GJ-02041", "E200 2041 F023")
        )
    }
    val bookCount = readTools.size + pendingTools.size

    if (showResult) {
        BackHandler { showResult = false }
        InventoryResultScreen(
            bookCount = bookCount, actualCount = readTools.size,
            differences = pendingTools.map { InventoryDifference(it, 1, 0) },
            onBackClick = { showResult = false }, onReturnToListClick = onReturnToListClick
        )
        return
    }

    Scaffold(
        containerColor = bgColor,
        topBar = { ApplicationTopBar("盘点·RFID读取", onBackClick) },
        bottomBar = { VerificationButton("提交后台比对", onClick = { showResult = true }) }
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
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            "任务单号：PD20261028002",
                            modifier = Modifier.weight(1f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF222222)
                        )
                        Text("进行中", fontSize = 13.sp, color = themeBlue)
                    }
                    Text("范围：中心工具库 / B区-05柜", fontSize = 13.sp, color = Color(0xFF222222))
                    Text(
                        "账面${bookCount}件 · 已盘${readTools.size}件 · 待盘${pendingTools.size}件",
                        fontSize = 13.sp,
                        color = Color(0xFF222222)
                    )
                }
                HorizontalDivider(thickness = 8.dp, color = bgColor)
                VerificationButton("RFID读取（占位）", onClick = {}, enabled = false)
                HorizontalDivider(thickness = 8.dp, color = bgColor)
                VerificationTabs(
                    listOf("已盘 ${readTools.size}", "待盘 ${pendingTools.size}"),
                    selectedTab
                ) { selectedTab = it }
            }
            items(if (selectedTab == 0) readTools else pendingTools, key = { it.id }) { tool ->
                VerificationToolRow(tool, if (selectedTab == 0) "RFID读取" else "未扫描")
            }
            item {
                Spacer(Modifier
                    .height(20.dp)
                    .fillMaxWidth()
                    .background(Color.White))
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun InventoryScanScreenPreview() {
    InventoryScanScreen()
}
