package com.hxb.myandroidapp_1021.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxb.myandroidapp_1021.ui.common.DisplayField

/**
 * 单张工具卡片；选中状态由页面持有，组件只上报选择事件并展示选中动画。
 */
@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun ManualScanCard(
    title: String,
    lineLabel: String,
    statusText: String,
    isRead: Boolean,
    fields: List<DisplayField>,
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val themeBlue = Color(0xFF1E71C8)
    val textGray = Color(0xFF888888)
    val lift by animateDpAsState(if (selected) 2.dp else 0.dp, label = "cardLift")
    val selectionModifier = Modifier.toggleable(
        value = selected,
        role = Role.Checkbox,
        onValueChange = onSelectedChange
    )
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .offset(y = -lift),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF9FAFB),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) themeBlue else Color(0xFFE4E9F0)
        ),
        shadowElevation = lift * 2
    ) {
        Column(
            modifier = selectionModifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = lineLabel,
                    fontSize = 12.sp,
                    color = textGray
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isRead) Color(0xFFE7F6EB) else Color(0xFFF0F2F5)
                ) {
                    Text(
                        text = statusText,
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 2.dp
                        ),
                        fontSize = 11.sp,
                        color = if (isRead) Color(0xFF16763C) else Color(0xFF596575)
                    )
                }
            }
            Text(
                title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF182536)
            )
            fields.forEach { field ->
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
