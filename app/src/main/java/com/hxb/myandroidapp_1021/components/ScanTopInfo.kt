package com.hxb.myandroidapp_1021.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxb.myandroidapp_1021.data.models.DisplayField

/**
 * 展示调用页面提供的单据信息，不持有业务状态。
 */
@Composable
fun ScanTopInfo(
    fields: List<DisplayField>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 1.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        fields.forEach { field ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(field.label, fontSize = 13.sp, color = Color(0xFF888888))
                Text(
                    text = field.value,
                    fontSize = 13.sp,
                    color = Color(0xFF222222)
                )
            }
        }
    }
}
