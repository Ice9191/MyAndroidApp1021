package com.hxb.myandroidapp_1021.feature.login.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.hxb.myandroidapp_1021.core.ui.components.ToastNotification
import com.hxb.myandroidapp_1021.core.ui.components.TopAppBar

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LoginContent(
        uiState = uiState,
        onUsernameChange = viewModel::onUsernameChange,
        onPasswordChange = viewModel::onPasswordChange,
        onTogglePasswordVisible = viewModel::togglePasswordVisible,
        onNotificationDismiss = viewModel::dismissNotification,
        onLoginClick = { viewModel.login(onLoginSuccess) },
        onSettingsClick = {
            viewModel.dismissNotification()
            onSettingsClick()
        }
    )
}

@Composable
fun LoginContent(
    uiState: LoginUiState,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisible: () -> Unit,
    onNotificationDismiss: () -> Unit,
    onLoginClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    // 状态管理
    val imageUrl = "https://img.icons8.com/color/96/000000/toolbox.png"
    val backgroundColor = MaterialTheme.colorScheme.background
    val themeBlue = MaterialTheme.colorScheme.primary
    val image =
        if (uiState.passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
    val focusManager = LocalFocusManager.current

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            // 登录页顶部只保留右上角设置入口，不显示标题。
            TopAppBar(
                title = "",
                onConfigClick = {
                    focusManager.clearFocus()
                    onSettingsClick()
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .clipToBounds()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // === 1. 顶部 Logo 和 标题区域 ===
                Spacer(modifier = Modifier.height(24.dp))

                // 网络图片图标
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Logo",
                    modifier = Modifier.size(72.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "工器具库系统：PDA端",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(24.dp))

                // === 2. 登录表单区域 ===
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surface),
                    verticalArrangement = Arrangement.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(horizontal = 24.dp)
                            .padding(top = 16.dp, bottom = 32.dp)
                    ) {
                        Text(
                            text = "账号登录",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // 账号输入
                        Text(
                            text = "账号",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = uiState.username,
                            onValueChange = onUsernameChange,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("请输入账号", color = MaterialTheme.colorScheme.outline) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Account",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(4.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                focusedBorderColor = themeBlue,
                                cursorColor = themeBlue
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 密码输入
                        Text(
                            text = "密码",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = uiState.password,
                            onValueChange = onPasswordChange,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("请输入密码", color = MaterialTheme.colorScheme.outline) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Password",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            trailingIcon = {
                                IconButton(
                                    shape = RoundedCornerShape(4.dp),
                                    onClick = onTogglePasswordVisible
                                ) {
                                    Icon(
                                        imageVector = image,
                                        contentDescription = "Toggle password visibility",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            visualTransformation = if (uiState.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            shape = RoundedCornerShape(4.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                focusedBorderColor = themeBlue,
                                cursorColor = themeBlue
                            )
                        )

                        // 错误提示
                        uiState.errorMessage?.let { msg ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = msg, fontSize = 13.sp, color = MaterialTheme.colorScheme.error)
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // 登录按钮
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                onLoginClick()
                            },
                            enabled = !uiState.isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp),
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = themeBlue)
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(text = "登录", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            ToastNotification(
                message = uiState.notificationMessage,
                notificationId = uiState.notificationId,
                onDismiss = onNotificationDismiss,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}
