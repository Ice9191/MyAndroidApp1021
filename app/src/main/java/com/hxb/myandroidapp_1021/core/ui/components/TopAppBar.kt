package com.hxb.myandroidapp_1021.core.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    centerTitle: Boolean = false,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    if (centerTitle) {
        CenterAlignedTopAppBar(
            title = { DefaultTitle(title, contentColor) },
            modifier = modifier,
            navigationIcon = {
                if (navigationIcon != null) {
                    navigationIcon()
                } else if (onBackClick != null) {
                    DefaultBackIcon(onBackClick, contentColor)
                }
            },
            actions = actions,
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = backgroundColor,
                scrolledContainerColor = Color.Unspecified,
                navigationIconContentColor = contentColor,
                titleContentColor = contentColor,
                actionIconContentColor = contentColor
            )
        )
    } else {
        TopAppBar(
            title = { DefaultTitle(title, contentColor) },
            modifier = modifier,
            navigationIcon = {
                if (navigationIcon != null) {
                    navigationIcon()
                } else if (onBackClick != null) {
                    DefaultBackIcon(onBackClick, contentColor)
                }
            },
            actions = actions,
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = backgroundColor,
                navigationIconContentColor = contentColor,
                titleContentColor = contentColor,
                actionIconContentColor = contentColor
            )
        )
    }
}

/**
 * 默认的标题样式
 */
@Composable
private fun DefaultTitle(title: String, contentColor: Color) {
    Text(
        text = title,
        color = contentColor,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 1
    )
}

@Composable
private fun DefaultBackIcon(onBackClick: () -> Unit, contentColor: Color) {
    IconButton(onClick = onBackClick) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = contentColor
        )
    }
}