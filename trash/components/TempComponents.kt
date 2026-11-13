package com.hxb.myandroidapp_1021.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hxb.myandroidapp_1021.ui.commonmodel.DisplayField
import com.hxb.myandroidapp_1021.ui.temp.TempTool

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
