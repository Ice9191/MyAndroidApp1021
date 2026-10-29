package com.hxb.myandroidapp_1021.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hxb.myandroidapp_1021.view_model.RfidViewModel
import kotlin.math.roundToInt

// 定义全局颜色（蓝白配色）
val PrimaryBlue = Color(0xFF1976D2) // 顶部导航栏和按钮的蓝色
val BackgroundGray = Color(0xFFF5F5F5) // 灰色背景
val CardWhite = Color.White // 白色卡片

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolInspectionScreen(viewModel: RfidViewModel = viewModel()) {
    // 页面状态
    var searchQuery by remember { mutableStateOf("") }
    var inspectionReason by remember { mutableStateOf("") }
    var inspectionAgency by remember { mutableStateOf("") }
    var expectedReturnDate by remember { mutableStateOf("") }
    val context = LocalContext.current
    // 监听viewModel中的数据变化
    val isScanning by viewModel.isScanning.collectAsState()
    val scannedTags by viewModel.scannedTags.collectAsState()
    val power by viewModel.power.collectAsState()
    // 初始化硬件
    LaunchedEffect(Unit) {
        viewModel.initHardware()
    }
    // 监听SharedFlow错误提示并弹Toast
    LaunchedEffect(Unit) {
        viewModel.errorMessage.collect {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            // 顶部导航栏
            TopAppBar(
                title = { Text("工具送检", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { /* 返回逻辑 */ }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryBlue
                )
            )
        },
        containerColor = BackgroundGray // 整体背景灰色
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ================= 新增：开始RFID扫描按钮 =================
            Button(
                onClick = {
                    if (isScanning) viewModel.stopScan() else viewModel.startScan()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp), // 高度设置大一点，方便PDA手指点击
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isScanning) Color(0xFFE53935) else PrimaryBlue,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = "扫描图标",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isScanning) "停止 RFID 扫描" else "开始 RFID 扫描",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedButton(
                onClick = {
                    viewModel.clearList()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp), // 略矮于扫描按钮
                border = BorderStroke(1.dp, PrimaryBlue), // 蓝色描边
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PrimaryBlue // 文字和图标为蓝色
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh, // 使用刷新/清空图标
                    contentDescription = "重置列表",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "重置已读取列表",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            // =========================================================

            // 卡片：发射功率调节
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "发射功率",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Text(
                            text = "$power dBm",
                            fontSize = 14.sp,
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = power.toFloat(),
                        onValueChange = { viewModel.updatePower(it.roundToInt()) },
                        valueRange = RfidViewModel.MIN_POWER.toFloat()..RfidViewModel.MAX_POWER.toFloat(),
                        steps = RfidViewModel.MAX_POWER - RfidViewModel.MIN_POWER - 1,
                        colors = SliderDefaults.colors(
                            thumbColor = PrimaryBlue,
                            activeTrackColor = PrimaryBlue,
                            inactiveTrackColor = Color.LightGray
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "功率越大读距越远；读手边标签建议 15–25 dBm",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            // 卡片1：已扫描/待送检列表
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "已读取工器具",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        // 动态显示数量
                        Text(
                            text = "共 ${scannedTags.size} 件",
                            fontSize = 14.sp,
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 读取到的工具列表
                    if (scannedTags.isEmpty()) {
                        Text(
                            text = "暂无数据，请点击上方按钮进行扫描",
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        // 遍历scannedTags列表
                        scannedTags.forEachIndexed { index, epc ->
                            Text(
                                text = "${index + 1}. RFID：$epc",
                                color = Color.DarkGray,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(12.dp))

                    // 异常情况：搜索框（占位）
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("如果RFID无法读取，可输入名称/编码检索") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = PrimaryBlue
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color.LightGray,
                            focusedLabelColor = PrimaryBlue,
                            cursorColor = PrimaryBlue
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            // 卡片2：送检信息表单
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "送检信息填写",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = inspectionReason,
                        onValueChange = { inspectionReason = it },
                        label = { Text("送检原因") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color.LightGray,
                            focusedLabelColor = PrimaryBlue,
                            cursorColor = PrimaryBlue
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = inspectionAgency,
                        onValueChange = { inspectionAgency = it },
                        label = { Text("送检机构") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color.LightGray,
                            focusedLabelColor = PrimaryBlue,
                            cursorColor = PrimaryBlue
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = expectedReturnDate,
                        onValueChange = { expectedReturnDate = it },
                        label = { Text("预计返回时间") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color.LightGray,
                            focusedLabelColor = PrimaryBlue,
                            cursorColor = PrimaryBlue
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            // 提示信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "请核对工器具无误，填写完整信息后提交审核",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // 底部确认按钮
            Button(
                onClick = { /* 提交逻辑 */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "确认提交",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp)) // 底部留白
        }
    }
}