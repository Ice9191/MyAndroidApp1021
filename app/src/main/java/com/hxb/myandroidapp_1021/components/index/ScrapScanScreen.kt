package com.hxb.myandroidapp_1021.components.index

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 工具报废·读取工具页（结构对照送修页，业务换成报废）。
 */
@Composable
fun ScrapScanScreen(onBackClick: () -> Unit = {}, onCompleteClick: () -> Unit = {}) {
    ScrapScanContent(
        applicationType = "报废",
        initialTools = listOf(
            ToolScanItem("GJ-01055", "安全帽", "GJ-01055", "E200 1055 C001"),
            ToolScanItem("GJ-01062", "绝缘绳", "GJ-01062", "E200 1062 C002"),
            ToolScanItem("GJ-01070", "绝缘手套", "GJ-01070", "E200 1070 C003")
        ),
        onBackClick = onBackClick
    ) { tools, onBack ->
        ScrapConfirmScreen(tools = tools, onBackClick = onBack, onConfirmClick = onCompleteClick)
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun ScrapScanScreenPreview() {
    ScrapScanScreen()
}

@Composable
private fun ScrapScanContent(
    applicationType: String,
    initialTools: List<ToolScanItem>,
    onBackClick: () -> Unit,
    confirmationScreen: @Composable (List<ToolScanItem>, () -> Unit) -> Unit
) {
    val themeBlue = Color(0xFF1E71C8)
    val bgColor = Color(0xFFF5F6F8)
    val textDark = Color(0xFF222222)
    val textGray = Color(0xFF888888)
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var searchText by remember { mutableStateOf("") }
    var showConfirmScreen by remember { mutableStateOf(false) }
    val readList = remember { mutableStateListOf<ToolScanItem>() }
    val availableTools =
        remember { mutableStateListOf<ToolScanItem>().apply { addAll(initialTools) } }
    val filteredTools = availableTools.filter { tool ->
        val query = searchText.trim()
        tool.name.contains(query, ignoreCase = true) || tool.code.contains(
            query,
            ignoreCase = true
        ) || tool.rfid.replace(" ", "").contains(query.replace(" ", ""), ignoreCase = true)
    }
    val activeList = if (selectedTabIndex == 0) readList.toList() else filteredTools
    val selectedCount = activeList.count { it.isChecked }

    if (showConfirmScreen) {
        BackHandler { showConfirmScreen = false }
        confirmationScreen(readList.toList()) { showConfirmScreen = false }
        return
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        containerColor = bgColor,
        topBar = { ScrapScanTopBar("$applicationType·读取工具", onBackClick) },
        bottomBar = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Button(
                    onClick = { showConfirmScreen = true },
                    enabled = readList.isNotEmpty(),
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
                        text = "下一步",
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
            // 申请信息
            item {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "申请类别：${applicationType}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textDark
                    )
                    Text(
                        text = "申请人：张伟",
                        fontSize = 13.sp,
                        color = textDark,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    Text(
                        text = "当前已选：${readList.size}件",
                        fontSize = 13.sp,
                        color = textDark,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                HorizontalDivider(thickness = 8.dp, color = bgColor)
            }

            // RFID 读取（占位）
            item {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            disabledContainerColor = Color(0xFFB0BEC5)
                        )
                    ) {
                        Text(
                            text = "RFID读取",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                HorizontalDivider(thickness = 8.dp, color = bgColor)
            }

            // Tab：已读取 / 手动选择
            item {
                SecondaryTabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.White,
                    contentColor = themeBlue,
                    indicator = {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(selectedTabIndex),
                            color = themeBlue
                        )
                    }
                ) {
                    listOf("已读取 ${readList.size}", "手动选择").forEachIndexed { index, label ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = label,
                                    color = if (selectedTabIndex == index) themeBlue else textGray,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }

            // 手动选择时的搜索框
            if (selectedTabIndex == 1) {
                item {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        OutlinedTextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            placeholder = { Text("搜索工具名称、编号或RFID", fontSize = 13.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = "搜索")
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(4.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = themeBlue,
                                unfocusedBorderColor = Color(0xFFDDDDDD),
                                cursorColor = themeBlue
                            )
                        )
                    }
                }
            }

            // 已选统计 + 加入/移除按钮 + 表头
            item {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("已选${selectedCount}件", fontSize = 14.sp, color = textGray)
                    Button(
                        onClick = {
                            val selected = activeList.filter { it.isChecked }
                            if (selectedTabIndex == 1) {
                                readList.addAll(selected.map { it.copy(isChecked = false) })
                                availableTools.removeAll(selected)
                            } else {
                                availableTools.addAll(selected.map { it.copy(isChecked = false) })
                                readList.removeAll(selected)
                            }
                        },
                        enabled = selectedCount > 0,
                        modifier = Modifier.height(30.dp),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = themeBlue,
                            disabledContainerColor = Color(0xFFB0BEC5)
                        )
                    ) {
                        Text(
                            text = if (selectedTabIndex == 0) "取消读取" else "确认",
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(bgColor)
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "工具 / RFID",
                        modifier = Modifier.weight(1f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = textGray
                    )
                    Text(
                        text = "数量/件",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = textGray
                    )
                }
                HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))
            }

            // 空状态
            if (activeList.isEmpty()) {
                item {
                    Text(
                        text = if (selectedTabIndex == 0) "暂无已读取工具" else "暂无匹配工具",
                        fontSize = 14.sp,
                        color = textGray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(16.dp)
                    )
                }
            }

            // 工具列表
            items(activeList, key = { it.id }) { tool ->
                ScrapScanToolRow(tool) { checked ->
                    val source = if (selectedTabIndex == 0) readList else availableTools
                    val index = source.indexOf(tool)
                    if (index != -1) source[index] = tool.copy(isChecked = checked)
                }
            }

            item {
                Spacer(
                    Modifier
                        .height(20.dp)
                        .fillMaxWidth()
                        .background(Color.White)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScrapScanTopBar(title: String, onBackClick: () -> Unit) {
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
private fun ScrapScanToolRow(tool: ToolScanItem, onCheckedChange: ((Boolean) -> Unit)? = null) {
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
