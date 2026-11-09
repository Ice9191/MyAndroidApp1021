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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxb.myandroidapp_1021.components.SearchField
import com.hxb.myandroidapp_1021.components.ToolInfoTable
import com.hxb.myandroidapp_1021.models.ToolInfoRow
import com.hxb.myandroidapp_1021.models.ToolScanItem
import com.hxb.myandroidapp_1021.routes.ReturnConfirm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReturnConfirmScreen(
    order: ReturnConfirm? = null,
    tools: List<ToolScanItem> = listOf(
        ToolScanItem("GJ-01118", rfid = "E200 1118 B001"),
        ToolScanItem("GJ-01155", rfid = "E200 1155 B002"),
        ToolScanItem("GJ-01162", rfid = "E200 1162 B003")
    ),
    orderNo: String = order?.orderNo ?: "JC20261027006",
    onBackClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    val themeBlue = Color(0xFF1E71C8)
    val focusManager = LocalFocusManager.current
    val bgColor = Color(0xFFF5F6F8)
    val textDark = Color(0xFF222222)
    val textGray = Color(0xFF888888)
    val employeeIdState = rememberTextFieldState("100268")
    val locationState = rememberTextFieldState()
    var boundLocation by remember { mutableStateOf("") }
    var showConfirmDialog by remember { mutableStateOf(false) }
    val canComplete =
        tools.isNotEmpty() && employeeIdState.text.isNotBlank() && boundLocation.isNotBlank()

    LaunchedEffect(locationState) {
        snapshotFlow { locationState.text.toString() }.collect { boundLocation = "" }
    }

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
                        "归还·绑定库位", fontSize = 18.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.offset(x = (-16).dp)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { focusManager.clearFocus(); onBackClick() },
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
                    onClick = { focusManager.clearFocus(); showConfirmDialog = true },
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
                .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "原借出单：$orderNo",
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
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    "归还人工号",
                    fontSize = 14.sp,
                    color = textDark,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                SearchField(
                    state = employeeIdState,
                    placeholder = "请输入归还人工号",
                    showSearchIcon = false
                )
            }
            HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    "上架库位",
                    fontSize = 14.sp,
                    color = textDark,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                SearchField(
                    state = locationState,
                    placeholder = "输入库位",
                    showSearchIcon = false
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        focusManager.clearFocus(); boundLocation =
                        locationState.text.toString().trim()
                    },
                    enabled = locationState.text.isNotBlank(),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
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
            HorizontalDivider(thickness = 8.dp, color = Color.White)
            ToolInfoTable(
                rows = tools.map { ToolInfoRow(rfid = it.rfid, name = it.name) },
                infoHeader = "RFID",
                showName = false,
                rowVerticalPadding = 6
            )
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
