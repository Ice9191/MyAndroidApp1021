package com.hxb.myandroidapp_1021.ui.overduealert

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hxb.myandroidapp_1021.components.InputFieldOne
import com.hxb.myandroidapp_1021.components.OrderList
import com.hxb.myandroidapp_1021.components.ScrollToTopButton
import com.hxb.myandroidapp_1021.components.TopAppBar
import kotlinx.coroutines.launch

@Composable
fun OverdueAlertListScreen(
    viewModel: OverdueAlertListViewModel = viewModel(),
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    OverdueAlertListContent(
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onToggleExpanded = viewModel::toggleExpanded,
        onBackClick = onBackClick
    )
}

@Composable
fun OverdueAlertListContent(
    uiState: OverdueAlertListUiState,
    onSearchQueryChange: (String) -> Unit,
    onToggleExpanded: (String) -> Unit,
    onBackClick: () -> Unit = {}
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val showScrollToTopButton by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 2 }
    }
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = Modifier.imePadding(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = "超期告警",
                onBackClick = { focusManager.clearFocus(); onBackClick() }
            )
        }
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
        ) {
            Column(Modifier.fillMaxSize()) {
                InputFieldOne(
                    value = uiState.searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = "借用人 / 单号 / RFID"
                )
                if (uiState.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                OrderList(
                    orders = uiState.filteredAlerts,
                    listState = listState,
                    emptyText = when {
                        uiState.isLoading -> "正在加载告警记录"
                        uiState.searchQuery.isBlank() -> "暂无告警记录"
                        else -> "暂无匹配的告警记录"
                    },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 88.dp),
                    key = { it.alertId },
                    header = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "告警记录",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                if (uiState.isLoading) "加载中" else "已显示 ${uiState.filteredAlerts.size} 条",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                ) { alert ->
                    OverdueAlertCard(
                        alert = alert,
                        expanded = alert.alertId in uiState.expandedAlertIds,
                        onToggleExpanded = { onToggleExpanded(alert.alertId) }
                    )
                }
            }
            ScrollToTopButton(
                visible = showScrollToTopButton,
                onClick = { coroutineScope.launch { listState.animateScrollToItem(0) } },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 32.dp)
            )
        }
    }
}

@Composable
private fun OverdueAlertCard(alert: OverdueAlert, expanded: Boolean, onToggleExpanded: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 1.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                alert.alertId.ifBlank { "未提供" },
                modifier = Modifier.weight(1f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    alert.status?.takeIf { it.isNotBlank() } ?: "未提供",
                    modifier = Modifier
                        .widthIn(max = 96.dp)
                        .padding(end = 8.dp),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Button(
                    onClick = onToggleExpanded,
                    modifier = Modifier
                        .height(30.dp)
                        .wrapContentWidth(),
                    shape = RoundedCornerShape(4.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        if (expanded) "收起" else "展开",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 1.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            OverdueAlertField("器具名称", alert.toolName)
            OverdueAlertField("借用人", alert.borrower)
            OverdueAlertField("RFID 编码", alert.rfid)
            OverdueAlertField("应还时间", alert.dueAt)
            OverdueAlertField(
                "超期天数",
                alert.overdueDays?.let { "$it 天" },
                color = MaterialTheme.colorScheme.error
            )
        }
        if (expanded) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 1.dp),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                OverdueAlertField("告警类型", alert.alertType)
                OverdueAlertField("工单号", alert.workOrderNo)
                OverdueAlertField("借用单号", alert.borrowOrderNo)
                OverdueAlertField("物资编码", alert.itemCode)
                OverdueAlertField("借出时间", alert.borrowedAt)
                OverdueAlertField("处理状态", alert.handlingStatus)
            }
        }
    }
}

/**
 * 按可用宽度分配标签和值的空间，长文本自动换行，不按模拟数据限制行数或条目高度。
 */
@Composable
private fun OverdueAlertField(
    label: String,
    value: String?,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            label,
            modifier = Modifier.alignByBaseline(),
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value?.takeIf { it.isNotBlank() } ?: "未提供",
            modifier = Modifier
                .weight(1f)
                .alignByBaseline(),
            fontSize = 13.sp,
            textAlign = TextAlign.End,
            color = color
        )
    }
}

