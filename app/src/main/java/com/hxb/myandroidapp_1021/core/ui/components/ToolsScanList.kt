package com.hxb.myandroidapp_1021.core.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxb.myandroidapp_1021.pages.commonmodel.DisplayField
import com.hxb.myandroidapp_1021.core.ui.theme.AppColors

/**
 * 展示任意工具类型，字段和读取状态由调用页面映射。
 * 传入 selectedTool 与 onItemClick 时卡片可点击选中，badge 用于展示好坏件等标记。
 */
@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun <T> ToolsScanList(
    tools: List<T>,
    key: (T) -> Any,
    title: (T) -> String,
    lineLabel: (T) -> String,
    statusText: (T) -> String,
    isRead: (T) -> Boolean,
    fields: (T) -> List<DisplayField>,
    modifier: Modifier = Modifier,
    selectedTool: T? = null,
    onItemClick: ((T) -> Unit)? = null,
    badge: ((T) -> String?)? = null
) {
    val textGray = MaterialTheme.colorScheme.onSurfaceVariant
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(tools, key = key) { tool ->
            val picked = isRead(tool)
            val selected = selectedTool != null && selectedTool == tool
            val lift by animateDpAsState(if (selected) 2.dp else 0.dp, label = "toolCardLift")
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = -lift)
                    .then(
                        if (onItemClick != null) Modifier.clickable { onItemClick(tool) }
                        else Modifier
                    ),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(
                    width = if (selected) 2.dp else 1.dp,
                    color = if (selected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outlineVariant
                ),
                shadowElevation = lift * 2
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = lineLabel(tool),
                            fontSize = 12.sp,
                            color = textGray
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (picked) AppColors.ReadBackground else AppColors.UnreadBackground
                        ) {
                            Text(
                                text = statusText(tool),
                                modifier = Modifier.padding(
                                    horizontal = 8.dp,
                                    vertical = 2.dp
                                ),
                                fontSize = 11.sp,
                                color = if (picked) AppColors.ReadText else AppColors.UnreadText
                            )
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            title(tool),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        badge?.invoke(tool)?.let { label ->
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.errorContainer
                            ) {
                                Text(
                                    text = label,
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp, vertical = 0.dp)
                                        .widthIn(min = 40.dp),
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.error,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    fields(tool).forEach { field ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = field.label,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                            Text(
                                text = field.value,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
