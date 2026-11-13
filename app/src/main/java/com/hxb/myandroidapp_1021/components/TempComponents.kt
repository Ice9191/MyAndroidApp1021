package com.hxb.myandroidapp_1021.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hxb.myandroidapp_1021.ui.commonmodel.DisplayField
import com.hxb.myandroidapp_1021.ui.temp.TempTool

/**
 * 临时借还共用卡片，字段纵向换行，避免长工单号或工具名称挤压操作按钮。
 */
@Composable
internal fun TempCard(
    title: String,
    status: String,
    fields: List<DisplayField>,
    actionText: String? = null,
    enabled: Boolean = true,
    onAction: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                status,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
            fields.forEach {
                Text(
                    "${it.label}：${it.value.ifBlank { "—" }}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (actionText != null) {
                Button(
                    onClick = onAction, enabled = enabled, shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(12.dp)
                ) { Text(actionText) }
            }
        }
    }
}

@Composable
internal fun TempMessage(message: String?) {
    if (message != null) {
        Text(
            message,
            Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

internal fun TempTool.fields(): List<DisplayField> = listOf(
    DisplayField("物资编码", itemCode), DisplayField("RFID 编码", rfid),
    DisplayField("存放货位", location), DisplayField("单位", unit)
)
