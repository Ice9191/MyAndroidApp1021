package com.hxb.myandroidapp_1021.pages.stockdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hxb.myandroidapp_1021.components.BottomButton
import com.hxb.myandroidapp_1021.components.ConfirmDialog
import com.hxb.myandroidapp_1021.components.InputFieldOne
import com.hxb.myandroidapp_1021.components.OrderList
import com.hxb.myandroidapp_1021.components.TopAppBar
import com.hxb.myandroidapp_1021.pages.commonmodel.DisplayField

@Composable
fun StockDetailListScreen(
    viewModel: StockDetailListViewModel = viewModel(),
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    StockDetailListContent(
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onSelectRecord = viewModel::selectRecord,
        onChangeCondition = viewModel::openConditionChange,
        onShowFilters = viewModel::showFilters,
        onBackClick = onBackClick
    )
    uiState.selectedRecord?.let { record ->
        StockDetailBottomDrawer(
            title = record.description,
            status = record.inventoryStatus,
            fields = listOf(
                DisplayField("RFID编码", record.rfid),
                DisplayField("物资编码", record.itemCode),
                DisplayField("物资描述", record.description),
                DisplayField("规格型号", record.specification ?: "未提供"),
                DisplayField("单位", record.unit),
                DisplayField("物资分类", record.category ?: "未提供"),
                DisplayField("库存状态", record.inventoryStatus),
                DisplayField("好坏件状态", record.condition),
                DisplayField("当前故障描述", record.faultDescription ?: "—"),
                DisplayField("仓库", record.warehouse),
                DisplayField("货位", record.location ?: "未提供"),
                DisplayField(
                    "是否送检", when (record.requiresInspection) {
                        true -> "是"; false -> "否"; null -> "未提供"
                    }
                ),
                DisplayField("送检周期", record.inspectionCycle ?: "—")
            ),
            onDismiss = viewModel::dismissDetail
        )
    }
    if (uiState.showFilters) {
        StockDetailFilter(
            filter = uiState.draftFilter,
            error = uiState.filterError,
            onChange = viewModel::onFilterChange,
            onReset = viewModel::resetFilters,
            onConfirm = viewModel::applyFilters,
            onDismiss = viewModel::dismissFilters
        )
    }
    uiState.conditionRecord?.let { record ->
        ConfirmDialog(
            title = "变更好坏件",
            onConfirm = viewModel::confirmConditionChange,
            onDismiss = viewModel::dismissConditionChange
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(record.description)
                Text(
                    record.rfid,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                listOf("好件", "坏件").forEach { condition ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.onConditionChange(condition) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            modifier = Modifier
                                .size(18.dp)
                                .scale(0.8f),
                            checked = uiState.pendingCondition == condition,
                            onCheckedChange = null
                        )
                        Text(condition)
                    }
                }
            }
        }
    }
}

