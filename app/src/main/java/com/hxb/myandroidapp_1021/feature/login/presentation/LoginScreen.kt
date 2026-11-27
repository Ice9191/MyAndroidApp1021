package com.hxb.myandroidapp_1021.feature.login.presentation

import android.app.Activity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apartment
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.hxb.myandroidapp_1021.R
import com.hxb.myandroidapp_1021.core.theme.GlassAccent
import com.hxb.myandroidapp_1021.core.theme.GlassBackgroundBottom
import com.hxb.myandroidapp_1021.core.theme.GlassBackgroundTop
import com.hxb.myandroidapp_1021.core.theme.GlassGlowBlue
import com.hxb.myandroidapp_1021.core.theme.GlassGlowCyan
import com.hxb.myandroidapp_1021.core.theme.GlassGlowViolet
import com.hxb.myandroidapp_1021.core.theme.MyAndroidApp1021Theme
import com.hxb.myandroidapp_1021.core.ui.components.TopAppBar

@Composable
fun LoginScreen(
    onLogin: (user: String, password: String, department: String) -> Unit = { _, _, _ -> },
    onExit: () -> Unit = {},
    onScanClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    LoginContent(
        onLogin = onLogin,
        onExit = onExit,
        onScanClick = onScanClick,
        onSettingsClick = onSettingsClick
    )
}

@Composable
fun LoginContent(
    onLogin: (user: String, password: String, department: String) -> Unit = { _, _, _ -> },
    onExit: () -> Unit = {},
    onScanClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    var user by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // 登录页始终是深色底，强制状态栏/导航栏使用浅色图标
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = false
            controller.isAppearanceLightNavigationBars = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(GlassBackgroundTop, GlassBackgroundBottom)))
    ) {
        AuroraBackground()

        // 顶部栏：复用通用 TopAppBar，透明底叠在渐变背景上
        TopAppBar(
            title = "登录",
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = Color.Transparent,
            contentColor = Color.White,
            actions = {
                IconButton(onClick = onScanClick) {
                    Icon(
                        imageVector = Icons.Rounded.QrCodeScanner,
                        contentDescription = "扫一扫"
                    )
                }
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = "设置"
                    )
                }
            }
        )

        // 外层负责垂直居中，内层负责键盘弹出后可滚动
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .padding(horizontal = 24.dp)
                .padding(top = 72.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 公司 Logo（替换原来的“RFID管理系统”文字）
                LogoMark()

                Spacer(Modifier.height(28.dp))

                // 中间玻璃面板：用户、密码、部门
                GlassPanel(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 440.dp)
                ) {
                    GlassField(
                        label = "用户",
                        value = user,
                        onValueChange = { user = it },
                        icon = Icons.Rounded.Person,
                        placeholder = "请输入用户名"
                    )
                    Spacer(Modifier.height(16.dp))
                    GlassField(
                        label = "密码",
                        value = password,
                        onValueChange = { password = it },
                        icon = Icons.Rounded.Lock,
                        placeholder = "请输入密码",
                        isPassword = true,
                        passwordVisible = passwordVisible,
                        onTogglePassword = { passwordVisible = !passwordVisible },
                        keyboardType = KeyboardType.Password
                    )
                    Spacer(Modifier.height(16.dp))
                    GlassField(
                        label = "部门",
                        value = department,
                        onValueChange = { department = it },
                        icon = Icons.Rounded.Apartment,
                        placeholder = "请输入部门",
                        imeAction = ImeAction.Done
                    )
                }

                Spacer(Modifier.height(24.dp))

                // 下方两个玻璃按钮：登录、退出
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 440.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    GlassActionButton(
                        text = "登录",
                        primary = true,
                        enabled = user.isNotBlank() && password.isNotBlank(),
                        modifier = Modifier.weight(1f),
                        onClick = { onLogin(user.trim(), password, department.trim()) }
                    )
                    GlassActionButton(
                        text = "退出",
                        primary = false,
                        modifier = Modifier.weight(1f),
                        onClick = onExit
                    )
                }

                Spacer(Modifier.height(28.dp))

                Text(
                    text = "© 2026 工器具库系统 · v1.0",
                    color = Color.White.copy(alpha = 0.38f),
                    fontSize = 11.sp,
                    letterSpacing = 0.6.sp
                )
            }
        }
    }
}

