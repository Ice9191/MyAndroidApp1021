package com.hxb.myandroidapp_1021.components.index.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ManualRfidInput(
    existingRfids: List<String>,
    selectedCount: Int,
    readLabel: String,
    onRead: () -> Unit,
    onAdd: (String) -> Unit
) {
    val themeBlue = Color(0xFF1E71C8)
    var input by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val normalized = input.replace(" ", "").uppercase()

    Column(Modifier
        .fillMaxWidth()
        .background(Color.White)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("已选${selectedCount}件", fontSize = 14.sp, color = Color(0xFF888888))
            BasicTextField(
                value = input,
                onValueChange = { input = it; error = null },
                singleLine = true,
                modifier = Modifier.weight(1f),
                textStyle = TextStyle(fontSize = 13.sp, color = Color(0xFF222222)),
                cursorBrush = SolidColor(themeBlue),
                decorationBox = { innerTextField ->
                    Column {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(32.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (input.isEmpty()) {
                                Text("输入RFID编码", fontSize = 13.sp, color = Color(0xFF888888))
                            }
                            innerTextField()
                        }
                        HorizontalDivider(
                            color = if (error != null) Color(0xFFB3261E) else Color(
                                0xFFDDDDDD
                            )
                        )
                    }
                }
            )
            Button(
                onClick = {
                    if (normalized.isBlank()) {
                        onRead()
                    } else if (existingRfids.any {
                            it.replace(" ", "").uppercase() == normalized
                        }) {
                        error = "该RFID已在列表中，请勿重复添加"
                    } else {
                        onAdd(normalized)
                        input = ""
                        error = null
                    }
                },
                enabled = normalized.isNotBlank() || selectedCount > 0,
                modifier = Modifier.height(30.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = themeBlue,
                    disabledContainerColor = Color(0xFFB0BEC5)
                )
            ) {
                Text(
                    if (normalized.isNotBlank()) "添加" else readLabel,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
        }
        error?.let {
            Text(it, color = Color(0xFFB3261E), modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}
