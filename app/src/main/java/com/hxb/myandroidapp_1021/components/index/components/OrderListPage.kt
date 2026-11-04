package com.hxb.myandroidapp_1021.components.index.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxb.myandroidapp_1021.components.index.models.DisplayField
import kotlinx.coroutines.launch

/** 三个列表页统一配色 */
private val ThemeBlue = Color(0xFF1E71C8)
private val PageBg = Color(0xFFF5F6F8)
private val TextDark = Color(0xFF222222)
private val TextGray = Color(0xFF888888)

/**
 * 借出 / 归还 / 报废三个列表页共用的页面骨架：
 * 蓝色标题栏 + 搜索框 + 列表头 + 「单号 / 状态 / 按钮 / 字段明细」卡片列表 + 回到顶部按钮。
 *
 * 用法（借出为例）：
 * ```
 * OrderListPage(
 *     title = "借出",
 *     orders = orderList,
 *     searchPlaceholder = "请输入拣选单号",
 *     orderNo = { it.orderNo },
 *     fields = { order -> listOf(DisplayField("班组", order.team), ...) },
 *     statusText = { it.status },
 *     actionText = "开始拣选",
 *     listHeaderLabel = "待处理拣选单",
 *     searchMatcher = { order, q -> order.orderNo.contains(q, ignoreCase = true) }
 * )
 * ```
 *
 * @param title 标题栏文字
 * @param orders 已由调用方做过业务过滤（如报废只留审批通过的）的列表
 * @param searchPlaceholder 搜索框提示文字
 * @param orderNo 取单号
 * @param fields 取卡片里的「标签：值」明细行
 * @param statusText 单号右侧的状态文字，返回 null 则不显示
 * @param actionText 单号右侧的按钮文字，null 则不显示按钮
 * @param onActionClick 按钮点击
 * @param listHeaderLabel 列表头左侧的普通文字（与 listHeader 二选一）
 * @param listHeader 列表头左侧自定义内容（如下拉筛选），优先级高于 listHeaderLabel
 * @param searchMatcher 搜索过滤规则，传 null 则不按搜索词过滤
 * @param emptyText 列表为空时的提示
 * @param noResultText 有搜索词但没匹配到时的提示
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> OrderListPage(
    title: String,
    orders: List<T>,
    searchPlaceholder: String,
    orderNo: (T) -> String,
    fields: (T) -> List<DisplayField>,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    statusText: (T) -> String? = { null },
    actionText: String? = null,
    onActionClick: (T) -> Unit = {},
    listHeaderLabel: String? = null,
    listHeader: (@Composable () -> Unit)? = null,
    searchMatcher: ((T, String) -> Boolean)? = null,
    emptyText: String = "暂无数据",
    noResultText: String = "暂无匹配数据"
) {
    val focusManager = LocalFocusManager.current
    val searchState = rememberTextFieldState()
    val query = searchState.text.toString().trim()
    val visibleOrders = if (searchMatcher == null || query.isEmpty()) {
        orders
    } else {
        orders.filter { searchMatcher(it, query) }
    }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val showScrollToTopButton by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 2 } // 向下滑过 2 个条目再显示回到顶部
    }

    Scaffold(
        modifier = modifier.imePadding(),
        containerColor = PageBg,
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .height(75.dp)
                    .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
                title = {
                    Text(
                        text = title,
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
                    containerColor = ThemeBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // === 1. 顶部搜索框 ===
                SearchField(state = searchState, placeholder = searchPlaceholder)

                // === 2. 列表 ===
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState
                ) {
                    // 列表头：左侧说明 / 筛选，右侧总数
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(PageBg)
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (listHeader != null) {
                                listHeader()
                            } else {
                                Text(
                                    text = listHeaderLabel.orEmpty(),
                                    fontSize = 14.sp,
                                    color = TextGray
                                )
                            }
                            Text(
                                text = "共${visibleOrders.size}张",
                                fontSize = 14.sp,
                                color = TextGray
                            )
                        }
                    }

                    // 空数据占位
                    if (visibleOrders.isEmpty()) {
                        item {
                            Text(
                                text = if (query.isEmpty()) emptyText else noResultText,
                                fontSize = 14.sp,
                                color = TextGray,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White)
                                    .padding(16.dp)
                            )
                        }
                    } else {
                        itemsIndexed(visibleOrders) { index, order ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White)
                            ) {
                                // 第一行：单号 + 状态 + 操作按钮
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = orderNo(order),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDark,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        statusText(order)?.let { status ->
                                            Text(
                                                text = status,
                                                fontSize = 13.sp,
                                                color = ThemeBlue,
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                        }
                                        if (actionText != null) {
                                            Button(
                                                onClick = { onActionClick(order) },
                                                modifier = Modifier
                                                    .height(30.dp)
                                                    .wrapContentWidth(),
                                                shape = RoundedCornerShape(4.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = ThemeBlue)
                                            ) {
                                                Text(
                                                    text = actionText,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                }

                                HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F0))

                                // 字段明细
                                Column(
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 12.dp
                                    ),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    fields(order).forEach { field ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = field.label,
                                                fontSize = 13.sp,
                                                color = TextGray,
                                                maxLines = 1,
                                                modifier = Modifier.alignByBaseline()
                                            )
                                            Text(
                                                text = field.value,
                                                fontSize = 13.sp,
                                                color = TextDark,
                                                maxLines = 1,
                                                modifier = Modifier
                                                    .padding(start = 16.dp)
                                                    .alignByBaseline()
                                            )
                                        }
                                    }
                                }

                                // 卡片之间的灰色间隔
                                if (index != visibleOrders.lastIndex) {
                                    HorizontalDivider(thickness = 8.dp, color = PageBg)
                                }
                            }
                        }
                    }
                }
            }

            // === 3. 回到顶部悬浮按钮 ===
            AnimatedVisibility(
                visible = showScrollToTopButton,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 32.dp)
            ) {
                SmallFloatingActionButton(
                    onClick = {
                        coroutineScope.launch { listState.animateScrollToItem(0) }
                    },
                    containerColor = ThemeBlue,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "回到顶部"
                    )
                }
            }
        }
    }
}
