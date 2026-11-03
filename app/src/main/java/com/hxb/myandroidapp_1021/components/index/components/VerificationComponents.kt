package com.hxb.myandroidapp_1021.components.index.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxb.myandroidapp_1021.components.index.ToolScanItem

@Composable
fun VerificationButton(text: String, onClick: () -> Unit, enabled: Boolean = true) {
    Column(Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp, vertical = 12.dp)) {
        Button(
            onClick = onClick, enabled = enabled, modifier = Modifier.fillMaxWidth().height(44.dp),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E71C8), disabledContainerColor = Color(0xFFB0BEC5))
        ) {
            Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationTabs(labels: List<String>, selectedIndex: Int, onSelect: (Int) -> Unit) {
    val themeBlue = Color(0xFF1E71C8)
    SecondaryTabRow(
        selectedTabIndex = selectedIndex, containerColor = Color.White, contentColor = themeBlue,
        indicator = {
            TabRowDefaults.SecondaryIndicator(Modifier.tabIndicatorOffset(selectedIndex), color = themeBlue)
        }
    ) {
        labels.forEachIndexed { index, label ->
            Tab(selected = selectedIndex == index, onClick = { onSelect(index) }, text = {
                Text(label, color = if (selectedIndex == index) themeBlue else Color(0xFF888888),
                    fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Normal)
            })
        }
    }
}

@Composable
fun VerificationToolRow(tool: ToolScanItem, source: String, onCheckedChange: ((Boolean) -> Unit)? = null) {
    Row(
        Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onCheckedChange != null) {
            Checkbox(checked = tool.isChecked, onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF1E71C8)))
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(tool.name, modifier = Modifier.weight(1f), fontSize = 14.sp,
                    fontWeight = FontWeight.Bold, color = Color(0xFF222222))
                Text(source, fontSize = 12.sp, color = Color(0xFF1E71C8))
            }
            Text(tool.code, fontSize = 13.sp, color = Color(0xFF888888))
            Text("RFID：${tool.rfid}", fontSize = 13.sp, color = Color(0xFF888888))
        }
    }
    HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F0))
}
