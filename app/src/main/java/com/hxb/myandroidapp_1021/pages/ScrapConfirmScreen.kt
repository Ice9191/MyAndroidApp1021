package com.hxb.myandroidapp_1021.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxb.myandroidapp_1021.components.DateField
import com.hxb.myandroidapp_1021.components.ToolInfoTable
import com.hxb.myandroidapp_1021.models.ToolInfoRow
import com.hxb.myandroidapp_1021.models.ToolScanItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrapConfirmScreen(
    tools: List<ToolScanItem> = listOf(
        ToolScanItem(
            "GJ-01055",
            "安全帽",
            "GJ-01055",
            "E200 1055 C001"
        )
    ),
    onBackClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
    onHomeClick:()-> Unit={}
) {
    val themeBlue = Color(0xFF1E71C8)
    val focusManager = LocalFocusManager.current
    val bgColor = Color(0xFFF5F6F8)
    val textDark = Color(0xFF222222)
    var reason by remember { mutableStateOf("") }
    var scrapCount by remember { mutableStateOf("") }
    var organization by remember { mutableStateOf("") }
    var returnDate by remember { mutableStateOf("") }
    var showConfirmDialog by remember { mutableStateOf(false) }
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = themeBlue,
        unfocusedBorderColor = Color(0xFFDDDDDD),
        cursorColor = themeBlue
    )
    val canSubmit =
        tools.isNotEmpty() && reason.isNotBlank() && scrapCount.isNotBlank() &&
                organization.isNotBlank() && returnDate.isNotBlank()

    Scaffold(
        modifier = Modifier.imePadding(),
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .height(75.dp)
                    .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
                title = {
                    Text(
                        text = "填写信息",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.offset(x = (-16).dp)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            focusManager.clearFocus()
                            onBackClick()
                        },
                        modifier = Modifier.offset(x = (-7).dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            focusManager.clearFocus()
                            onHomeClick()
                        },
                        modifier = Modifier.offset(x = (-4).dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E71C8),
                    titleContentColor = Color.White,
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
                    onClick = { focusManager.clearFocus(); showConfirmDialog = true },
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
                        "确认",
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
                .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
        ) {
            // === 1. 表单：报废原因 / 报废机构 / 报废日期 ===
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Text("报废原因", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textDark)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    placeholder = { Text("请输入报废原因") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(4.dp),
                    colors = fieldColors
                )
                Spacer(Modifier.height(16.dp))
                Text("报废数量", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textDark)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = scrapCount,
                    onValueChange = { scrapCount = it },
                    placeholder = { Text("请输入报废数量") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    suffix = { Text("件") },
                    shape = RoundedCornerShape(4.dp),
                    colors = fieldColors
                )
                Spacer(Modifier.height(16.dp))
                Text("报废机构", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textDark)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = organization,
                    onValueChange = { organization = it },
                    placeholder = { Text("请输入报废机构") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(4.dp),
                    colors = fieldColors
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    "报废日期",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = textDark
                )
                Spacer(Modifier.height(8.dp))
                DateField(
                    value = returnDate,
                    onValueChange = { returnDate = it },
                    placeholder = "请选择报废日期",
                    contentDescription = "选择报废日期"
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    "报废现场图片（选填）",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = textDark
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { /* 拍照功能暂留占位 */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CameraAlt,
                            contentDescription = null,
                            tint = themeBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(" 拍照", fontSize = 13.sp, color = themeBlue)
                    }
                    OutlinedButton(
                        onClick = { /* 相册选择暂留占位 */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PhotoLibrary,
                            contentDescription = null,
                            tint = themeBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(" 从相册选择", fontSize = 13.sp, color = themeBlue)
                    }
                }
            }

            // === 2. 工器具表格 ===
            ToolInfoTable(
                rows = tools.map { ToolInfoRow(rfid = it.rfid, name = it.name) },
                rowVerticalPadding = 6
            )
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            containerColor = Color.White,
            title = { Text("确认报废申请") },
            text = {
                Text(
                    "共${tools.size}件工具\n报废原因：${reason.trim()}\n报废数量：${scrapCount.trim()}件\n" +
                            "报废机构：${organization.trim()}\n报废日期：$returnDate"
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
