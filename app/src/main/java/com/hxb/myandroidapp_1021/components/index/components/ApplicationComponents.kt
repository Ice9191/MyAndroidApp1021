package com.hxb.myandroidapp_1021.components.index.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxb.myandroidapp_1021.components.index.ToolScanItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationTopBar(title: String, onBackClick: () -> Unit) {
    TopAppBar(
        modifier = Modifier.height(75.dp),
        title = {
            Text(
                title, fontSize = 18.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.offset(x = (-16).dp)
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick, modifier = Modifier.offset(x = (-7).dp)) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "返回")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF1E71C8), titleContentColor = Color.White,
            navigationIconContentColor = Color.White
        )
    )
}

@Composable
fun ApplicationToolRow(tool: ToolScanItem, onCheckedChange: ((Boolean) -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onCheckedChange != null) {
            Checkbox(
                checked = tool.isChecked, onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF1E71C8))
            )
        }
        Column(Modifier.weight(1f)) {
            Text(
                tool.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222222)
            )
            Text(
                tool.code,
                fontSize = 13.sp,
                color = Color(0xFF888888),
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                "RFID：${tool.rfid}",
                fontSize = 13.sp,
                color = Color(0xFF888888),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Text("1", fontSize = 14.sp, color = Color(0xFF222222))
    }
    HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F0))
}
