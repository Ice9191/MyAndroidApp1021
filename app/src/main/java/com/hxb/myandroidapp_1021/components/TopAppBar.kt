package com.hxb.myandroidapp_1021.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 统一页面顶部栏，按回调显示返回、首页、设置和退出登录按钮。
 */
@Composable
fun TopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    onHomeClick: (() -> Unit)? = null,
    onConfigClick: (() -> Unit)? = null,
    onLogoutClick: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars.only(WindowInsetsSides.Top))
                .height(40.dp)
        ) {
            val titleInset = 48.dp * maxOf(
                1,
                (if (onHomeClick != null) 1 else 0) + (if (onConfigClick != null) 1 else 0)
            )
            Text(
                text = title,
                modifier = if (onLogoutClick != null) {
                    Modifier.align(Alignment.CenterStart).padding(start = 16.dp, end = 144.dp)
                } else {
                    Modifier.align(Alignment.Center).padding(horizontal = titleInset)
                },
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (onBackClick != null) {
                IconButton(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(40.dp),
                    onClick = onBackClick,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                if (onLogoutClick != null) {
                    TextButton(onClick = onLogoutClick, shape = RoundedCornerShape(4.dp)) {
                        Text("退出登录", color = MaterialTheme.colorScheme.onPrimary, fontSize = 14.sp)
                    }
                }
                if (onHomeClick != null) {
                    IconButton(
                        onClick = onHomeClick,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "首页"
                        )
                    }
                }
                if (onConfigClick != null) {
                    IconButton(
                        onClick = onConfigClick,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "设置"
                        )
                    }
                }
            }
        }
    }
}
