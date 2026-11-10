package com.hxb.myandroidapp_1021.ui.picking

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hxb.myandroidapp_1021.data.models.DisplayField
import kotlinx.coroutines.launch

@Composable
fun PickingListScreen(
    viewModel: PickingListViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onStartPickClick: (PickingOrder) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PickingListContent(
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onBackClick = onBackClick,
        onStartPickClick = onStartPickClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickingListContent(
    uiState: PickingListUiState,
    onSearchQueryChange: (String) -> Unit,
    onBackClick: () -> Unit = {},
    onStartPickClick: (PickingOrder) -> Unit = {}
) {
    val themeBlue = Color(0xFF1E71C8)
    val textDark = Color(0xFF222222)
    val pageBg = Color(0xFFF5F6F8)
    val textGray = Color(0xFF888888)
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val showScrollToTopButton by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 2 } // 向下滑过 2 个条目再显示回到顶部按钮
    }
    val focusManager = LocalFocusManager.current
    val clearFocus = { focusManager.clearFocus() }
    val searchInteractionSource = remember { MutableInteractionSource() }
    val searchShape = RoundedCornerShape(4.dp)
    val searchColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color(0xFFF5F6F8),
        unfocusedContainerColor = Color(0xFFF5F6F8),
        focusedBorderColor = themeBlue,
        unfocusedBorderColor = Color.Transparent
    )

    Scaffold(
        modifier = Modifier.imePadding(),
        containerColor = pageBg,
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .height(75.dp)
                    .pointerInput(Unit) { detectTapGestures(onTap = { clearFocus() }) },
                title = {
                    Text(
                        text = "借出",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.offset(x = (-16).dp)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            clearFocus()
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
                    containerColor = themeBlue,
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
                .pointerInput(Unit) { detectTapGestures(onTap = { clearFocus() }) }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // === 1. 顶部搜索框 ===
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    BasicTextField(
                        value = uiState.searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(color = Color(0xFF333333)),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        interactionSource = searchInteractionSource,
                        cursorBrush = SolidColor(themeBlue),
                        decorationBox = { innerTextField ->
                            Box {
                                OutlinedTextFieldDefaults.Container(
                                    enabled = true,
                                    isError = false,
                                    interactionSource = searchInteractionSource,
                                    modifier = Modifier.matchParentSize(),
                                    colors = searchColors,
                                    shape = searchShape
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 48.dp)
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = Color(0xFF999999),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Box(
                                        modifier = Modifier.weight(1f),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        if (uiState.searchQuery.isEmpty()) {
                                            Text(
                                                text = "请输入拣选单号",
                                                fontSize = 14.sp,
                                                color = Color(0xFF999999)
                                            )
                                        }
                                        innerTextField()
                                    }
                                }
                            }
                        }
                    )
                }

                // === 2. 列表 ===
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState
                ) {
                    if (uiState.filteredOrders.isEmpty()) {
                        item {
                            Text(
                                text = if (uiState.searchQuery.isEmpty()) "暂无数据" else "暂无匹配数据",
                                fontSize = 14.sp,
                                color = textGray,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White)
                                    .padding(16.dp)
                            )
                        }
                    } else {
                        items(uiState.filteredOrders) { order ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 1.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = order.orderNo,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textDark,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = order.status.label,
                                            fontSize = 13.sp,
                                            color = themeBlue,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )

                                        Button(
                                            onClick = { onStartPickClick(order) },
                                            modifier = Modifier
                                                .height(30.dp)
                                                .wrapContentWidth(),
                                            shape = RoundedCornerShape(4.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = themeBlue)
                                        ) {
                                            Text(
                                                text = "开始拣选",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }

                                HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F0))

                                Column(
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 1.dp
                                    ),
                                    verticalArrangement = Arrangement.spacedBy(1.dp)
                                ) {
                                    listOf(
                                        DisplayField("拣选单号", order.orderNo),
                                        DisplayField("单据状态", order.status.label),
                                        DisplayField("车间/工班", order.workshop),
                                        DisplayField("借用人", order.borrower),
                                        DisplayField("拣选进度", order.pendingCount.toString()),
                                        DisplayField("创建时间", order.creationDate)
                                    ).forEach { field ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = field.label,
                                                fontSize = 13.sp,
                                                color = textGray,
                                                maxLines = 1,
                                                modifier = Modifier.alignByBaseline()
                                            )
                                            Text(
                                                text = field.value,
                                                fontSize = 13.sp,
                                                color = textDark,
                                                maxLines = 1,
                                                modifier = Modifier
                                                    .padding(start = 16.dp)
                                                    .alignByBaseline()
                                            )
                                        }
                                    }
                                }

                                HorizontalDivider(thickness = 8.dp, color = pageBg)
                            }
                        }
                    }
                }
            }

            // === 3. 右下角悬浮按钮：回到顶部 ===
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 32.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AnimatedVisibility(
                    visible = showScrollToTopButton,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
                ) {
                    SmallFloatingActionButton(
                        onClick = { coroutineScope.launch { listState.animateScrollToItem(0) } },
                        containerColor = themeBlue,
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
}
