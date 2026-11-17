package com.hxb.myandroidapp_1021.pages.returns.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 好坏件标识：坏件用错误色，其余用主色容器，与工具卡片上的标识一致。
 */
@Composable
fun ConditionBadge(condition: String) {
    val bad = condition == "坏件"
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = if (bad) MaterialTheme.colorScheme.errorContainer
        else MaterialTheme.colorScheme.primaryContainer
    ) {
        Text(
            condition,
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 0.dp)
                .widthIn(min = 40.dp),
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            color = if (bad) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.primary
        )
    }
}
