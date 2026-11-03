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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickingConfirmScreen(
    onBackClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {}
) {
    val themeBlue = Color(0xFF1E71C8)
    val bgColor = Color(0xFFF5F6F8)
    val textDark = Color(0xFF222222)
    val textGray = Color(0xFF888888)

    // 模拟读取到的 RFID 列表
    val rfidList = remember {
        listOf(
            "E200 1026 A001",
            "E200 1018 A002",
            "E200 1018 A002",
            "E200 1018 A002",
            "E200 1018 A002",
            "E200 1018 A002",
            "E200 1018 A002",
            "E200 1031 A003"
        )
    }

    var employeeId by remember { mutableStateOf("100268") }

    Scaffold(
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                modifier = Modifier.height(75.dp),
                title = {
                    Text(
                        text = "借出·出库确认",
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
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Button(
                    onClick = onConfirmClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = themeBlue)
                ) {
                    Text(
                        text = "确认出库",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            // === 1. 头部信息 ===
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "拣选单号：JC20261028001",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textDark
                    )
                    Text(text = "待提交", fontSize = 14.sp, color = themeBlue)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "已读取${rfidList.size}件 / 未读取0件",
                    fontSize = 14.sp,
                    color = textDark
                )
            }

            HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))

            // === 2. 中间：领料人信息及填写 ===
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "领料人工号",
                    fontSize = 14.sp,
                    color = textDark,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
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

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = buildAnnotatedString {
                        append("姓名：")
                        withStyle(
                            style = SpanStyle(
                                color = textDark
                            )
                        ) {
                            append("张伟")
                        }
                        append("   班组：")
                        withStyle(
                            style = SpanStyle(
                                color = textDark
                            )
                        ) {
                            append("检修一班")
                        }
                    },
                    fontSize = 14.sp,
                    color = textDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "经办人：王芳（材料员）",
                    fontSize = 14.sp,
                    color = textDark
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "出库时间：2026-10-30 09:30",
                    fontSize = 14.sp,
                    color = textDark
                )
            }

            HorizontalDivider(thickness = 8.dp, color = bgColor)

            // === 3. 底部：读取到的 RFID 列表 ===
            Column(modifier = Modifier.fillMaxWidth()) {
                // 表头
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F6F8))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RFID",
                        modifier = Modifier.weight(1f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = textGray
                    )
                    Text(
                        text = "数量/件",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = textGray
                    )
                }
                HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))

                // 列表内容
                rfidList.forEach { rfid ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = rfid,
                            modifier = Modifier.weight(1f),
                            fontSize = 14.sp,
                            color = textDark
                        )
                        Text(
                            text = "1",
                            fontSize = 14.sp,
                            color = textDark
                        )
                    }
                    HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F0))
                }

                // 底部留白，防止贴边
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}