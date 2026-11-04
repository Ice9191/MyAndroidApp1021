package com.hxb.myandroidapp_1021.components.index

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
import androidx.compose.ui.Alignment
import com.hxb.myandroidapp_1021.components.index.ToolScanItem
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults

data class InventoryDifference(val tool: ToolScanItem, val bookCount: Int, val actualCount: Int)

@Composable
fun InventoryResultScreen(
    bookCount: Int = 24,
    actualCount: Int = 23,
    differences: List<InventoryDifference> = listOf(
        InventoryDifference(ToolScanItem("GJ-02036", "绝缘扳手", "GJ-02036", "E200 2036 F024"), 1, 0)
    ),
    onBackClick: () -> Unit = {},
    onReturnToListClick: () -> Unit = {}
) {
    val themeBlue = Color(0xFF1E71C8)
    val bgColor = Color(0xFFF5F6F8)
    val textDark = Color(0xFF222222)
    val textGray = Color(0xFF888888)
    val shortage = differences.sumOf { (it.bookCount - it.actualCount).coerceAtLeast(0) }
    val surplus = differences.sumOf { (it.actualCount - it.bookCount).coerceAtLeast(0) }

    Scaffold(
        containerColor = bgColor,
        topBar = { ApplicationTopBar("盘点·后台比对结果", onBackClick) },
        bottomBar = { VerificationButton("返回任务列表", onClick = onReturnToListClick) }
    ) { paddingValues ->
        LazyColumn(Modifier.fillMaxSize().padding(paddingValues)) {
            item {
                Column(Modifier.fillMaxWidth().background(Color.White).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("任务单号：PD20261028002", modifier = Modifier.weight(1f), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textDark)
                        Text("示例结果", fontSize = 13.sp, color = themeBlue)
                    }
                    Text("范围：中心工具库 / B区-05柜", fontSize = 13.sp, color = textDark)
                }
                HorizontalDivider(thickness = 8.dp, color = bgColor)
                Column(Modifier.fillMaxWidth().background(Color.White).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("后台返回盘点结果", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textDark)
                    Text("账面${bookCount}件    最终实盘${actualCount}件", fontSize = 14.sp, color = textDark)
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("盘亏${shortage}件", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE53935))
                        Text("盘盈${surplus}件", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = themeBlue)
                    }
                }
                HorizontalDivider(thickness = 8.dp, color = bgColor)
            }
            if (differences.isEmpty()) {
                item {
                    Text("未发现盘盈盘亏", fontSize = 14.sp, color = textGray,
                        modifier = Modifier.fillMaxWidth().background(Color.White).padding(16.dp))
                }
            }
            items(differences, key = { it.tool.id }) { difference ->
                val delta = difference.actualCount - difference.bookCount
                Column(Modifier.fillMaxWidth().background(Color.White).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(difference.tool.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textDark, modifier = Modifier.weight(1f))
                        Text("待处理", fontSize = 13.sp, color = Color(0xFFC58B36))
                    }
                    Text("工具编码：${difference.tool.code}", fontSize = 13.sp, color = textDark)
                    Text("RFID：${difference.tool.rfid}", fontSize = 13.sp, color = textGray)
                    Text(
                        "账面 ${difference.bookCount}    /    实盘 ${difference.actualCount}    /    差异 ${if (delta > 0) "+$delta" else "$delta"}",
                        fontSize = 13.sp, color = textDark,
                        modifier = Modifier.fillMaxWidth().background(bgColor).padding(12.dp)
                    )
                }
                HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F0))
            }
            item {
                Spacer(Modifier.height(20.dp).fillMaxWidth().background(Color.White))
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun InventoryResultScreenPreview() {
    InventoryResultScreen()
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
