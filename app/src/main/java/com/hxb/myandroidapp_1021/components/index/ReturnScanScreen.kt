package com.hxb.myandroidapp_1021.components.index

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxb.myandroidapp_1021.components.index.models.DisplayField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReturnScanScreen(
    onBackClick: () -> Unit = {},
    onCompleteClick: () -> Unit = {}
) {
    val themeBlue = Color(0xFF1E71C8)
    val bgColor = Color(0xFFF5F6F8)
    val textDark = Color(0xFF222222)
    val textGray = Color(0xFF888888)
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showBindLocation by remember { mutableStateOf(false) }
    val readList = remember {
        mutableStateListOf(
            ToolScanItem("GJ-01118", "绝缘操作杆", "GJ-01118", "E200 1118 B001"),
            ToolScanItem("GJ-01155", "安全帽", "GJ-01155", "E200 1155 B002")
        )
    }
    val unreadList = remember {
        mutableStateListOf(
            ToolScanItem("GJ-01162", "绝缘绳", "GJ-01162", "E200 1162 B003")
        )
    }
    val activeList = if (selectedTabIndex == 0) readList else unreadList
    val isNextEnabled = readList.isNotEmpty() && unreadList.isEmpty()

    if (showBindLocation) {
        BackHandler { showBindLocation = false }
        ReturnConfirmScreen(
            tools = readList.toList(),
            onBackClick = { showBindLocation = false },
            onConfirmClick = onCompleteClick
        )
        return
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                modifier = Modifier.height(75.dp),
                title = {
                    Text(
                        "归还·读取", fontSize = 18.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.offset(x = (-16).dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.offset(x = (-7).dp)) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = themeBlue, titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Button(
                    onClick = { showBindLocation = true },
                    enabled = isNextEnabled,
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
                        "下一步",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Row(
                        Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "JC20261027006",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = textDark
                        )
                        Text("部分归还", fontSize = 14.sp, color = themeBlue)
                    }
                    Column(
                        Modifier.padding(top = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            DisplayField("班组", "检修一班"),
                            DisplayField("归还人", "张伟"),
                            DisplayField("应还件数", "5件"),
                            DisplayField("历史已还", "2件"),
                            DisplayField("本次待还", "${readList.size + unreadList.size}件")
                        ).forEach { field ->
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "${field.label}：",
                                    fontSize = 13.sp,
                                    color = textGray,
                                    modifier = Modifier.width(72.dp)
                                )
                                Text(
                                    field.value,
                                    fontSize = 13.sp,
                                    color = textDark,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
                HorizontalDivider(thickness = 8.dp, color = bgColor)
            }
            item {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {}, enabled = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            disabledContainerColor = Color(
                                0xFFB0BEC5
                            )
                        )
                    ) {
                        Text(
                            "RFID读取",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                HorizontalDivider(thickness = 8.dp, color = bgColor)
            }
            item {
                SecondaryTabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.White,
                    contentColor = themeBlue,
                    indicator = {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(selectedTabIndex),
                            color = themeBlue
                        )
                    }
                ) {
                    listOf(
                        "已还 ${readList.size}",
                        "未还 ${unreadList.size}"
                    ).forEachIndexed { index, label ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    label,
                                    color = if (selectedTabIndex == index) themeBlue else textGray,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                                )
                            })
                    }
                }
            }
            item {
                if (selectedTabIndex == 1) {
                    ManualRfidInput(
                        existingRfids = (readList + unreadList).map { it.rfid },
                        selectedCount = unreadList.count { it.isChecked },
                        readLabel = "人工读取",
                        onRead = { showConfirmDialog = true }
                    ) { rfid ->
                        unreadList.add(ToolScanItem(id = rfid, rfid = rfid, isChecked = true))
                    }
                } else {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "已选${activeList.count { it.isChecked }}件",
                            fontSize = 14.sp,
                            color = textGray
                        )
                        Button(
                            onClick = { showConfirmDialog = true },
                            enabled = activeList.any { it.isChecked },
                            modifier = Modifier.height(30.dp),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = themeBlue,
                                disabledContainerColor = Color(0xFFB0BEC5)
                            )
                        ) {
                            Text(
                                if (selectedTabIndex == 0) "取消读取" else "人工读取",
                                fontSize = 13.sp,
                                color = Color.White
                            )
                        }
                    }
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(bgColor)
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        "RFID",
                        modifier = Modifier.weight(1f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = textGray
                    )
                    Text(
                        "数量/件",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = textGray
                    )
                }
                HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))
            }
            items(activeList, key = { it.id }) { tool ->
                Column(Modifier.background(Color.White)) {
                    ReadItemRow(tool, themeBlue, textDark) { checked ->
                        val index = activeList.indexOf(tool)
                        if (index != -1) activeList[index] = tool.copy(isChecked = checked)
                    }
                    HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F0))
                }
            }

            item {
                Spacer(
                    modifier = Modifier
                        .height(20.dp)
                        .fillMaxWidth()
                        .background(Color.White)
                )
            }
        }
    }
    if (showConfirmDialog) {
        val isReturnAction = selectedTabIndex == 1
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text(if (isReturnAction) "确认归还" else "取消确认") },
            text = { Text("${if (isReturnAction) "核实实物后，将" else "将"}选中的${activeList.count { it.isChecked }}件工具转入${if (isReturnAction) "已还" else "未还"}？") },
            containerColor = Color.White,
            confirmButton = {
                TextButton(onClick = {
                    val selected = activeList.filter { it.isChecked }
                    val destination = if (isReturnAction) readList else unreadList
                    destination.addAll(selected.map { it.copy(isChecked = false) })
                    activeList.removeAll(selected)
                    showConfirmDialog = false
                }) { Text("确认", color = themeBlue) }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text(
                        "取消",
                        color = textGray
                    )
                }
            }
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun ReturnScanScreenPreview() {
    ReturnScanScreen()
}

@Composable
private fun ManualRfidInput(
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
