package com.hxb.myandroidapp_1021.components.index.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxb.myandroidapp_1021.components.index.ToolScanItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationScanScreen(
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
        ) ||
                tool.rfid.replace(" ", "").contains(query.replace(" ", ""), ignoreCase = true)
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
        topBar = { ApplicationTopBar("$applicationType·读取工具", onBackClick) },
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
                    Text(
                        "申请类别：${applicationType}（新申请）",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textDark
                    )
                    Text(
                        "申请人：张伟",
                        fontSize = 13.sp,
                        color = textDark,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    Text(
                        "当前已选：${readList.size}件",
                        fontSize = 13.sp,
                        color = textDark,
                        modifier = Modifier.padding(top = 8.dp)
                    )
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
                        onClick = {},
                        enabled = false,
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
                            "RFID读取（占位）",
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
                            Modifier.tabIndicatorOffset(
                                selectedTabIndex
                            ), color = themeBlue
                        )
                    }
                ) {
                    listOf("已读取 ${readList.size}", "手动选择").forEachIndexed { index, label ->
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
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "搜索"
                                )
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
                            if (selectedTabIndex == 0) "取消读取" else "确认",
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
                        "工具 / RFID",
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
            if (activeList.isEmpty()) {
                item {
                    Text(
                        if (selectedTabIndex == 0) "暂无已读取工具" else "暂无匹配工具",
                        fontSize = 14.sp, color = textGray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(16.dp)
                    )
                }
            }
            items(activeList, key = { it.id }) { tool ->
                ApplicationToolRow(tool) { checked ->
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
