package com.hxb.myandroidapp_1021.feature.serverconfig.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hxb.myandroidapp_1021.core.ui.components.ToastNotification
import com.hxb.myandroidapp_1021.core.ui.components.TopAppBar
import com.hxb.myandroidapp_1021.core.ui.theme.AppColors

@Composable
fun ServerConfigScreen(
    viewModel: ServerConfigViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ServerConfigContent(
        uiState = uiState,
        onIpAddressChange = viewModel::onIpAddressChange,
        onPortChange = viewModel::onPortChange,
        onTestClick = viewModel::testConnection,
        onNotificationDismiss = viewModel::dismissNotification,
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
    onNotificationDismiss: (Int) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val themeBlue = MaterialTheme.colorScheme.primary
    val bgColor = MaterialTheme.colorScheme.background
    val connectionStatus =
        if (uiState.isTesting) ConnectionStatus.TESTING else uiState.connectionStatus
    val statusText = when (connectionStatus) {
        ConnectionStatus.UNTESTED -> "尚未测试"
        ConnectionStatus.TESTING -> "正在测试..."
        ConnectionStatus.SUCCESS -> "连接成功"
        ConnectionStatus.FAILURE -> "连接失败"
    }
    val statusColor = when (connectionStatus) {
        ConnectionStatus.SUCCESS -> AppColors.Success
        ConnectionStatus.FAILURE -> MaterialTheme.colorScheme.error
        else -> themeBlue
    }
    val actionsEnabled = !uiState.isTesting && !uiState.isSaving
    val focusManager = LocalFocusManager.current

    Scaffold(
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
                    enabled = actionsEnabled
                ) {
                    if (uiState.isTesting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = statusColor,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(text = statusText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { focusManager.clearFocus(); onSaveClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = themeBlue),
                    enabled = actionsEnabled
                ) {
                    Text(text = "保存并退出", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = bgColor
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
        ) {
            // 连接设置区
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
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
                    isError = uiState.saveErrorMessage != null,
                    supportingText = uiState.saveErrorMessage?.let { message -> { Text(message) } },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedBorderColor = themeBlue,
                        cursorColor = themeBlue
                    )
                )
            }

            ToastNotification(
                message = uiState.notificationMessage,
                notificationId = uiState.notificationId,
                onDismiss = { onNotificationDismiss(uiState.notificationId) }
            )
        }
    }
}
