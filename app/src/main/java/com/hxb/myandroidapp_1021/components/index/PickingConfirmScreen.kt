package com.hxb.myandroidapp_1021.components.index

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxb.myandroidapp_1021.components.index.components.SearchField
import com.hxb.myandroidapp_1021.components.index.components.ToolInfoTable
import com.hxb.myandroidapp_1021.components.index.models.DisplayField
import com.hxb.myandroidapp_1021.components.index.models.PickingOrder
import com.hxb.myandroidapp_1021.components.index.models.ToolInfoRow
import com.hxb.myandroidapp_1021.components.index.models.ToolScanItem




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickingConfirmScreen(
    order: PickingOrder? = null,
    onBackClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {}
) {
    val themeBlue = Color(0xFF1E71C8)
    val focusManager = LocalFocusManager.current
    val bgColor = Color(0xFFF5F6F8)
    val textDark = Color(0xFF222222)
    val textGray = Color(0xFF888888)
    val orderNo = order?.orderNo.orEmpty()
    val status = order?.status.orEmpty()
    val team = order?.team.orEmpty()
    val person = order?.person.orEmpty()
    val employeeIdState = rememberTextFieldState("")
    // 模拟读取到的工器具
    val toolList = remember {
        listOf(
            ToolScanItem(
                id = "GJ-01026",
                name = "绝缘鞋",
                code = "GJ-01026",
                rfid = "E200 1026 A001"
            ),
            ToolScanItem(
                id = "GJ-01018",
                name = "绝缘手套",
                code = "GJ-01018",
                rfid = "E200 1018 A002"
            ),
            ToolScanItem(
                id = "GJ-01031",
                name = "绝缘操作杆",
                code = "GJ-01031",
                rfid = "E200 1031 A003"
            ),
            ToolScanItem(
                id = "GJ-01118",
                name = "安全帽",
                code = "GJ-01118",
                rfid = "E200 1118 B001"
            )
        )
    }
    val infoFields = listOf(
        DisplayField("姓名", person),
        DisplayField("班组", team),
        DisplayField("经办人", "王芳（材料员）"),
        DisplayField("出库时间", "2026-10-30 09:30")
    )

    Scaffold(
            modifier = Modifier.imePadding(),
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .height(75.dp)
                    .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
                title = {
                    Text(
                        text = "借出·出库确认",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.offset(x = (-16).dp)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            focusManager.clearFocus()
                            onBackClick()
                        },
                        modifier = Modifier.offset(x = (-7).dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = themeBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Button(
                    onClick = { focusManager.clearFocus(); onConfirmClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = themeBlue)
                ) {
                    Text(
                        text = "确认出库",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
        ) {
            // === 1. 单据头部 ===
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "拣选单号：$orderNo",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textDark
                    )
                    Text(text = status.ifBlank { "待提交" }, fontSize = 14.sp, color = themeBlue)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "已读取${toolList.size}件 / 未读取0件",
                    fontSize = 14.sp,
                    color = textDark
                )
            }
            HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))

            // === 2. 领料人工号 + 信息行 ===
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = "领料人工号",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = textDark,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                SearchField(
                    state = employeeIdState,
                    placeholder = "请输入领料人工号",
                    showSearchIcon = false
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    infoFields.forEach { field ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = field.label,
                                fontSize = 13.sp,
                                lineHeight = 16.sp,
                                color = textGray,
                                maxLines = 1
                            )
                            Text(
                                text = field.value,
                                fontSize = 13.sp,
                                lineHeight = 16.sp,
                                color = textDark,
                                maxLines = 1,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
            }

            // === 3. 工器具表格 ===
            ToolInfoTable(
                rows = toolList.map { ToolInfoRow(rfid = it.rfid, name = it.name) }
            )
        }
    }
}
