package com.hxb.myandroidapp_1021.components.index

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerConfigScreen(onBackClick: () -> Unit = {}, onSaveClick: () -> Unit = {}) {
    var ipAddress by remember { mutableStateOf("192.168.1.100") }
    var port by remember { mutableStateOf("8080") }
    var connectionStatus by remember { mutableStateOf("尚未测试") }
    var isTesting by remember { mutableStateOf(false) }
    val themeBlue = Color(0xFF1E71C8)
    val bgColor = Color(0xFFF7F8FA)
    val statusColor = when (connectionStatus) {
        "连接成功" -> Color(0xFF4CAF50)
        "连接失败" -> Color(0xFFF44336)
        else -> Color(0xFF1E71C8)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(75.dp),
                title = {
                    Text(
                        "服务器配置",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.offset(x = (-16).dp)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.offset(x = (-7).dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = themeBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        if (!isTesting) {
                            isTesting = true
                            connectionStatus = "正在测试..."
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, statusColor),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = statusColor,
                        disabledContentColor = statusColor,
                        disabledContainerColor = Color.Transparent
                    ),
                    enabled = !isTesting
                ) {
                    Text(text = connectionStatus, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onSaveClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = themeBlue)
                ) {
                    Text(text = "保存并退出", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = bgColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // 连接设置区
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                // IP 地址输入框
                Text(text = "IP地址", fontSize = 14.sp, color = Color(0xFF666666))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = ipAddress,
                    onValueChange = {
                        ipAddress = it
                        connectionStatus = "尚未测试"
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(4.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedBorderColor = themeBlue,
                        cursorColor = themeBlue
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 端口号输入框
                Text(text = "端口号", fontSize = 14.sp, color = Color(0xFF666666))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = port,
                    onValueChange = {
                        port = it
                        connectionStatus = "尚未测试"
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(4.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedBorderColor = themeBlue,
                        cursorColor = themeBlue
                    )
                )
            }

            // 模拟网络请求测试按钮效果
            LaunchedEffect(isTesting) {
                if (isTesting) {
                    delay(1500.milliseconds)
                    connectionStatus = if (ipAddress.isNotEmpty() && port.isNotEmpty()) {
                        "连接成功"
                    } else {
                        "连接失败"
                    }
                    isTesting = false
                }
            }
        }
    }
}