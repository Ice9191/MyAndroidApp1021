package com.hxb.myandroidapp_1021.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.outlined.FactCheck
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Outbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.hxb.myandroidapp_1021.models.MenuItemData
import com.hxb.myandroidapp_1021.models.MenuSectionData
import com.hxb.myandroidapp_1021.routes.InspectionList
import com.hxb.myandroidapp_1021.routes.InventoryList
import com.hxb.myandroidapp_1021.routes.PickingList
import com.hxb.myandroidapp_1021.routes.RepairList
import com.hxb.myandroidapp_1021.routes.ReturnList
import com.hxb.myandroidapp_1021.routes.ScrapList
import com.hxb.myandroidapp_1021.routes.ScrapOutList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onLogoutClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onMenuClick: (Any) -> Unit = {}
) {
    val themeBlue = Color(0xFF1E71C8)
    val bgColor = Color(0xFFF5F6F8)
    val lightBlueBg = Color(0xFFEDF3FB)
    val textDark = Color(0xFF222222)
    val textGray = Color(0xFF888888)
    val greenColor = Color(0xFF4CAF50)
    val dividerColor = Color(0xFFF0F0F0)
    val menuSections = listOf(
        MenuSectionData(
            title = "借用归还",
            items = listOf(
                MenuItemData("借出", Icons.AutoMirrored.Filled.ExitToApp, PickingList),
                MenuItemData("归还", Icons.AutoMirrored.Filled.Login, ReturnList)
            )
        ),
        MenuSectionData(
            title = "库存管理",
            items = listOf(
                MenuItemData("工具送检", Icons.AutoMirrored.Outlined.FactCheck, InspectionList),
                MenuItemData("工具送修", Icons.Outlined.Build, RepairList)
            )
        ),
        MenuSectionData(
            title = "库存核减",
            items = listOf(
                MenuItemData("工具报废", Icons.Outlined.DeleteForever, ScrapList),
                MenuItemData("报废出库", Icons.Outlined.Outbox, ScrapOutList)
            )
        ),
        MenuSectionData(
            title = "盘点管理",
            items = listOf(
                MenuItemData("盘点单", Icons.Filled.QrCodeScanner, InventoryList)
            )
        )
    )

    Scaffold(
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                modifier = Modifier.height(75.dp),
                title = {
                    Text(
                        "工器具库系统",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    TextButton(
                        onClick = onLogoutClick,
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Text(
                            text = "退出登录",
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier.offset(x = (-4).dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = themeBlue,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // 登录人信息
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = "https://picsum.photos/800/300",
                    contentDescription = "Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(4.dp))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "欢迎回来",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "张三 · 材料员", fontSize = 13.sp, color = textGray)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Wifi,
                        contentDescription = "Connected",
                        tint = greenColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "服务器已连接", fontSize = 14.sp, color = greenColor)
                }
            }

            HorizontalDivider(thickness = 1.dp, color = dividerColor)

            // 模块
            menuSections.forEach { section ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    MenuSectionTitle(section.title)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        section.items.forEach { item ->
                            MenuButton(
                                item = item,
                                modifier = Modifier.weight(1f),
                                themeBlue = themeBlue,
                                bgColor = lightBlueBg,
                                onClick = {
                                    onMenuClick(item.route)
                                }
                            )
                        }

                        if (section.items.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                }

                HorizontalDivider(thickness = 1.dp, color = dividerColor)
            }
        }
    }
}

@Composable
fun MenuSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF333333),
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun MenuButton(
    item: MenuItemData,
    modifier: Modifier = Modifier,
    themeBlue: Color,
    bgColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 16.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            tint = themeBlue,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = item.title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF333333)
        )
    }
}