package com.hxb.myandroidapp_1021.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp

/**
 * 输入值、提示和错误由调用页面传入，输入变化通过 onValueChange 上报。
 * 传入 onClick 时输入框作为只读点击区域，由调用页面打开选择弹窗，点击不会聚焦键盘。
 * width 包含组件两侧留白，默认占满父布局可用宽度，与原布局一致。
 */
@Composable
fun InputFieldOne(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    showSearchIcon: Boolean = true,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
    enabled: Boolean = true,
    width: Dp = Dp.Unspecified,
    horizontalPadding: Dp = 16.dp,
    trailingIcon: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val themeBlue = MaterialTheme.colorScheme.primary
    val searchInteractionSource = remember { MutableInteractionSource() }
    val searchShape = RoundedCornerShape(4.dp)
    val searchColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.background,
        unfocusedContainerColor = MaterialTheme.colorScheme.background,
        focusedBorderColor = themeBlue,
        unfocusedBorderColor = Color.Transparent
    )

    Box(
        modifier = modifier
            .then(if (width == Dp.Unspecified) Modifier.fillMaxWidth() else Modifier.width(width))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = horizontalPadding, vertical = 8.dp)
    ) {
        BasicTextField(
            enabled = enabled,
            readOnly = onClick != null,
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { if (errorMessage != null) error(errorMessage) },
            textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurface),
            singleLine = true,
            keyboardOptions = keyboardOptions,
            interactionSource = searchInteractionSource,
            cursorBrush = SolidColor(themeBlue),
            decorationBox = { innerTextField ->
                Box {
                    OutlinedTextFieldDefaults.Container(
                        enabled = enabled,
                        isError = errorMessage != null,
                        interactionSource = searchInteractionSource,
                        modifier = Modifier.matchParentSize(),
                        colors = searchColors,
                        shape = searchShape
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (showSearchIcon) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (value.isEmpty()) {
                                Text(
                                    text = placeholder,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            innerTextField()
                        }
                        trailingIcon?.invoke()
                    }
                }
            }
        )
        if (onClick != null) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(role = Role.Button, onClick = onClick)
            )
        }
    }
}
