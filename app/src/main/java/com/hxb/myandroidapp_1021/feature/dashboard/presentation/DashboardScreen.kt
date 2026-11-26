package com.hxb.myandroidapp_1021.feature.dashboard.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.hxb.myandroidapp_1021.core.ui.components.ToastNotification
import com.hxb.myandroidapp_1021.core.ui.components.TopAppBar
import com.hxb.myandroidapp_1021.core.ui.theme.AppColors

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onMenuClick: (Any) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DashboardContent(
        uiState = uiState,
        onLogoutClick = viewModel::onLogout,
        onErrorDismiss = viewModel::dismissError,
        onRetryLoad = viewModel::loadDashboard,
        onMenuClick = onMenuClick
    )
}

@Composable
fun DashboardContent(
    uiState: DashboardUiState,
    onLogoutClick: () -> Unit = {},
    onErrorDismiss: () -> Unit = {},
    onRetryLoad: () -> Unit = {},
    onMenuClick: (Any) -> Unit = {}
) {
    val user = uiState.user
    val themeBlue = MaterialTheme.colorScheme.primary
    val bgColor = MaterialTheme.colorScheme.background
    val lightBlueBg = MaterialTheme.colorScheme.primaryContainer
    val textDark = MaterialTheme.colorScheme.onSurface
    val textGray = MaterialTheme.colorScheme.onSurfaceVariant
    val greenColor = AppColors.Success
    val dividerColor = MaterialTheme.colorScheme.outlineVariant
    val serverColor = if (uiState.serverConnected) greenColor else textGray

    Scaffold(
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = "",
                onLogoutClick = onLogoutClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // 登录人信息
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = "https://picsum.photos/800/300",
                    contentDescription = "Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(4.dp))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "欢迎回来",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = user?.let { "${it.userName} · ${it.userRole}" }.orEmpty(),
                        fontSize = 13.sp,
                        color = textGray
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Wifi,
                        contentDescription = null,
                        tint = serverColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = uiState.serverText, fontSize = 14.sp, color = serverColor)
                }
            }

            HorizontalDivider(thickness = 1.dp, color = dividerColor)

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                )
            } else if (user == null) {
                Button(
                    onClick = onRetryLoad,
                    enabled = !uiState.isLoggingOut,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                ) {
                    Text("重试")
                }
            }

            // 模块
            uiState.menuSections.forEach { section ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp)
                ) {
                    Text(
                        text = section.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        section.items.chunked(2).forEach { items ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items.forEach { item ->
                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(lightBlueBg, RoundedCornerShape(4.dp))
                                            .clickable { onMenuClick(item.route) }
                                            .padding(vertical = 16.dp, horizontal = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = null,
                                            tint = themeBlue,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = item.title,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                                if (items.size == 1) Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                HorizontalDivider(thickness = 1.dp, color = dividerColor)
            }
        }
        ToastNotification(
            message = uiState.errorMessage,
            notificationId = 0,
            onDismiss = onErrorDismiss,
            modifier = Modifier.padding(paddingValues)
        )
    }
}
