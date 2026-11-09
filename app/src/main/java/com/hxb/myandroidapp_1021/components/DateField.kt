package com.hxb.myandroidapp_1021.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * 只读的日期选择输入框：框里的日期不能手输，点右侧日历图标弹 [DatePickerDialog]，
 * 选完（或点「确定」）按 `yyyy-MM-dd` 回写字符串；「取消」只关弹窗不动值。
 *
 * 送检 / 送修的「预计返回时间」、报废的「报废日期」共用，靠 [placeholder]、[contentDescription] 区分。
 *
 * 用法：
 * ```
 * DateField(
 *     value = returnDate,
 *     onValueChange = { returnDate = it },
 *     placeholder = "请选择报废日期",
 *     contentDescription = "选择报废日期"
 * )
 * ```
 *
 * @param value 当前日期字符串（yyyy-MM-dd），空串表示还没选
 * @param onValueChange 选完日期后的回写
 * @param placeholder 未选择时的提示文字
 * @param contentDescription 日历图标的无障碍描述
 */
@Composable
fun DateField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "请选择日期",
    contentDescription: String = "选择日期"
) {
    val themeBlue = Color(0xFF1E71C8)
    val focusManager = LocalFocusManager.current
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState =
        rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())

    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        placeholder = { Text(placeholder) },
        trailingIcon = {
            IconButton(onClick = {
                focusManager.clearFocus()
                showDatePicker = true
            }) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = contentDescription,
                    tint = themeBlue
                )
            }
        },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(4.dp),
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
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onValueChange(
                                SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).apply {
                                    timeZone = TimeZone.getTimeZone("UTC")
                                }.format(Date(millis))
                            )
                        }
                        showDatePicker = false
                    },
                    enabled = datePickerState.selectedDateMillis != null
                ) {
                    Text("确定", color = themeBlue)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("取消") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
