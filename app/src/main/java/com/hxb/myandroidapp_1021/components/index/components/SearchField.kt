package com.hxb.myandroidapp_1021.components.index.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** 各页面统一的主题蓝 */
private val ThemeBlue = Color(0xFF1E71C8)

/**
 * 列表页顶部的搜索框（灰底、圆角、左侧放大镜、默认 38dp 高）。
 *
 * 用法：
 * ```
 * val searchState = rememberTextFieldState()
 * SearchField(state = searchState, placeholder = "请输入拣选单号")
 * // 取值：searchState.text.toString()
 * ```
 *
 * 为什么用 TextFieldState 版重载：material3 的
 * `OutlinedTextField(value = ..., onValueChange = ...)` 没有 `contentPadding` 参数，
 * 内边距固定为上下各 16dp（按 56dp 的最小高度设计）。一旦把高度压到 38dp，
 * 114px 里 96px 被内边距吃掉，只剩 18px 给文字排版，文字就会被裁掉。
 * 只有 state 版重载能传 `contentPadding`，所以这里用它。
 *
 * @param state 输入框文本状态，由调用方 rememberTextFieldState() 创建，便于做搜索过滤
 * @param modifier 作用于最外层容器
 * @param placeholder 提示文字，聚焦时也会保留（与输入文字同一套排版，不会错位）
 * @param fieldHeight 输入框高度，默认 38dp；改这个不需要动 contentPadding
 * @param accentColor 光标 / 聚焦描边颜色
 * @param showSearchIcon 是否显示左侧放大镜；纯输入场景（如领料人工号）可以关掉
 */
@Composable
fun SearchField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    placeholder: String = "请输入拣选单号",
    fieldHeight: Dp = 38.dp,
    accentColor: Color = ThemeBlue,
    showSearchIcon: Boolean = true
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        OutlinedTextField(
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .height(fieldHeight),
            textStyle = TextStyle(
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = Color(0xFF333333)
            ),
            placeholder = {
                Text(
                    text = placeholder,
                    fontSize = 14.sp,
                    color = Color(0xFF999999)
                )
            },
            leadingIcon = if (showSearchIcon) {
                {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF999999),
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else null,
            lineLimits = TextFieldLineLimits.SingleLine,
            shape = RoundedCornerShape(4.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF333333),
                unfocusedTextColor = Color(0xFF333333),
                focusedContainerColor = Color(0xFFF5F6F8),
                unfocusedContainerColor = Color(0xFFF5F6F8),
                focusedBorderColor = accentColor,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = accentColor
            ),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
        )
    }
}