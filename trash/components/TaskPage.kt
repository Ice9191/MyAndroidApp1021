package com.hxb.myandroidapp_1021.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * 其他拣选、其他上架共用的页面外壳，只接收展示内容和返回事件。
 */
@Composable
fun TaskPage(
    title: String,
    onBackClick: () -> Unit,
    bottomBar: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        modifier = Modifier.imePadding(),
        containerColor = Color(0xFFF5F6F8),
        topBar = {
            TopAppBar(
                title = title,
                onBackClick = onBackClick
            )
        },
        bottomBar = bottomBar
    ) { padding ->
        Column(Modifier
            .fillMaxSize()
            .padding(padding), content = content)
    }
}
