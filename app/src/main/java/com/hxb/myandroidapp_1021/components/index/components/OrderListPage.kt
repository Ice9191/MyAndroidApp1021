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
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
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
import com.hxb.myandroidapp_1021.components.index.models.OrderCardSpec
import com.hxb.myandroidapp_1021.components.index.models.OrderListTexts
import kotlinx.coroutines.launch

private val ThemeBlue = Color(0xFF1E71C8)
private val PageBg = Color(0xFFF5F6F8)
private val TextDark = Color(0xFF222222)
private val TextGray = Color(0xFF888888)

/**
 * 单据列表页骨架（借出 / 归还 / 送检 / 送修 / 报废 / 报废出库 / 盘点共用）：
 * 蓝色标题栏 + 搜索框 + 列表头 + 单据卡片列表 + 右下角「回到顶部 / 新增」按钮。
 *
 * 页面本身只负责这层骨架，卡片内容与文案分别由 [card]、[texts] 两个对象描述。
 *
 * 用法（借出为例）：
 * ```
 * OrderListPage(
 *     title = "借出",
 *     orders = orderList,
 *     card = OrderCardSpec(
 *         orderNo = { it.orderNo },
 *         fields = { order -> listOf(DisplayField("班组", order.team), ...) },
 *         statusText = { it.status },
 *         actionText = "开始拣选",
 *         onActionClick = { order -> onStartPickClick(order) },
 *         searchMatcher = { order, q -> order.orderNo.contains(q, ignoreCase = true) }
 *     ),
 *     texts = OrderListTexts(
 *         searchPlaceholder = "请输入拣选单号",
 *         headerLabel = "待处理拣选单"
 *     ),
 *     onBackClick = onBackClick
 * )
 * ```
 *
 * @param title 标题栏文字
 * @param orders 已由调用方做完业务过滤的列表
 * @param card 卡片取数规则（单号 / 明细 / 状态 / 按钮 / 搜索匹配）
 * @param texts 搜索提示、列表头文字、空数据文案
 * @param onBackClick 返回
 * @param header 自定义列表头（如下拉筛选），传了它就不显示 [texts] 里的 headerLabel
 * @param onAddClick 传了才显示右下角「+」按钮
 */
@Composable
fun <T> OrderListPage(
    title: String,
    orders: List<T>,
    card: OrderCardSpec<T>,
    texts: OrderListTexts,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    header: (@Composable () -> Unit)? = null,
    onAddClick: (() -> Unit)? = null
) {
    val focusManager = LocalFocusManager.current
    val searchState = rememberTextFieldState()
    val query = searchState.text.toString().trim()
    val searchMatcher = card.searchMatcher
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
    val clearFocus = { focusManager.clearFocus() }

    Scaffold(
        modifier = modifier.imePadding(),
        containerColor = PageBg,
        topBar = { OrderListTopBar(title = title, onBackClick = onBackClick, onTap = clearFocus) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pointerInput(Unit) { detectTapGestures(onTap = { clearFocus() }) }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // === 1. 顶部搜索框 ===
                SearchField(state = searchState, placeholder = texts.searchPlaceholder)

                // === 2. 列表 ===
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState
                ) {
                    item {
                        OrderListHeaderRow(
                            texts = texts,
                            count = visibleOrders.size,
                            header = header
                        )
                    }

                    if (visibleOrders.isEmpty()) {
                        item {
                            OrderListEmptyRow(
                                text = if (query.isEmpty()) texts.emptyText else texts.noResultText
                            )
                        }
                    } else {
                        items(visibleOrders) { order ->
                            OrderCard(order = order, card = card)
                        }
                    }
                }
            }

            // === 3. 右下角悬浮按钮：回到顶部 / 新增(+) ===
            OrderListFloatingButtons(
                showScrollToTop = showScrollToTopButton,
                onScrollToTop = { coroutineScope.launch { listState.animateScrollToItem(0) } },
                onAddClick = onAddClick
            )
        }
    }
}

/**
 * 顶部蓝色标题栏：点空白处清掉搜索框焦点，返回前也清一次
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderListTopBar(title: String, onBackClick: () -> Unit, onTap: () -> Unit) {
    TopAppBar(
        modifier = Modifier
            .height(75.dp)
            .pointerInput(Unit) { detectTapGestures(onTap = { onTap() }) },
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
                    onTap()
                    onBackClick()
                },
                modifier = Modifier.offset(x = (-7).dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Back"
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

/**
 * 列表头：左侧说明 / 自定义控件，右侧总数
 */
@Composable
private fun OrderListHeaderRow(
    texts: OrderListTexts,
    count: Int,
    header: (@Composable () -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PageBg)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (header != null) {
            header()
        } else {
            Text(
                text = texts.headerLabel.orEmpty(),
                fontSize = 14.sp,
                color = TextGray
            )
        }
        Text(
            text = "共${count}张",
            fontSize = 14.sp,
            color = TextGray
        )
    }
}

/**
 * 空列表 / 搜不到时的占位行
 */
@Composable
private fun OrderListEmptyRow(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        color = TextGray,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    )
}

/**
 * 一张单据卡片：单号+状态+按钮 -> 明细字段 -> 8dp 灰条
 */
@Composable
private fun <T> OrderCard(order: T, card: OrderCardSpec<T>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        OrderCardHeader(order = order, card = card)

        HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F0))

        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            card.fields(order).forEach { field -> OrderFieldRow(field = field) }
        }

        HorizontalDivider(thickness = 8.dp, color = PageBg)
    }
}

/**
 * 卡片第一行：左边单号，右边状态 + 操作按钮
 */
@Composable
private fun <T> OrderCardHeader(order: T, card: OrderCardSpec<T>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = card.orderNo(order),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            card.statusText(order)?.let { status ->
                Text(
                    text = status,
                    fontSize = 13.sp,
                    color = card.statusColor(order),
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            card.actionText?.let { label ->
                Button(
                    onClick = { card.onActionClick(order) },
                    modifier = Modifier
                        .height(30.dp)
                        .wrapContentWidth(),
                    shape = RoundedCornerShape(4.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ThemeBlue)
                ) {
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * 卡片里的一行明细：左标签右值，按基线对齐
 */
@Composable
private fun OrderFieldRow(field: DisplayField) {
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

/**
 * 右下角悬浮按钮组：回到顶部（滑过 2 条才出现）+ 新增（传了才显示）
 */
@Composable
private fun BoxScope.OrderListFloatingButtons(
    showScrollToTop: Boolean,
    onScrollToTop: () -> Unit,
    onAddClick: (() -> Unit)?
) {
    Column(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(end = 16.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AnimatedVisibility(
            visible = showScrollToTop,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
        ) {
            SmallFloatingActionButton(
                onClick = onScrollToTop,
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

        if (onAddClick != null) {
            SmallFloatingActionButton(
                onClick = onAddClick,
                containerColor = ThemeBlue,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "新增"
                )
            }
        }
    }
}