package com.hxb.myandroidapp_1021.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxb.myandroidapp_1021.core.ui.theme.AppColors
import com.hxb.myandroidapp_1021.pages.commonmodel.DisplayField

/**
 * 临时借还的单张卡片，样式与工单拣选的列表卡片一致：
 * 首行左侧标题、右侧状态与操作按钮；字段键左值右；进度使用统一的进度条。
 */
@Composable
fun TempListCard(
    title: String,
    status: String,
    fields: List<DisplayField>,
    progress: OrderProgress? = null,
    actionText: String? = null,
    enabled: Boolean = true,
    onAction: () -> Unit = {}
) {
    val themeBlue = MaterialTheme.colorScheme.primary
    val textDark = MaterialTheme.colorScheme.onSurface
    val textGray = MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 1.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = status,
                    fontSize = 13.sp,
                    color = themeBlue,
                    maxLines = 1
                )
                if (actionText != null) {
                    Button(
                        onClick = onAction,
                        enabled = enabled,
                        modifier = Modifier
                            .height(30.dp)
                            .wrapContentWidth(),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = themeBlue)
                    ) {
                        Text(text = actionText, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 1.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            fields.forEach { field ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = field.label,
                        fontSize = 13.sp,
                        color = textGray,
                        maxLines = 1,
                        modifier = Modifier.alignByBaseline()
                    )
                    Text(
                        text = field.value,
                        fontSize = 13.sp,
                        color = textDark,
                        maxLines = 1,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .alignByBaseline()
                    )
                }
            }
            progress?.let { itemProgress ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 0.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${itemProgress.label} ${itemProgress.completed}/${itemProgress.total}",
                        fontSize = 13.sp,
                        color = textGray
                    )
                    LinearProgressIndicator(
                        progress = {
                            if (itemProgress.total > 0) {
                                (itemProgress.completed.toFloat() / itemProgress.total).coerceIn(
                                    0f,
                                    1f
                                )
                            } else {
                                0f
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp),
                        color = AppColors.Progress,
                        trackColor = AppColors.UnreadBackground,
                        gapSize = 0.dp,
                        drawStopIndicator = {}
                    )
                }
            }
        }
    }
}
