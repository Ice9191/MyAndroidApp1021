package com.hxb.myandroidapp_1021.ui.putaway

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hxb.myandroidapp_1021.components.ConfirmDialog
import com.hxb.myandroidapp_1021.components.InputFieldOne
import com.hxb.myandroidapp_1021.components.ManualScanCard
import com.hxb.myandroidapp_1021.components.OrderList
import com.hxb.myandroidapp_1021.components.ScanTabs
import com.hxb.myandroidapp_1021.components.ScanTopInfo
import com.hxb.myandroidapp_1021.components.ToolsInfoList
import com.hxb.myandroidapp_1021.ui.common.DisplayField
import com.hxb.myandroidapp_1021.ui.common.ToolInfoRow
import kotlinx.coroutines.launch

@Composable
fun PutawayConfirmScreen(
    flowViewModel: PutawayListViewModel,
    viewModel: PutawayConfirmViewModel = viewModel(),
    onBackClick: () -> Unit = {}
) {
    val listState by flowViewModel.uiState.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    PutawayConfirmContent(
        order = listState.selectedOrder,
        uiState = uiState,
        onRfidChange = viewModel::onRfidChange,
        onLocationChange = viewModel::onLocationChange,
        onIdentifyTool = { viewModel.identifyTool(listState.selectedOrder) },
        onBindLocation = { viewModel.bindLocation(listState.selectedOrder) },
        onTabSelected = viewModel::onTabSelected,
        onSelectTool = { rfid ->
            viewModel.onRfidChange(rfid)
            if (rfid.isNotBlank()) viewModel.identifyTool(listState.selectedOrder)
        },
        onConfirmPutaway = {
            viewModel.confirmPutaway(
                listState.selectedOrder,
                flowViewModel::confirmItem
            )
        },
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PutawayConfirmContent(
    order: PutawayOrder?,
    uiState: PutawayConfirmUiState,
    onRfidChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onIdentifyTool: () -> Unit,
    onBindLocation: () -> Unit,
    onTabSelected: (Int) -> Unit,
    onSelectTool: (String) -> Unit,
    onConfirmPutaway: () -> Unit,
    onBackClick: () -> Unit
) {
    val themeBlue = Color(0xFF1E71C8)
    val background = Color(0xFFF5F6F8)
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var showConfirmDialog by remember { mutableStateOf(false) }
    val current = order?.details?.firstOrNull { it.lineNo == uiState.selectedLineNo }
    val details = order?.details.orEmpty()
    val visibleDetails = when (uiState.selectedTab) {
        1 -> details.filterNot { it.isCompleted }
        2 -> details.filter { it.isCompleted }
        else -> details
    }
    val canSubmit = !uiState.isLoading && !uiState.isSubmitting &&
            current != null && !current.isCompleted && uiState.boundLocation.isNotBlank()
    val notice = uiState.errorMessage ?: uiState.message

    Scaffold(
        modifier = Modifier.imePadding(),
        containerColor = background,
        topBar = {
            TopAppBar(
                modifier = Modifier.height(75.dp),
                title = { Text("上架作业", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(shape = RoundedCornerShape(4.dp), onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = themeBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                if (notice != null) {
                    Text(
                        notice,
                        color = if (uiState.errorMessage != null) Color(0xFFB3261E) else Color(
                            0xFF16763C
                        ),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Button(
                    shape = RoundedCornerShape(4.dp),
                    onClick = { focusManager.clearFocus(); showConfirmDialog = true },
                    enabled = canSubmit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = themeBlue)
                ) {
                    Text(
                        if (uiState.isSubmitting) "正在提交" else "确认本件上架",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading || order == null) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isLoading) CircularProgressIndicator(color = themeBlue) else Text("请返回列表选择上架单")
            }
        } else {
            OrderList(
                orders = visibleDetails,
                listState = listState,
                emptyText = "暂无对应的上架明细",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                key = { it.lineNo },
                header = {
                    Column(Modifier
                        .fillMaxWidth()
                        .background(Color.White)) {
                        ScanTopInfo(
                            fields = listOf(
                                DisplayField("上架单号", order.orderNo),
                                DisplayField("入库单号", order.inboundOrderNo),
                                DisplayField("单据状态", order.status),
                                DisplayField("当前操作人", uiState.operator)
                            ),
                            modifier = Modifier.padding(top = 12.dp)
                        )
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "上架进度 ${order.completedCount}/${details.size}",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                            LinearProgressIndicator(
                                progress = { if (details.isEmpty()) 0f else order.completedCount.toFloat() / details.size },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(4.dp),
                                color = themeBlue,
                                trackColor = background,
                                gapSize = 0.dp,
                                drawStopIndicator = {}
                            )
                        }
                        if (order.isCompleted) {
                            Text(
                                "本单已全部上架，工具库存已更新",
                                color = Color(0xFF16763C),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        Text(
                            "1. 识别工具",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        InputFieldOne(
                            value = uiState.rfid,
                            onValueChange = onRfidChange,
                            placeholder = "扫描或输入 RFID",
                            showSearchIcon = false,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            enabled = !uiState.isSubmitting
                        )
                        Button(
                            shape = RoundedCornerShape(4.dp),
                            onClick = { focusManager.clearFocus(); onIdentifyTool() },
                            enabled = uiState.rfid.isNotBlank() && !uiState.isSubmitting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = themeBlue)
                        ) { Text("识别 RFID") }
                        if (current != null) {
                            ToolsInfoList(
                                rows = listOf(
                                    ToolInfoRow(
                                        current.rfid,
                                        current.description
                                    )
                                ), title = "当前工具"
                            )
                            ScanTopInfo(
                                fields = listOf(
                                    DisplayField("物资编码", current.itemCode),
                                    DisplayField("批次号", current.batchNo),
                                    DisplayField("仓库", current.warehouse)
                                )
                            )
                        }
                        Text(
                            "2. 绑定实际货位",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                        )
                        InputFieldOne(
                            value = uiState.locationCode,
                            onValueChange = onLocationChange,
                            placeholder = "扫描或输入货位码",
                            showSearchIcon = false,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            enabled = !uiState.isSubmitting
                        )
                        Button(
                            shape = RoundedCornerShape(4.dp),
                            onClick = { focusManager.clearFocus(); onBindLocation() },
                            enabled = current != null && uiState.locationCode.isNotBlank() && !uiState.isSubmitting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = themeBlue)
                        ) { Text("绑定货位") }
                        ScanTabs(
                            labels = listOf(
                                "全部 ${details.size}",
                                "待上架 ${details.size - order.completedCount}",
                                "已上架 ${order.completedCount}"
                            ),
                            selectedIndex = uiState.selectedTab,
                            onTabSelected = onTabSelected,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }
            ) { detail ->
                ManualScanCard(
                    title = detail.description,
                    lineLabel = "行号 ${detail.lineNo}",
                    statusText = detail.status,
                    isRead = detail.isCompleted,
                    selected = current?.lineNo == detail.lineNo,
                    onSelectedChange = { selected ->
                        if (!uiState.isSubmitting) {
                            focusManager.clearFocus()
                            onSelectTool(if (selected) detail.rfid else "")
                            if (selected) coroutineScope.launch { listState.animateScrollToItem(0) }
                        }
                    },
                    fields = listOf(
                        DisplayField("RFID", detail.rfid),
                        DisplayField("物资编码", detail.itemCode),
                        DisplayField("仓库", detail.warehouse),
                        DisplayField("实际货位", detail.location.ifBlank { "-" }),
                        DisplayField("库存状态", detail.inventoryStatus)
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                )
            }
        }
    }
    if (showConfirmDialog && current != null) {
        ConfirmDialog(
            title = "确认本件上架",
            onConfirm = { showConfirmDialog = false; onConfirmPutaway() },
            onDismiss = { showConfirmDialog = false }
        ) {
            Text("工具：${current.description}\nRFID：${current.rfid}\n仓库：${current.warehouse}\n实际货位：${uiState.boundLocation}\n上架人：${uiState.operator}")
        }
    }
}
