package com.hxb.myandroidapp_1021.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AppColors.Primary,
    onPrimary = AppColors.OnPrimary,
    primaryContainer = AppColors.DarkPrimaryContainer,
    onPrimaryContainer = AppColors.DarkOnSurface,
    secondary = AppColors.Primary,
    onSecondary = AppColors.OnPrimary,
    tertiary = AppColors.Success,
    onTertiary = AppColors.OnPrimary,
    background = AppColors.DarkBackground,
    onBackground = AppColors.DarkOnSurface,
    secondaryContainer = AppColors.DarkPrimaryContainer,
    onSecondaryContainer = AppColors.DarkOnSurface,
    surface = AppColors.DarkSurface,
    surfaceContainerLowest = AppColors.DarkBackground,
    surfaceContainerLow = AppColors.DarkSurface,
    surfaceContainer = AppColors.DarkSurface,
    surfaceContainerHigh = AppColors.DarkSurface,
    surfaceContainerHighest = AppColors.DarkSurface,
    onSurface = AppColors.DarkOnSurface,
    surfaceVariant = AppColors.DarkBackground,
    onSurfaceVariant = AppColors.DarkOnSurfaceVariant,
    outline = AppColors.DarkOutline,
    outlineVariant = AppColors.DarkOutlineVariant,
    error = AppColors.Error,
    onError = AppColors.OnPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = AppColors.Primary,
    onPrimary = AppColors.OnPrimary,
    primaryContainer = AppColors.PrimaryContainer,
    onPrimaryContainer = AppColors.Primary,
    secondary = AppColors.Primary,
    onSecondary = AppColors.OnPrimary,
    tertiary = AppColors.Success,
    onTertiary = AppColors.OnPrimary,
    background = AppColors.Background,
    onBackground = AppColors.OnSurface,
    secondaryContainer = AppColors.PrimaryContainer,
    onSecondaryContainer = AppColors.Primary,
    surface = AppColors.Surface,
    surfaceContainerLowest = AppColors.Surface,
    surfaceContainerLow = AppColors.Background,
    surfaceContainer = AppColors.Surface,
    surfaceContainerHigh = AppColors.Background,
    surfaceContainerHighest = AppColors.Background,
    onSurface = AppColors.OnSurface,
    surfaceVariant = AppColors.Background,
    onSurfaceVariant = AppColors.OnSurfaceVariant,
    outline = AppColors.Outline,
    outlineVariant = AppColors.OutlineVariant,
    error = AppColors.Error,
    onError = AppColors.OnPrimary
)

@Composable
fun ToolSystemPdaProjectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // 固定使用项目色板，不再根据系统壁纸动态取色。
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