/** 背景光斑：给玻璃面板提供可“透”的底色 */
@Composable
private fun AuroraBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val cyanCenter = Offset(w * 0.12f, h * 0.10f)
        val blueCenter = Offset(w * 0.95f, h * 0.42f)
        val violetCenter = Offset(w * 0.35f, h * 0.98f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(GlassGlowCyan.copy(alpha = 0.32f), Color.Transparent),
                center = cyanCenter,
                radius = w * 0.80f
            ),
            radius = w * 0.80f,
            center = cyanCenter
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(GlassGlowBlue.copy(alpha = 0.28f), Color.Transparent),
                center = blueCenter,
                radius = w * 0.75f
            ),
            radius = w * 0.75f,
            center = blueCenter
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(GlassGlowViolet.copy(alpha = 0.26f), Color.Transparent),
                center = violetCenter,
                radius = w * 0.85f
            ),
            radius = w * 0.85f,
            center = violetCenter
        )
    }
}

/** 顶部的公司 Logo，替换原来的“RFID管理系统”文字标题 */
@Composable
private fun LogoMark() {
    Box(contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(GlassGlowCyan.copy(alpha = 0.26f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )
        Image(
            painter = painterResource(id = R.drawable.logo_company),
            contentDescription = "公司 Logo",
            modifier = Modifier
                .size(104.dp)
                .shadow(
                    elevation = 18.dp,
                    shape = RoundedCornerShape(26.dp),
                    ambientColor = Color.Black.copy(alpha = 0.45f),
                    spotColor = Color.Black.copy(alpha = 0.55f)
                )
                .clip(RoundedCornerShape(26.dp))
        )
    }
}

/** 玻璃面板：半透明白色渐变 + 顶部高光 + 细描边 */
@Composable
private fun GlassPanel(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(26.dp)
    Column(
        modifier = modifier
            .shadow(
                elevation = 26.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.45f),
                spotColor = Color.Black.copy(alpha = 0.55f)
            )
            .clip(shape)
            .background(
                Brush.linearGradient(
                    listOf(Color.White.copy(alpha = 0.32f), Color.White.copy(alpha = 0.10f))
                )
            )
            .drawBehind {
                // 面板顶部的高光，模拟玻璃反光
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White.copy(alpha = 0.16f), Color.Transparent),
                        endY = size.height * 0.5f
                    )
                )
            }
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(Color.White.copy(alpha = 0.62f), Color.White.copy(alpha = 0.14f))
                ),
                shape = shape
            )
            .padding(horizontal = 22.dp, vertical = 20.dp),
        content = content
    )
}

/** 玻璃风格的输入框：标签 + 图标 + 下划线 + 可选密码可见切换 */
@Composable
private fun GlassField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    placeholder: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: () -> Unit = {},
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val lineColor = if (focused) GlassAccent else Color.White.copy(alpha = 0.22f)
    val labelColor = if (focused) Color.White else Color.White.copy(alpha = 0.70f)
    val iconTint = if (focused) GlassAccent else Color.White.copy(alpha = 0.66f)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = label,
                color = labelColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )
        }
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 40.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = Color.White.copy(alpha = 0.38f),
                        fontSize = 16.sp
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                    singleLine = true,
                    cursorBrush = SolidColor(GlassAccent),
                    visualTransformation = if (isPassword && !passwordVisible) {
                        PasswordVisualTransformation()
                    } else {
                        VisualTransformation.None
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = keyboardType,
                        imeAction = imeAction
                    ),
                    interactionSource = interactionSource
                )
            }
            if (isPassword) {
                IconButton(
                    onClick = onTogglePassword,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                        contentDescription = if (passwordVisible) "隐藏密码" else "显示密码",
                        tint = Color.White.copy(alpha = 0.55f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(lineColor)
        )
    }
}

/** 玻璃按钮：主按钮为青色渐变，次按钮为白色半透明 */
@Composable
private fun GlassActionButton(
    text: String,
    primary: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(18.dp)
    val background = if (primary) {
        Brush.linearGradient(
            listOf(GlassGlowCyan.copy(alpha = 0.95f), GlassGlowBlue.copy(alpha = 0.95f))
        )
    } else {
        Brush.linearGradient(
            listOf(Color.White.copy(alpha = 0.20f), Color.White.copy(alpha = 0.08f))
        )
    }
    val borderColor = if (primary) {
        Color.White.copy(alpha = 0.45f)
    } else {
        Color.White.copy(alpha = 0.30f)
    }

    Box(
        modifier = modifier
            .height(50.dp)
            .shadow(
                elevation = 14.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.35f),
                spotColor = Color.Black.copy(alpha = 0.45f)
            )
            .clip(shape)
            .background(background)
            .border(width = 1.dp, color = borderColor, shape = shape)
            .alpha(if (enabled) 1f else 0.5f)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 2.sp
        )
    }
}

@Preview(name = "登录页", showBackground = true, widthDp = 411, heightDp = 891)
@Composable
fun LoginScreenPreview() {
    MyAndroidApp1021Theme(dynamicColor = false) {
        LoginScreen()
    }
}
