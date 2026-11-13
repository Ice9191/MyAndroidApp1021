package com.hxb.myandroidapp_1021.ui.returns

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.hxb.myandroidapp_1021.theme.AppColors
import com.hxb.myandroidapp_1021.components.TopAppBar
import com.hxb.myandroidapp_1021.components.ConfirmDialog
import com.hxb.myandroidapp_1021.components.InputFieldOne
import com.hxb.myandroidapp_1021.components.ToolsInfoList
import com.hxb.myandroidapp_1021.ui.commonmodel.ToolInfoRow

@Composable
fun ReturnConfirmScreen(
    scanViewModel: ReturnScanViewModel,
    viewModel: ReturnConfirmViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scanState by scanViewModel.uiState.collectAsStateWithLifecycle()
    ReturnConfirmContent(
        uiState = uiState,
        scanState = scanState,
        onEmployeeIdChange = viewModel::onEmployeeIdChange,
        onLocationChange = viewModel::onLocationChange,
        onBindLocation = viewModel::bindLocation,
        onBackClick = onBackClick,
        onConfirmClick = {
            viewModel.confirmReturn(
                scanState.order,
                scanState.readList,
                onConfirmClick
            )
        },
        onHomeClick = onHomeClick
    )
}

@Composable
fun ReturnConfirmContent(
    uiState: ReturnConfirmUiState,
    scanState: ReturnScanUiState,
    onEmployeeIdChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onBindLocation: () -> Unit,
    onBackClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    val themeBlue = MaterialTheme.colorScheme.primary
    val focusManager = LocalFocusManager.current
    val bgColor = MaterialTheme.colorScheme.background
    val textDark = MaterialTheme.colorScheme.onSurface
    val tools = scanState.readList
    val orderNo = scanState.order?.orderNo.orEmpty()
    var showConfirmDialog by remember { mutableStateOf(false) }
    val canComplete = tools.isNotEmpty() && uiState.canConfirm

    Scaffold(
        modifier = Modifier.imePadding(),
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = "工单归还上架·绑定库位",
                modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
                onBackClick = { focusManager.clearFocus(); onBackClick() },
                onHomeClick = { focusManager.clearFocus(); onHomeClick() }
            )
        },
        bottomBar = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
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
                        disabledContainerColor = AppColors.DisabledContainer
                    )
                ) {
                    Text(
                        if (uiState.isSubmitting) "提交中" else "完成",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface)
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
            HorizontalDivider(thickness = 8.dp, color = MaterialTheme.colorScheme.outlineVariant)
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
                InputFieldOne(
                    value = uiState.employeeId,
                    onValueChange = onEmployeeIdChange,
                    placeholder = "请输入归还人工号",
                    showSearchIcon = false,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
            }
            HorizontalDivider(thickness = 8.dp, color = MaterialTheme.colorScheme.outlineVariant)
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
                InputFieldOne(
                    value = uiState.locationCode,
                    onValueChange = onLocationChange,
                    placeholder = "输入库位",
                    showSearchIcon = false,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        onBindLocation()
                    },
                    enabled = uiState.locationCode.isNotBlank() && !uiState.isSubmitting,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = themeBlue,
                        disabledContainerColor = AppColors.DisabledContainer
                    )
                ) {
                    Text(
                        "扫描库码",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            HorizontalDivider(thickness = 8.dp, color = MaterialTheme.colorScheme.surface)
            ToolsInfoList(
                rows = tools.map { ToolInfoRow(rfid = it.rfid, name = it.name) },
            )
        }
    }
    if (showConfirmDialog) {
        ConfirmDialog(
            title = "确认上架",
            onConfirm = {
                showConfirmDialog = false
                onConfirmClick()
            },
            onDismiss = { showConfirmDialog = false }
        ) {
            Text("确认${tools.size}件工具已放入 ${uiState.boundLocation}？当前仅演示页面流程，不提交归还记录。")
        }
    }
}
