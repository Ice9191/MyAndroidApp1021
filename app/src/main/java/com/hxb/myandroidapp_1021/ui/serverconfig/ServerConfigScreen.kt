package com.hxb.myandroidapp_1021.ui.serverconfig

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hxb.myandroidapp_1021.components.TopAppBar
import com.hxb.myandroidapp_1021.theme.AppColors

@Composable
fun ServerConfigScreen(
    viewModel: ServerConfigViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ServerConfigContent(
        uiState = uiState,
        onIpAddressChange = viewModel::onIpAddressChange,
        onPortChange = viewModel::onPortChange,
        onTestClick = viewModel::testConnection,
        onSaveClick = { viewModel.saveConfig(onSaveClick) },
        onBackClick = onBackClick
    )
}

@Composable
fun ServerConfigContent(
    uiState: ServerConfigUiState,
    onIpAddressChange: (String) -> Unit,
    onPortChange: (String) -> Unit,
    onTestClick: () -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val themeBlue = MaterialTheme.colorScheme.primary
    val bgColor = MaterialTheme.colorScheme.background
    val statusText = when (uiState.connectionStatus) {
        ConnectionStatus.UNTESTED -> "尚未测试"
        ConnectionStatus.TESTING -> "正在测试..."
        ConnectionStatus.SUCCESS -> "连接成功"
        ConnectionStatus.FAILURE -> "连接失败"
    }
    val statusColor = when (uiState.connectionStatus) {
        ConnectionStatus.SUCCESS -> AppColors.Success
        ConnectionStatus.FAILURE -> MaterialTheme.colorScheme.error
        else -> themeBlue
    }
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopAppBar(
                title = "服务器配置",
                modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
                onBackClick = { focusManager.clearFocus(); onBackClick() }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        focusManager.clearFocus()
                        onTestClick()
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
                    enabled = !uiState.isTesting
                ) {
                    Text(text = statusText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { focusManager.clearFocus(); onSaveClick() },
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
                .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
        ) {
            // 连接设置区
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                // IP 地址输入框
                Text(
                    text = "IP地址",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.ipAddress,
                    onValueChange = onIpAddressChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(4.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedBorderColor = themeBlue,
                        cursorColor = themeBlue
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 端口号输入框
                Text(
                    text = "端口号",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.port,
                    onValueChange = onPortChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(4.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedBorderColor = themeBlue,
                        cursorColor = themeBlue
                    )
                )
            }
        }
    }
}
