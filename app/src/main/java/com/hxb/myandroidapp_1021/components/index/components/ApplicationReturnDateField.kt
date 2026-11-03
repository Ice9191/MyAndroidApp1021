package com.hxb.myandroidapp_1021.components.index.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationReturnDateField(value: String, onValueChange: (String) -> Unit) {
    val themeBlue = Color(0xFF1E71C8)
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState =
        rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
    OutlinedTextField(
        value = value, onValueChange = {}, readOnly = true,
        placeholder = { Text("请选择预计返回日期") },
        trailingIcon = {
            IconButton(onClick = { showDatePicker = true }) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = "选择预计返回日期",
                    tint = themeBlue
                )
            }
        },
        modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(4.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = themeBlue,
            unfocusedBorderColor = Color(0xFFDDDDDD),
            cursorColor = themeBlue
        )
    )
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onValueChange(SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).apply {
                            timeZone = TimeZone.getTimeZone("UTC")
                        }.format(Date(millis)))
                    }
                    showDatePicker = false
                }, enabled = datePickerState.selectedDateMillis != null) {
                    Text(
                        "确定",
                        color = themeBlue
                    )
                }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("取消") } }
        ) { DatePicker(state = datePickerState) }
    }
}
