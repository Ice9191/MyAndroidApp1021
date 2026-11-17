package com.hxb.myandroidapp_1021.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 文档未定义 PDA 顶部刷新入口，移除额外的操作行，只保留查询状态提示。
 * 统一展示查询失败和缓存来源；缓存只用于查看，提交仍由服务端校验。
 */
@Composable
fun DataPage(
    isLoading: Boolean,
    fromCache: Boolean,
    errorMessage: String?,
    content: @Composable () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        if (errorMessage != null || fromCache) {
            Text(
                text = errorMessage ?: "当前显示本机缓存，业务提交仍需联网",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                color = if (errorMessage != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (isLoading) LinearProgressIndicator(Modifier.fillMaxWidth())
        Box(Modifier.weight(1f)) { content() }
    }
}
