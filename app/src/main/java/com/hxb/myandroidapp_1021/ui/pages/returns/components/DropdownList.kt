package com.hxb.myandroidapp_1021.ui.pages.returns.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 下拉选择：展示当前值和候选项，选中后回调；空字符串表示未选择，显示 placeholder。
 */
@Composable
fun DropdownList(
    label: String,
    options: List<String>,
    selected: String,
    placeholder: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    val themeBlue = MaterialTheme.colorScheme.primary
    val textGray = MaterialTheme.colorScheme.onSurfaceVariant

    Column(modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        if (label.isNotBlank()) Text(label, fontSize = 13.sp, color = textGray)
        Box {
            Row(
                Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = if (expanded) themeBlue else MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .clickable(enabled = enabled) { expanded = true }
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selected.ifBlank { placeholder },
                    fontSize = 14.sp,
                    color = if (selected.isBlank()) textGray else MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Open options",
                    tint = textGray
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                DropdownMenuItem(
                    text = { Text(placeholder, fontSize = 14.sp, color = textGray) },
                    onClick = {
                        onSelect("")
                        expanded = false
                    }
                )
                options.forEach { option ->
                    val current = option == selected
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                fontSize = 14.sp,
                                color = if (current) themeBlue else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (current) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                                    )
                                    .padding(vertical = 4.dp)
                            )
                        },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
