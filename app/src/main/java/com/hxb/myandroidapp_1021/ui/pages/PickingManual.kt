package com.hxb.myandroidapp_1021.ui.pages

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxb.myandroidapp_1021.data.models.DisplayField
import com.hxb.myandroidapp_1021.data.models.ToolScanItemStatus
import com.hxb.myandroidapp_1021.ui.viewmodel.PickingScanState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickingByMan(
    state: PickingScanState,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit
) {
    val themeBlue = Color(0xFF1E71C8)
    val bgColor = Color(0xFFF5F6F8)
    val textGray = Color(0xFF888888)
    var selectedTab by remember { mutableStateOf(0) }
    var selectedIds by remember { mutableStateOf(emptySet<Int>()) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    val readList = state.readList
    val unreadList = state.unreadList

    Scaffold(
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                modifier = Modifier.height(75.dp),
                title = {
                    Text(
                        text = "借出·拣选",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.offset(x = (-16).dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.offset(x = (-7).dp)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {

                    IconButton(onClick = onHomeClick, modifier = Modifier.offset(x = (-4).dp)) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = themeBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Surface(color = Color.White) {
                Button(
                    onClick = { showConfirmDialog = true },
                    enabled = selectedIds.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .height(44.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = themeBlue)
                ) {
                    Text("确认")
                }
            }
        }
    ) { paddingValues ->
        Column(Modifier.fillMaxSize().padding(paddingValues)) {
            val tabLabels = listOf("已读取 ${readList.size}", "未读取 ${unreadList.size}")
            SecondaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = themeBlue,
                indicator = {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(selectedTab),
                        color = themeBlue
                    )
                },
                divider = { HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F0)) }
            ) {
                tabLabels.forEachIndexed { index, label ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = label,
                                color = if (selectedTab == index) themeBlue else textGray,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
            val visibleTools = if (selectedTab == 0) readList else unreadList
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(visibleTools, key = { it.id }) { tool ->
                    val selected = tool.id in selectedIds
                    val picked = tool.status == ToolScanItemStatus.PICKED
                    val lift by animateDpAsState(if (selected) 2.dp else 0.dp, label = "cardLift")
                    val selectionModifier = Modifier.toggleable(
                        value = selected,
                        role = Role.Checkbox,
                        onValueChange = {
                            selectedIds = if (selected) selectedIds - tool.id else selectedIds + tool.id
                        }
                    )
                    Surface(
                        modifier = Modifier.fillMaxWidth().offset(y = -lift),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF9FAFB),
                        border = BorderStroke(
                            width = if (selected) 2.dp else 1.dp,
                            color = if (selected) themeBlue else Color(0xFFE4E9F0)
                        ),
                        shadowElevation = lift * 2
                    ) {
                        Column(
                            modifier = selectionModifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "行号 ${tool.id.toString().padStart(2, '0')}",
                                    fontSize = 12.sp,
                                    color = textGray
                                )
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (picked) Color(0xFFE7F6EB) else Color(0xFFF0F2F5)
                                ) {
                                    Text(
                                        text = tool.status.label,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        fontSize = 11.sp,
                                        color = if (picked) Color(0xFF16763C) else Color(0xFF596575)
                                    )
                                }
                            }
                            Text(tool.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF182536))
                            listOf(
                                DisplayField("物资编码", tool.itemCode),
                                DisplayField("RFID编码", tool.rfid),
                                DisplayField("推荐货位", tool.recommendedLocation),
                                DisplayField("单位", tool.unit)
                            ).forEach { field ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = field.label,
                                        fontSize = 13.sp,
                                        color = Color(0xFF888888),
                                        maxLines = 1
                                    )
                                    Text(
                                        text = field.value,
                                        fontSize = 13.sp,
                                        color = Color(0xFF222222),
                                        maxLines = 1,
                                        modifier = Modifier.padding(start = 16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showConfirmDialog) {
        val toReadCount = unreadList.count { it.id in selectedIds }
        val toUnreadCount = readList.count { it.id in selectedIds }
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("确认操作") },
            text = {
                Column {
                    if (toReadCount > 0) Text("人工读取 $toReadCount 件工具")
                    if (toUnreadCount > 0) Text("取消读取 $toUnreadCount 件工具")
                }
            },
            containerColor = Color.White,
            confirmButton = {
                TextButton(onClick = {
                    state.toggleRead(selectedIds)
                    selectedIds = emptySet()
                    showConfirmDialog = false
                }) {
                    Text("确认", color = themeBlue)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("取消", color = textGray)
                }
            }
        )
    }
}
