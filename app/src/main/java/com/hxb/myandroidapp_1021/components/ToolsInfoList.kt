package com.hxb.myandroidapp_1021.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxb.myandroidapp_1021.data.models.ToolInfoRow

/**
 * 只展示传入的工器具行数据，滚动与业务状态由调用页面管理。
 */
@Composable
fun ToolsInfoList(
    rows: List<ToolInfoRow>,
    modifier: Modifier = Modifier,
    title: String = "工器具信息",
    valueTitle: String = "数量/件"
) {
    val textDark = Color(0xFF222222)
    val textGray = Color(0xFF888888)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(Color(0xFFF5F6F8))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                title,
                modifier = Modifier.weight(1f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = textGray
            )
            Text(
                valueTitle,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = textGray
            )
        }
        HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))

        rows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(row.rfid, fontSize = 14.sp, color = textDark)
                    if (row.name.isNotBlank()) {
                        Text(
                            row.name,
                            fontSize = 13.sp,
                            color = textGray,
                            modifier = Modifier.padding(top = 1.dp)
                        )
                    }
                }
                Text(
                    row.value ?: "${row.quantity}",
                    fontSize = 14.sp,
                    color = row.valueColor ?: textDark
                )
            }
            HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F0))
        }
    }
}
