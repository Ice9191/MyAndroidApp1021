package com.hxb.myandroidapp_1021.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

/**
 * 放在 TopAppBar 下方的内容容器顶部；新通知重新计时，关闭时保留文字以完成退出动画。
 */
@Composable
fun TopNotification(
    message: String?,
    notificationId: Int,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var displayedMessage by remember { mutableStateOf(message.orEmpty()) }
    LaunchedEffect(message, notificationId) {
        if (message != null) {
            displayedMessage = message
            delay(1000.milliseconds)
            onDismiss()
        }
    }
    AnimatedVisibility(
        visible = message != null,
        enter = slideInVertically { -it },
        exit = slideOutVertically { -it },
        modifier = modifier
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth().semantics { liveRegion = LiveRegionMode.Polite }
        ) {
            Text(displayedMessage, Modifier.padding(horizontal = 16.dp, vertical = 12.dp))
        }
    }
}
