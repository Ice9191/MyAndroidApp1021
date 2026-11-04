package com.hxb.myandroidapp_1021.components.index

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * 工具报废·填写信息页（对照送修页做成一样的结构，字段换成报废相关的）。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrapConfirmScreen(
    tools: List<ToolScanItem> = listOf(
        ToolScanItem(
            "GJ-01055",
            "安全帽",
            "GJ-01055",
            "E200 1055 C001"
        )
    ),
    onBackClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {}
) {
    val themeBlue = Color(0xFF1E71C8)
    val bgColor = Color(0xFFF5F6F8)
    val textDark = Color(0xFF222222)
    var reason by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var scrapDate by remember { mutableStateOf("") }
    var showConfirmDialog by remember { mutableStateOf(false) }
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = themeBlue,
        unfocusedBorderColor = Color(0xFFDDDDDD),
        cursorColor = themeBlue
    )
    val canSubmit =
        tools.isNotEmpty() && reason.isNotBlank() && description.isNotBlank() && scrapDate.isNotBlank()

    Scaffold(
        modifier = Modifier.imePadding(),
        containerColor = bgColor,
        topBar = { ScrapTopBar("报废·填写信息", onBackClick) },
        bottomBar = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Button(
                    onClick = { showConfirmDialog = true },
                    enabled = canSubmit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = themeBlue,
                        disabledContainerColor = Color(0xFFB0BEC5)
                    )
                ) {
                    Text(
                        text = "确认",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // 报废申请信息
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "报废申请信息",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textDark
                    )
                    Text(text = "待提交", fontSize = 14.sp, color = themeBlue)
                }
                Text(
                    text = "申请人：张伟",
                    fontSize = 13.sp,
                    color = textDark,
                    modifier = Modifier.padding(top = 12.dp)
                )
                Text(
                    text = "已选工具：${tools.size}件",
                    fontSize = 13.sp,
                    color = textDark,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            HorizontalDivider(thickness = 8.dp, color = bgColor)

            // 工具清单
            tools.forEach { tool -> ScrapToolRow(tool) }
            HorizontalDivider(thickness = 8.dp, color = bgColor)

            // 表单
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Text("报废原因", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textDark)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    placeholder = { Text("请输入报废原因") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(4.dp),
                    colors = fieldColors
                )

                Spacer(Modifier.height(16.dp))
                Text("报废说明", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textDark)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("请输入损坏情况、处置说明等") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(4.dp),
                    colors = fieldColors
                )

                Spacer(Modifier.height(16.dp))
                Text("报废日期", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textDark)
                Spacer(Modifier.height(8.dp))
                ScrapDateField(value = scrapDate, onValueChange = { scrapDate = it })

                Spacer(Modifier.height(16.dp))
                Text("报废现场图片（选填）", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textDark)
                Spacer(Modifier.height(8.dp))
                ScrapPhotoInput()
            }
            Spacer(
                Modifier
                    .height(20.dp)
                    .fillMaxWidth()
                    .background(Color.White)
            )
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            containerColor = Color.White,
            title = { Text("确认报废申请") },
            text = {
                Text(
                    "共${tools.size}件工具\n报废原因：${reason.trim()}\n" +
                            "报废说明：${description.trim()}\n报废日期：$scrapDate"
                )
            },
            confirmButton = {
                TextButton(onClick = { showConfirmDialog = false; onConfirmClick() }) {
                    Text("确认", color = themeBlue)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("取消", color = Color(0xFF888888))
                }
            }
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun ScrapConfirmScreenPreview() {
    ScrapConfirmScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScrapTopBar(title: String, onBackClick: () -> Unit) {
    TopAppBar(
        modifier = Modifier.height(75.dp),
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.offset(x = (-16).dp)
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick, modifier = Modifier.offset(x = (-7).dp)) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "返回")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF1E71C8),
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White
        )
    )
}

@Composable
private fun ScrapToolRow(tool: ToolScanItem, onCheckedChange: ((Boolean) -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onCheckedChange != null) {
            Checkbox(
                checked = tool.isChecked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF1E71C8))
            )
        }
        Column(Modifier.weight(1f)) {
            Text(
                text = tool.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222222)
            )
            Text(
                text = tool.code,
                fontSize = 13.sp,
                color = Color(0xFF888888),
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "RFID：${tool.rfid}",
                fontSize = 13.sp,
                color = Color(0xFF888888),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Text(text = "1", fontSize = 14.sp, color = Color(0xFF222222))
    }
    HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F0))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScrapDateField(value: String, onValueChange: (String) -> Unit) {
    val themeBlue = Color(0xFF1E71C8)
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState =
        rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        placeholder = { Text("请选择报废日期") },
        trailingIcon = {
            IconButton(onClick = { showDatePicker = true }) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "选择报废日期",
                    tint = themeBlue
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
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

@Composable
private fun ScrapPhotoInput() {
    val themeBlue = Color(0xFF1E71C8)
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedButton(
            onClick = {
                // 拍照功能暂留占位。
            },
            modifier = Modifier
                .weight(1f)
                .height(44.dp),
            shape = RoundedCornerShape(4.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.CameraAlt,
                contentDescription = null,
                tint = themeBlue,
                modifier = Modifier.size(18.dp)
            )
            Text(" 拍照", fontSize = 13.sp, color = themeBlue)
        }
        OutlinedButton(
            onClick = {
                // 相册选择功能暂留占位。
            },
            modifier = Modifier
                .weight(1f)
                .height(44.dp),
            shape = RoundedCornerShape(4.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.PhotoLibrary,
                contentDescription = null,
                tint = themeBlue,
                modifier = Modifier.size(18.dp)
            )
            Text(" 从相册选择", fontSize = 13.sp, color = themeBlue)
        }
    }
}
