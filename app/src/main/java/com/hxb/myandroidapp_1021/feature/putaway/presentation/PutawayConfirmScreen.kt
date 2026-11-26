package com.hxb.myandroidapp_1021.feature.putaway.presentation

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hxb.myandroidapp_1021.core.ui.common.DisplayField
import com.hxb.myandroidapp_1021.core.ui.components.FormFilter
import com.hxb.myandroidapp_1021.core.ui.components.InputFieldOne
import com.hxb.myandroidapp_1021.core.ui.components.ToastNotification
import com.hxb.myandroidapp_1021.core.ui.components.TopAppBar

@Composable
fun PutawayConfirmScreen(
    flowViewModel: PutawayListViewModel,
    viewModel: PutawayConfirmViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    val listState by flowViewModel.uiState.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(listState.selectedOrder) {
        viewModel.bindOrder(listState.selectedOrder)
    }
    PutawayConfirmContent(
        isLoading = listState.selectedOrder != null && !uiState.hasOrder,
        uiState = uiState,
        onScanQueryChange = viewModel::onScanQueryChange,
        onScanClick = viewModel::onScanClick,
        onSelectDetail = viewModel::selectDetail,
        onClearSelectedDetail = viewModel::clearSelectedDetail,
        onNotificationDismiss = viewModel::onNotificationDismiss,
        onBackClick = onBackClick,
        onHomeClick = onHomeClick
    )
}

@Composable
fun PutawayConfirmContent(
    isLoading: Boolean,
    uiState: PutawayConfirmUiState,
    onScanQueryChange: (String) -> Unit,
    onScanClick: () -> Unit,
    onSelectDetail: (String) -> Unit,
    onClearSelectedDetail: () -> Unit,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit = {},
    onNotificationDismiss: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = Modifier.imePadding(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = "工器具上架",
                onBackClick = onBackClick,
                onHomeClick = onHomeClick
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                InputFieldOne(
                    value = uiState.scanQuery,
                    onValueChange = onScanQueryChange,
                    placeholder = "扫描目标货位 / 物资RFID标签",
                    trailingIcon = {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .clickable(role = Role.Button, onClick = onScanClick),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.QrCodeScanner,
                                contentDescription = "Scan",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                )
                when {
                    isLoading -> Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }

                    !uiState.hasOrder -> Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("请返回列表选择上架单")
                    }

                    uiState.visibleDetails.isEmpty() -> Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("暂无匹配明细", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.visibleDetails, key = { it.detail.lineNo }) { row ->
                            PutawayDetailCard(
                                row = row,
                                onClick = {
                                    focusManager.clearFocus()
                                    onSelectDetail(row.detail.lineNo)
                                }
                            )
                        }
                    }
                }
            }
            ToastNotification(
                message = uiState.notificationMessage,
                notificationId = uiState.notificationId,
                onDismiss = onNotificationDismiss,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }

    val selectedDetail = uiState.selectedDetail
    if (selectedDetail != null) {
        val detail = selectedDetail.detail
        FormFilter(
            error = null,
            onConfirm = onClearSelectedDetail,
            onDismiss = onClearSelectedDetail,
            title = "明细详情",
            confirmText = "关闭",
            contentPadding = PaddingValues(
                start = 16.dp,
                top = 8.dp,
                end = 16.dp,
                bottom = 16.dp
            ),
            content = {
                DetailFields(
                    fields = listOf(
                        DisplayField("上架单号", detail.orderNo),
                        DisplayField("入库行号", detail.inboundLineNo),
                        DisplayField("上架行号", detail.lineNo),
                        DisplayField("物资描述", detail.description),
                        DisplayField("物资编码", detail.itemCode),
                        DisplayField("RFID", detail.rfid),
                        DisplayField("明细状态", detail.status.label),
                        DisplayField("批次号", detail.batchNo),
                        DisplayField("单位", detail.unit),
                        DisplayField("仓库", detail.warehouse),
                        DisplayField("数量", selectedDetail.quantity)
                    )
                )
            }
        )
    }
}

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
private fun PutawayDetailCard(
    row: PutawayDetailUi,
    onClick: () -> Unit
) {
    val detail = row.detail
    val themeBlue = MaterialTheme.colorScheme.primary
    val borderColor by animateColorAsState(
        targetValue = if (row.isScanned) themeBlue else MaterialTheme.colorScheme.outlineVariant,
        label = "putawayCardBorder"
    )
    val lift by animateDpAsState(
        targetValue = if (row.isScanned) 2.dp else 0.dp,
        label = "putawayCardLift"
    )
    val elevation by animateDpAsState(
        targetValue = if (row.isScanned) 4.dp else 0.dp,
        label = "putawayCardElevation"
    )
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = -lift),
        shape = RoundedCornerShape(0.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(if (row.isScanned) 2.dp else 1.dp, borderColor),
        shadowElevation = elevation
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = detail.description,
                    modifier = Modifier.weight(1f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Button(
                    onClick = onClick,
                    modifier = Modifier
                        .height(28.dp)
                        .wrapContentWidth(),
                    shape = RoundedCornerShape(4.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = themeBlue)
                ) {
                    Text(
                        text = "查看详情",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            DetailLine("RFID", detail.rfid)
            DetailLine("物资编码", detail.itemCode)
            DetailLine("明细状态", detail.status.label)
            DetailLine("仓库", detail.warehouse)
            DetailLine("数量", row.quantity)
        }
    }
}

@Composable
private fun DetailFields(fields: List<DisplayField>) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        fields.forEach { field ->
            DetailLine(field.label, field.value)
        }
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f),
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
