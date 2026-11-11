package com.hxb.myandroidapp_1021.ui.picking

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hxb.myandroidapp_1021.components.InputFieldOne
import com.hxb.myandroidapp_1021.components.ToolsInfoList
import com.hxb.myandroidapp_1021.data.models.DisplayField
import com.hxb.myandroidapp_1021.data.models.ToolInfoRow

@Composable
fun PickingConfirmScreen(
    viewModel: PickingConfirmViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PickingConfirmContent(
        uiState = uiState,
        onEmployeeIdChange = viewModel::onEmployeeIdChange,
        onBackClick = onBackClick,
        onHomeClick = onHomeClick,
        onConfirmClick = { viewModel.confirmOutbound(onSuccess = onConfirmClick) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickingConfirmContent(
    uiState: PickingConfirmUiState,
    onEmployeeIdChange: (String) -> Unit,
    onBackClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    val themeBlue = Color(0xFF1E71C8)
    val focusManager = LocalFocusManager.current
    val bgColor = Color(0xFFF5F6F8)
    val textDark = Color(0xFF222222)
    val textGray = Color(0xFF888888)
    // 根据状态生成展示字段
    val infoFields = listOf(
        DisplayField("姓名", uiState.borrower),
        DisplayField("班组", uiState.workshop),
        DisplayField("经办人", uiState.handler),
        DisplayField("出库时间", uiState.outboundTime)
    )
    val toolRows = uiState.readTools.map { ToolInfoRow(rfid = it.rfid, name = it.name) }

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
                        onClick = { focusManager.clearFocus(); onHomeClick() },
                        modifier = Modifier.offset(x = (-4).dp)
                    ) {
                        Icon(Icons.Default.Home, contentDescription = "Home", tint = Color.White)
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
                    colors = ButtonDefaults.buttonColors(containerColor = themeBlue),
                    enabled = !uiState.isSubmitting
                ) {
                    if (uiState.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "确认出库",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = themeBlue)
            }
        } else {
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
                        text = "已读取${uiState.readTools.size}件 / 未读取${uiState.unreadCount}件",
                        fontSize = 14.sp,
                        color = textDark
                    )
                }
                HorizontalDivider(thickness = 8.dp, color = Color(0xFFE0E0E0))

                // === 2. 领料人工号 + 信息行 ===
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Text(
                        text = "领料人信息",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = textDark,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    InputFieldOne(
                        value = uiState.employeeId,
                        onValueChange = onEmployeeIdChange,
                        placeholder = "请输入领料人工号",
                        showSearchIcon = false,
                        errorMessage = uiState.errorMessage,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                    )
                    if (uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 20.dp, top = 4.dp)
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
                                Text(field.label, fontSize = 13.sp, color = textGray)
                                Text(field.value, fontSize = 13.sp, color = textDark)
                            }
                        }
                    }
                }

                // === 3. 工器具表格 ===
                ToolsInfoList(rows = toolRows)
            }
        }
    }
}