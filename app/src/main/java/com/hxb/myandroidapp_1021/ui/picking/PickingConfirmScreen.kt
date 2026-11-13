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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hxb.myandroidapp_1021.components.TopAppBar
import com.hxb.myandroidapp_1021.components.InputFieldOne
import com.hxb.myandroidapp_1021.components.ToolsInfoList
import com.hxb.myandroidapp_1021.ui.commonmodel.DisplayField
import com.hxb.myandroidapp_1021.ui.commonmodel.ToolInfoRow

@Composable
fun PickingConfirmScreen(
    scanViewModel: PickingScanViewModel,
    viewModel: PickingConfirmViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scanState by scanViewModel.uiState.collectAsStateWithLifecycle()

    PickingConfirmContent(
        uiState = uiState,
        scanState = scanState,
        onEmployeeIdChange = viewModel::onEmployeeIdChange,
        onBackClick = onBackClick,
        onHomeClick = onHomeClick,
        onConfirmClick = {
            viewModel.confirmOutbound(
                scanState.order,
                scanState.readList,
                onConfirmClick
            )
        }
    )
}

@Composable
fun PickingConfirmContent(
    uiState: PickingConfirmUiState,
    scanState: PickingScanUiState,
    onEmployeeIdChange: (String) -> Unit,
    onBackClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    val themeBlue = MaterialTheme.colorScheme.primary
    val focusManager = LocalFocusManager.current
    val bgColor = MaterialTheme.colorScheme.background
    val textDark = MaterialTheme.colorScheme.onSurface
    val textGray = MaterialTheme.colorScheme.onSurfaceVariant
    // 根据状态生成展示字段
    val infoFields = listOf(
        DisplayField("姓名", scanState.order?.borrower.orEmpty()),
        DisplayField("班组", scanState.order?.workshop.orEmpty()),
        DisplayField("经办人", uiState.handler),
        DisplayField("出库时间", uiState.outboundTime)
    )
    val toolRows = scanState.readList.map { ToolInfoRow(rfid = it.rfid, name = it.name) }

    Scaffold(
        modifier = Modifier.imePadding(),
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = "工单拣选·确认",
                modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
                onBackClick = { focusManager.clearFocus(); onBackClick() },
                onHomeClick = { focusManager.clearFocus(); onHomeClick() }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Button(
                    onClick = { focusManager.clearFocus(); onConfirmClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = themeBlue),
                    enabled = !uiState.isLoading && !uiState.isSubmitting && scanState.readList.isNotEmpty()
                ) {
                    if (uiState.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "确认出库",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
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
                    .background(MaterialTheme.colorScheme.surface)
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
                            text = "拣选单号：${scanState.order?.orderNo.orEmpty()}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = textDark
                        )
                        Text(
                            text = scanState.order?.status?.label.orEmpty(),
                            fontSize = 14.sp,
                            color = themeBlue
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "已读取${scanState.readList.size}件 / 未读取${scanState.unreadList.size}件",
                        fontSize = 14.sp,
                        color = textDark
                    )
                }
                HorizontalDivider(thickness = 8.dp, color = MaterialTheme.colorScheme.outlineVariant)

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
                            color = MaterialTheme.colorScheme.error,
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