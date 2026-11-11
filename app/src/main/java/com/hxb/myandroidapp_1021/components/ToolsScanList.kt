package com.hxb.myandroidapp_1021.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxb.myandroidapp_1021.data.models.DisplayField

/**
 * 展示任意工具类型，字段和读取状态由调用页面映射。
 */
@Composable
fun <T> ToolsScanList(
    tools: List<T>,
    key: (T) -> Any,
    title: (T) -> String,
    lineLabel: (T) -> String,
    statusText: (T) -> String,
    isRead: (T) -> Boolean,
    fields: (T) -> List<DisplayField>,
    modifier: Modifier = Modifier
) {
    val textGray = Color(0xFF888888)
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(tools, key = key) { tool ->
            val picked = isRead(tool)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF9FAFB),
                border = BorderStroke(
                    width = 1.dp,
                    color = Color(0xFFE4E9F0)
                )
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
                            color = if (picked) Color(0xFFE7F6EB) else Color(0xFFF0F2F5)
                        ) {
                            Text(
                                text = statusText(tool),
                                modifier = Modifier.padding(
                                    horizontal = 8.dp,
                                    vertical = 2.dp
                                ),
                                fontSize = 11.sp,
                                color = if (picked) Color(0xFF16763C) else Color(
                                    0xFF596575
                                )
                            )
                        }
                    }
                    Text(
                        title(tool),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF182536)
                    )
                    fields(tool).forEach { field ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = field.label,
                                fontSize = 13.sp,
                                color = Color(0xFF888888),
                                maxLines = 1
                            )
                            Text(
                                text = field.value,
                                fontSize = 13.sp,
                                color = Color(0xFF222222),
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
