package com.hxb.myandroidapp_1021.components.index

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReturnConfirmScreen(
    tools: List<ToolScanItem> = listOf(
        ToolScanItem("GJ-01118", rfid = "E200 1118 B001"),
        ToolScanItem("GJ-01155", rfid = "E200 1155 B002"),
        ToolScanItem("GJ-01162", rfid = "E200 1162 B003")
    ),
    onBackClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {}
) {
    val themeBlue = Color(0xFF1E71C8)
    val bgColor = Color(0xFFF5F6F8)
    val textDark = Color(0xFF222222)
    val textGray = Color(0xFF888888)
    var employeeId by remember { mutableStateOf("100268") }
    var locationInput by remember { mutableStateOf("") }
    var boundLocation by remember { mutableStateOf("") }
    var showConfirmDialog by remember { mutableStateOf(false) }
    val canComplete = tools.isNotEmpty() && employeeId.isNotBlank() && boundLocation.isNotBlank()

    Scaffold(
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                modifier = Modifier.height(75.dp),
                title = {
                    Text(
                        "归还·绑定库位", fontSize = 18.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.offset(x = (-16).dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.offset(x = (-7).dp)) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = themeBlue, titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Button(
                    onClick = { showConfirmDialog = true },
                    enabled = canComplete,
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
                        "完成",
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
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            Column(Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
                Row(
                    Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "原借出单：JC20261027006",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textDark
                    )
                    Text("待上架", fontSize = 14.sp, color = themeBlue)
                }
                Text(
                    "本次${tools.size}件已核对",
                    fontSize = 14.sp,
                    color = textDark,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))
            Column(Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
                Text("归还人工号", fontSize = 14.sp, color = textDark, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = employeeId,
                    onValueChange = { employeeId = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(4.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = themeBlue,
                        unfocusedBorderColor = Color(0xFFDDDDDD),
                        cursorColor = themeBlue
                    )
                )
            }
            HorizontalDivider(thickness = 8.dp, color = bgColor)
            Column(Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
                Text("上架库位", fontSize = 14.sp, color = textDark, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = locationInput,
                    onValueChange = { locationInput = it; boundLocation = "" },
                    placeholder = { Text("输入库位，如：中心工具库 / A区-03柜") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(4.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = themeBlue,
                        unfocusedBorderColor = Color(0xFFDDDDDD),
                        cursorColor = themeBlue
                    )
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { boundLocation = locationInput.trim() },
                    enabled = locationInput.isNotBlank(),
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
                        "扫描库码",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            HorizontalDivider(thickness = 8.dp, color = bgColor)
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(bgColor)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    "RFID",
                    modifier = Modifier.weight(1f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = textGray
                )
                Text("数量/件", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textGray)
            }
            HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))
            tools.forEach { tool ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        tool.rfid,
                        modifier = Modifier.weight(1f),
                        fontSize = 14.sp,
                        color = textDark
                    )
                    Text("1", fontSize = 14.sp, color = textDark)
                }
                HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F0))
            }
            Spacer(Modifier.height(20.dp))
        }
    }
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("确认上架") },
            text = { Text("确认${tools.size}件工具已放入 $boundLocation？当前仅演示页面流程，不提交归还记录。") },
            containerColor = Color.White,
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
                        color = textGray
                    )
                }
            }
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun ReturnConfirmScreenPreview() {
    ReturnConfirmScreen()
}
