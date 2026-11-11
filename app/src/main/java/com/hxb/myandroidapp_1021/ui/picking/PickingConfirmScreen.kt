package com.hxb.myandroidapp_1021.ui.picking

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hxb.myandroidapp_1021.data.models.DisplayField
import com.hxb.myandroidapp_1021.data.models.ToolInfoRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickingConfirmScreen(
    state: PickingScanViewModel,
    onBackClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    val uiState by state.uiState.collectAsStateWithLifecycle()
    val themeBlue = Color(0xFF1E71C8)
    val focusManager = LocalFocusManager.current
    val bgColor = Color(0xFFF5F6F8)
    val textDark = Color(0xFF222222)
    val textGray = Color(0xFF888888)
    val employeeIdState = rememberTextFieldState("")
    val toolList = uiState.readList
    val infoFields = listOf(
        DisplayField("姓名", uiState.borrower),
        DisplayField("班组", uiState.workshop),
        DisplayField("经办人", "王芳（材料员）"),
        DisplayField("出库时间", "2026-10-30 09:30")
    )

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
                        text = "借出·出库确认",
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
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
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
                    onClick = { focusManager.clearFocus(); onConfirmClick() },
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
                .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
        ) {
            // === 1. 单据头部 ===
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
                        text = "拣选单号：${uiState.orderNo}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textDark
                    )
                    Text(text = uiState.orderStatus.label, fontSize = 14.sp, color = themeBlue)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "已读取${toolList.size}件 / 未读取${uiState.unreadList.size}件",
                    fontSize = 14.sp,
                    color = textDark
                )
            }
            HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))

            // === 2. 领料人工号 + 信息行 ===
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = "领料人工号",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = textDark,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        state = employeeIdState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp),
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = Color(0xFF333333)
                        ),
                        placeholder = {
                            Text(
                                text = "请输入领料人工号",
                                fontSize = 14.sp,
                                color = Color(0xFF999999)
                            )
                        },
                        lineLimits = TextFieldLineLimits.SingleLine,
                        shape = RoundedCornerShape(4.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF333333),
                            unfocusedTextColor = Color(0xFF333333),
                            focusedContainerColor = Color(0xFFF5F6F8),
                            unfocusedContainerColor = Color(0xFFF5F6F8),
                            focusedBorderColor = themeBlue,
                            unfocusedBorderColor = Color.Transparent,
                            cursorColor = themeBlue
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    infoFields.forEach { field ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = field.label,
                                fontSize = 13.sp,
                                lineHeight = 16.sp,
                                color = textGray,
                                maxLines = 1
                            )
                            Text(
                                text = field.value,
                                fontSize = 13.sp,
                                lineHeight = 16.sp,
                                color = textDark,
                                maxLines = 1,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
            }

            // === 3. 工器具表格 ===
            val toolRows = toolList.map { ToolInfoRow(rfid = it.rfid, name = it.name) }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                // 表头
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(Color(0xFFF5F6F8))
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
                        text = "数量/件",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = textGray
                    )
                }
                HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))

                // 数据行
                toolRows.forEach { row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = row.rfid,
                                fontSize = 14.sp,
                                lineHeight = 16.sp,
                                color = textDark
                            )
                            if (row.name.isNotBlank()) {
                                Text(
                                    text = row.name,
                                    fontSize = 13.sp,
                                    lineHeight = 14.sp,
                                    color = textGray,
                                    modifier = Modifier.padding(top = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = row.value ?: "${row.quantity}",
                            fontSize = 14.sp,
                            lineHeight = 16.sp,
                            color = row.valueColor ?: textDark
                        )
                    }
                    HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F0))
                }
            }
        }
    }
}
