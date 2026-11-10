package com.hxb.myandroidapp_1021.ui.picking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxb.myandroidapp_1021.data.models.DisplayField
import com.hxb.myandroidapp_1021.data.models.ScanTab
import com.hxb.myandroidapp_1021.data.models.ToolScanItemStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickingScanScreen(
    state: PickingScanViewModel,
    onManualClick: () -> Unit,
    onBackClick: () -> Unit = {},
    onNextClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    val themeBlue = Color(0xFF1E71C8)
    val bgColor = Color(0xFFF5F6F8)
    val textGray = Color(0xFF888888)
    val mockFields = remember {
        listOf(
            DisplayField("拣选单号", state.orderNo),
            DisplayField("单据状态", state.orderStatus),
            DisplayField("工单号", "GD20231028088"),
            DisplayField("归属车间", state.workshop),
            DisplayField("借用人", state.borrower),
            DisplayField("工具数量", "${state.tools.size}件"),
            DisplayField("计划日期", "2023-10-30 09:30"),
            DisplayField("创建人", "李四"),
            DisplayField("创建时间", "2023-10-28 10:00")
        )
    }
    var selectedTab by rememberSaveable { mutableStateOf(ScanTab.All) }
    val readList = state.readList
    val unreadList = state.unreadList
    val isNextEnabled = readList.isNotEmpty() && unreadList.isEmpty()

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
                    IconButton(onClick = onSettingsClick, modifier = Modifier.offset(x = (-4).dp)) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Setting",
                            tint = Color.White
                        )
                    }

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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // === 1. 顶部信息卡片 ===
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                mockFields.forEach { field ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(field.label, fontSize = 13.sp, color = Color(0xFF888888))
                        Text(
                            text = field.value,
                            fontSize = 13.sp,
                            color = Color(0xFF222222)
                        )
                    }
                }
            }
            HorizontalDivider(thickness = 8.dp, color = bgColor)

            // === 2. 读取控制区 ===
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {},
                    enabled = false,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    contentPadding = PaddingValues(4.dp),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "RFID 待开放",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = onManualClick,
                    modifier = Modifier.weight(1f).height(44.dp),
                    contentPadding = PaddingValues(4.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = themeBlue)
                ) {
                    Text("人工拣选", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onNextClick,
                    enabled = isNextEnabled,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    contentPadding = PaddingValues(4.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = themeBlue,
                        disabledContainerColor = Color(0xFFB0BEC5)
                    )
                ) {
                    Text(
                        text = "下一步",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            HorizontalDivider(thickness = 8.dp, color = bgColor)

            // === 3. 三个 Tab ===
            val tabLabels = listOf(
                "全部 ${state.tools.size}",
                "已读取 ${readList.size}",
                "未读取 ${unreadList.size}"
            )
            SecondaryTabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = Color.White,
                contentColor = themeBlue,
                indicator = {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(selectedTab.ordinal),
                        color = themeBlue
                    )
                },
                divider = { HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F0)) }
            ) {
                tabLabels.forEachIndexed { index, label ->
                    Tab(
                        selected = selectedTab.ordinal == index,
                        onClick = { selectedTab = ScanTab.entries[index] },
                        text = {
                            Text(
                                text = label,
                                color = if (selectedTab.ordinal == index) themeBlue else textGray,
                                fontWeight = if (selectedTab.ordinal == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // === 4. Tab 内容 ===
            val visibleTools = when (selectedTab) {
                ScanTab.All -> state.tools
                ScanTab.Read -> readList
                ScanTab.Unread -> unreadList
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(visibleTools, key = { it.id }) { tool ->
                    val picked = tool.status == ToolScanItemStatus.PICKED
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF9FAFB),
                        border = BorderStroke(
                            width = 1.dp,
                            color = Color(0xFFE4E9F0)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
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
}
