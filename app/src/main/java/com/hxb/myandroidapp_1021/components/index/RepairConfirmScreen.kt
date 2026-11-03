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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.hxb.myandroidapp_1021.components.index.components.ApplicationReturnDateField
import com.hxb.myandroidapp_1021.components.index.components.ApplicationToolRow
import com.hxb.myandroidapp_1021.components.index.components.ApplicationTopBar
import com.hxb.myandroidapp_1021.components.index.components.RepairPhotoInput

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepairConfirmScreen(
    tools: List<ToolScanItem> = listOf(
        ToolScanItem(
            "GJ-02008",
            "数字万用表",
            "GJ-02008",
            "E200 2008 D001"
        )
    ),
    onBackClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {}
) {
    val themeBlue = Color(0xFF1E71C8)
    val bgColor = Color(0xFFF5F6F8)
    val textDark = Color(0xFF222222)
    var reason by remember { mutableStateOf("") }
    var faultDescription by remember { mutableStateOf("") }
    var returnDate by remember { mutableStateOf("") }
    var showConfirmDialog by remember { mutableStateOf(false) }
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = themeBlue,
        unfocusedBorderColor = Color(0xFFDDDDDD),
        cursorColor = themeBlue
    )
    val canSubmit =
        tools.isNotEmpty() && reason.isNotBlank() && faultDescription.isNotBlank() && returnDate.isNotBlank()

    Scaffold(
        modifier = Modifier.imePadding(), containerColor = bgColor,
        topBar = { ApplicationTopBar("送修·填写信息", onBackClick) },
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
                        "确认",
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
            Column(Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "送修申请信息",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textDark
                    )
                    Text("待提交", fontSize = 14.sp, color = themeBlue)
                }
                Text(
                    "申请人：张伟",
                    fontSize = 13.sp,
                    color = textDark,
                    modifier = Modifier.padding(top = 12.dp)
                )
                Text(
                    "已选工具：${tools.size}件",
                    fontSize = 13.sp,
                    color = textDark,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            HorizontalDivider(thickness = 8.dp, color = bgColor)
            tools.forEach { tool -> ApplicationToolRow(tool) }
            HorizontalDivider(thickness = 8.dp, color = bgColor)
            Column(Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)) {
                Text("送修原因", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textDark)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    placeholder = { Text("请输入送修原因") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(4.dp),
                    colors = fieldColors
                )
                Spacer(Modifier.height(16.dp))
                Text("故障描述", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textDark)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = faultDescription,
                    onValueChange = { faultDescription = it },
                    placeholder = { Text("请输入故障描述") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(4.dp),
                    colors = fieldColors
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    "预计返回日期",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = textDark
                )
                Spacer(Modifier.height(8.dp))
                ApplicationReturnDateField(value = returnDate, onValueChange = { returnDate = it })
                Spacer(Modifier.height(16.dp))
                Text("故障 / 现场图片（选填）", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textDark)
                Spacer(Modifier.height(8.dp))
                RepairPhotoInput()
            }
            Spacer(Modifier
                .height(20.dp)
                .fillMaxWidth()
                .background(Color.White))
        }
    }
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false }, containerColor = Color.White,
            title = { Text("确认送修申请") },
            text = { Text("共${tools.size}件工具\n送修原因：${reason.trim()}\n故障描述：${faultDescription.trim()}\n预计返回：$returnDate") },
            confirmButton = {
                TextButton(onClick = { showConfirmDialog = false; onConfirmClick() }) {
                    Text(
                        "确认",
                        color = themeBlue
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text(
                        "取消",
                        color = Color(0xFF888888)
                    )
                }
            }
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun RepairConfirmScreenPreview() {
    RepairConfirmScreen()
}
