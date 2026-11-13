package com.hxb.myandroidapp_1021.theme

import androidx.compose.ui.graphics.Color

/**
 * 项目共用色板；常规页面颜色通过 MaterialTheme.colorScheme 使用。
 */
object AppColors {
    val Primary = Color(0xFF1E71C8)
    val OnPrimary = Color.White
    val Background = Color(0xFFF5F6F8)
    val Surface = Color.White
    val OnSurface = Color(0xFF222222)
    val OnSurfaceVariant = Color(0xFF888888)
    val Outline = Color(0xFFCCCCCC)
    val OutlineVariant = Color(0xFFE0E0E0)
    val Error = Color(0xFFE53935)
    val PrimaryContainer = Color(0xFFEDF3FB)

    val DarkPrimaryContainer = Color(0xFF153452)
    val DarkBackground = Color(0xFF121212)
    val DarkSurface = Color(0xFF1E1E1E)
    val DarkOnSurface = Color(0xFFE6E6E6)
    val DarkOnSurfaceVariant = Color(0xFFBDBDBD)
    val DarkOutline = Color(0xFF888888)
    val DarkOutlineVariant = Color(0xFF444444)

    // 工具读取状态、进度和禁用状态使用独立语义，避免与主色混用。
    val ReadBackground = Color(0xFFE7F6EB)
    val UnreadBackground = Color(0xFFF0F2F5)
    val ReadText = Color(0xFF16763C)
    val UnreadText = Color(0xFF596575)
    val Progress = Color(0xFF4B95F5)
    val DisabledContainer = Color(0xFFB0BEC5)
    val Success = Color(0xFF4CAF50)
}