@Composable
fun StockDetailListContent(
    uiState: StockDetailListUiState,
    onSearchQueryChange: (String) -> Unit,
    onSelectRecord: (String) -> Unit,
    onChangeCondition: (String) -> Unit,
    onShowFilters: () -> Unit,
    onBackClick: () -> Unit
) {
    val listState = rememberLazyListState()
    Scaffold(
        modifier = Modifier.imePadding(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { TopAppBar(title = "库存明细", onBackClick = onBackClick) }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            InputFieldOne(uiState.searchQuery, onSearchQueryChange, "物资编码 / 名称 / RFID")
            OrderList(
                orders = uiState.filteredRecords,
                listState = listState,
                emptyText = if (uiState.isLoading) "正在加载库存明细" else "暂无匹配的库存明细",
                modifier = Modifier.weight(1f),
                key = { it.rfid },
                header = {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = onShowFilters,
                            shape = RoundedCornerShape(4.dp)
                        ) { Text("筛选") }
                        Text(
                            "已显示 ${uiState.filteredRecords.size} 条",
                            Modifier.weight(1f),
                            textAlign = TextAlign.End,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    uiState.message?.let {
                        Text(
                            it,
                            Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            ) { record ->
                StockDetailCard(
                    record,
                    { onSelectRecord(record.rfid) },
                    { onChangeCondition(record.rfid) })
            }
        }
    }
}

/**
 * 点击整条记录查看详情，按钮单独消费点击事件，只打开好坏件变更弹窗。
 */
@Composable
private fun StockDetailCard(
    record: StockDetail,
    onClick: () -> Unit,
    onChangeCondition: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 1.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                record.description,
                Modifier.weight(1f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    record.inventoryStatus,
                    Modifier.padding(end = 8.dp),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Button(
                    onClick = onChangeCondition,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .height(30.dp)
                        .wrapContentWidth(),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("变更好坏件", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Column(
            Modifier.padding(horizontal = 16.dp, vertical = 1.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            listOf(
                DisplayField("RFID编码", record.rfid),
                DisplayField("物资编码", record.itemCode),
                DisplayField("规格型号", record.specification ?: "未提供"),
                DisplayField("单位", record.unit),
                DisplayField("仓库", record.warehouse),
                DisplayField("货位", record.location ?: "未提供"),
                DisplayField("好坏件状态", record.condition)
            ).forEach { field ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        field.label,
                        Modifier.alignByBaseline(),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        field.value,
                        Modifier
                            .weight(1f)
                            .alignByBaseline(),
                        fontSize = 13.sp,
                        textAlign = TextAlign.End,
                        color = if (field.label == "好坏件状态" && record.condition == "坏件") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        HorizontalDivider(thickness = 8.dp, color = MaterialTheme.colorScheme.background)
    }
}


/**
 * 单件库存详情抽屉，只展示页面传入的数据；遮罩、返回键及关闭按钮上报同一关闭事件。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StockDetailBottomDrawer(
    title: String,
    status: String,
    fields: List<DisplayField>,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        dragHandle = null
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "库存详情",
                    Modifier.weight(1f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss, shape = RoundedCornerShape(4.dp)) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close inventory details",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    Modifier
                        .padding(vertical = 12.dp)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background, RoundedCornerShape(4.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(title, Modifier.weight(1f), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(status, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                }
                fields.forEach { field ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            field.label,
                            Modifier
                                .weight(0.36f)
                                .alignByBaseline(),
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            field.value,
                            Modifier
                                .weight(0.64f)
                                .alignByBaseline(),
                            fontSize = 13.sp,
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }
            }
            BottomButton(text = "关闭", onClick = onDismiss)
        }
    }
}


/**
 * 筛选草稿由页面状态提供；选项自动换行，内容可滚动，底部操作保持可见。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StockDetailFilter(
    filter: StockDetailFilterState,
    error: String?,
    onChange: (StockDetailFilterState) -> Unit,
    onReset: () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        dragHandle = null
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .imePadding()
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("筛选", Modifier.weight(1f), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = onDismiss, shape = RoundedCornerShape(4.dp)) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close filter",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("库存状态", fontSize = 13.sp)
                // TODO: 字段未确定：选项文案依据页面需求，接口状态编码及完整枚举待确认。
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        "",
                        "在库",
                        "待拣选",
                        "已备货",
                        "已借出",
                        "待上架",
                        "已送检",
                        "已送修"
                    ).forEach { status ->
                        StockDetailTabButton(
                            text = status.ifBlank { "全部" },
                            selected = filter.status == status,
                            onClick = { onChange(filter.copy(status = status)) }
                        )
                    }
                }
                Text("好坏件状态", fontSize = 13.sp)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("", "好件", "坏件").forEach { condition ->
                        StockDetailTabButton(
                            text = condition.ifBlank { "全部" },
                            selected = filter.condition == condition,
                            onClick = { onChange(filter.copy(condition = condition)) }
                        )
                    }
                }
                Text("仓库", fontSize = 13.sp)
                InputFieldOne(
                    value = filter.warehouse,
                    onValueChange = { onChange(filter.copy(warehouse = it)) },
                    placeholder = "请输入仓库名称",
                    modifier = Modifier.fillMaxWidth(),
                    showSearchIcon = false,
                    horizontalPadding = 0.dp
                )
                Text("货位", fontSize = 13.sp)
                InputFieldOne(
                    value = filter.location,
                    onValueChange = { onChange(filter.copy(location = it)) },
                    placeholder = "请输入货位编码，如 A-03-02-01",
                    modifier = Modifier.fillMaxWidth(),
                    showSearchIcon = false,
                    horizontalPadding = 0.dp
                )
                Text("下次送检日期", fontSize = 13.sp)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InputFieldOne(
                        value = filter.startDate,
                        onValueChange = { onChange(filter.copy(startDate = it)) },
                        placeholder = "开始日期",
                        modifier = Modifier.weight(1f),
                        showSearchIcon = false,
                        horizontalPadding = 0.dp
                    )
                    Text("至", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                    InputFieldOne(
                        value = filter.endDate,
                        onValueChange = { onChange(filter.copy(endDate = it)) },
                        placeholder = "结束日期",
                        modifier = Modifier.weight(1f),
                        showSearchIcon = false,
                        horizontalPadding = 0.dp
                    )
                }
                error?.let { Text(it, fontSize = 13.sp, color = MaterialTheme.colorScheme.error) }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StockDetailTabButton(
                    text = "重置",
                    onClick = onReset,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                )
                StockDetailTabButton(
                    text = "确定",
                    onClick = onConfirm,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    prominent = true
                )
            }
        }
    }
}

@Composable
private fun StockDetailTabButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    prominent: Boolean = false
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
        colors = ButtonDefaults.textButtonColors(
            containerColor = when {
                prominent -> MaterialTheme.colorScheme.primary
                selected -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.background
            },
            contentColor = when {
                prominent -> MaterialTheme.colorScheme.onPrimary
                selected -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    ) { Text(text, fontSize = 13.sp) }
}


