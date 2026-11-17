package com.hxb.myandroidapp_1021.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

/**
 * 应用内 Toast：在屏幕正中间显示浅色提示，不占用页面布局；message 变化时重新弹出，3 秒后自动消失。
 * 不用已废弃的自定义 Toast View，改用 Popup 浮层。
 */
@Composable
fun ToastNotification(
    message: String?,
    notificationId: Int = 0,
    onDismiss: (() -> Unit)? = null
) {
    var displayed by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(message, notificationId) {
        if (message.isNullOrBlank()) return@LaunchedEffect
        displayed = message
        delay(3000.milliseconds)
        displayed = null
        onDismiss?.invoke()
    }
    val text = displayed ?: return
    Popup(
        popupPositionProvider = remember {
            object : PopupPositionProvider {
                override fun calculatePosition(
                    anchorBounds: IntRect,
                    windowSize: IntSize,
                    layoutDirection: LayoutDirection,
                    popupContentSize: IntSize
                ): IntOffset = IntOffset(
                    x = (windowSize.width - popupContentSize.width) / 2,
                    y = (windowSize.height - popupContentSize.height) / 2
                )
            }
        },
        properties = PopupProperties(
            focusable = false,
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Text(
            text = text,
            color = Color(0xFF222222),
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 40.dp)
                .background(Color(0xFFEFEFEF), RoundedCornerShape(8.dp))
                .padding(horizontal = 20.dp, vertical = 10.dp)
        )
    }
}
