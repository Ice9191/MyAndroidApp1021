package com.hxb.myandroidapp_1021.components.index.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxb.myandroidapp_1021.components.index.models.ToolCheckRow

private val ThemeBlue = Color(0xFF1E71C8)
private val TextDark = Color(0xFF222222)
private val TextGray = Color(0xFF888888)

/**
 * 「已读取 / 未读取」两个 Tab 共用的表格组件：
 * 顶部一行是「已选 N 件 + 操作按钮」（手动读取 / 取消读取），
 * 下面是一张三列表：（勾选框）、（工器具信息）、（数量），表格直接铺到屏幕底部，下方不留白。
 *
 * @param rows 表格数据
 * @param selectedCount 已勾选件数，显示在左上角
 * @param actionLabel 右上角按钮文字（手动读取 / 取消读取）
 * @param actionEnabled 按钮是否可点
 * @param onActionClick 按钮点击
 * @param onCheckedChange 勾选框变化回调
 */
@Composable
fun ToolCheckListTab(
    rows: List<ToolCheckRow>,
    selectedCount: Int,
    actionLabel: String,
    actionEnabled: Boolean,
    onActionClick: () -> Unit,
    onCheckedChange: (ToolCheckRow, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // === 1. 已选 N 件 + 操作按钮 ===
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "已选${selectedCount}件",
                fontSize = 14.sp,
                color = TextGray
            )
            Button(
                onClick = onActionClick,
                enabled = actionEnabled,
                modifier = Modifier.height(30.dp),
                shape = RoundedCornerShape(4.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ThemeBlue,
                    disabledContainerColor = Color(0xFFB0BEC5)
                )
            ) {
                Text(actionLabel, fontSize = 13.sp, color = Color.White)
            }
        }

        // === 2. 表头：（空） / RFID / 数量 ===
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F6F8))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(28.dp)) // 第一列留给 checkbox，表头空着
            Text(
                text = "RFID",
                modifier = Modifier.weight(1f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextGray
            )
            Text(
                text = "数量",
                modifier = Modifier.width(48.dp),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextGray,
                textAlign = TextAlign.End
            )
        }
        HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))

        // === 3. 表格内容 ===
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(rows, key = { it.id }) { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = row.checked,
                        onCheckedChange = { onCheckedChange(row, it) },
                        colors = CheckboxDefaults.colors(checkedColor = ThemeBlue),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = row.rfid, fontSize = 14.sp, color = TextDark)
                        if (row.subtitle.isNotBlank()) {
                            Text(text = row.subtitle, fontSize = 12.sp, color = TextGray)
                        }
                    }
                    Text(
                        text = "${row.quantity}",
                        modifier = Modifier.width(48.dp),
                        fontSize = 14.sp,
                        color = TextDark,
                        textAlign = TextAlign.End
                    )
                }
                HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F0))
            }
        }
    }
}