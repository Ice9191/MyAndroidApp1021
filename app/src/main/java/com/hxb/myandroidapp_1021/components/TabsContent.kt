package com.hxb.myandroidapp_1021.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxb.myandroidapp_1021.models.ScanColors
import com.hxb.myandroidapp_1021.models.ScanTab
import com.hxb.myandroidapp_1021.models.ScanTabsLabels
import com.hxb.myandroidapp_1021.models.ToolCheckRow
import com.hxb.myandroidapp_1021.models.ToolScanItem

/**
 * 扫码页三个 Tab 的内容容器：**纯路由 + 纯展示**。
 *  - [ScanTab.All]：全部（[AllToolsTab]）
 *  - [ScanTab.Read]：已读取（[CheckListTab]，取消读取）
 *  - [ScanTab.Unread]：未读取（[CheckListTab]，手动读取）
 *
 * 组件不知道数据是不是 SnapshotStateList、也不做写回：勾选只上抛 [onToggle]，由页面改自己的状态。
 * 文案差异用 [labels] 覆盖，颜色用 [colors] 传，组件本身只有「全部 / 已读取 / 未读取」的概念。
 *
 * @param tab 当前 Tab
 * @param allTools 全部工器具
 * @param readList 已读取列表（只读使用）
 * @param unreadList 未读取列表（只读使用）
 * @param colors 主题色 / 次文字色 / 页面底色
 * @param labels 三个 Tab 的文案（默认拣选措辞）
 * @param onToggle 勾选回调：(行 id, 是否勾选)，写回由页面负责
 * @param onActionClick 手动读取 / 取消读取按钮点击
 */
@Composable
fun TabsContent(
    tab: ScanTab,
    allTools: List<ToolScanItem>,
    readList: List<ToolScanItem>,
    unreadList: List<ToolScanItem>,
    colors: ScanColors,
    labels: ScanTabsLabels = ScanTabsLabels(),
    onToggle: (id: String, checked: Boolean) -> Unit,
    onActionClick: () -> Unit
) {
    when (tab) {
        ScanTab.All -> AllToolsTab(
            allTools = allTools,
            readList = readList,
            colors = colors,
            labels = labels
        )

        ScanTab.Read -> CheckListTab(
            sourceList = readList,
            actionLabel = labels.readAction,
            onToggle = onToggle,
            onActionClick = onActionClick
        )

        ScanTab.Unread -> CheckListTab(
            sourceList = unreadList,
            actionLabel = labels.unreadAction,
            onToggle = onToggle,
            onActionClick = onActionClick
        )
    }
}

@Composable
private fun AllToolsTab(
    allTools: List<ToolScanItem>,
    readList: List<ToolScanItem>,
    colors: ScanColors,
    labels: ScanTabsLabels
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgColor)
    ) {
        items(allTools, key = { it.id }) { tool ->
            val pickedItem = readList.firstOrNull { it.rfid == tool.rfid }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ToolField("RFID", tool.rfid)
                ToolField("名称", tool.name)
                ToolField(
                    "状态",
                    if (pickedItem != null) labels.readStatus else labels.unreadStatus,
                    valueColor = if (pickedItem != null) colors.themeBlue else colors.textGray
                )
                ToolField(labels.date, pickedItem?.pickDate?.ifBlank { "-" } ?: "-")
            }
            HorizontalDivider(thickness = 8.dp, color = colors.bgColor)
        }
    }
}

/**
 * 已读取 / 未读取：共用表格，展示 + 勾选回调
 */
@Composable
private fun CheckListTab(
    sourceList: List<ToolScanItem>,
    actionLabel: String,
    onToggle: (id: String, checked: Boolean) -> Unit,
    onActionClick: () -> Unit
) {
    ToolCheckListTab(
        rows = sourceList.map {
            ToolCheckRow(
                id = it.id,
                rfid = it.rfid,
                subtitle = it.name,
                quantity = 1,
                checked = it.isChecked
            )
        },
        selectedCount = sourceList.count { it.isChecked },
        actionLabel = actionLabel,
        actionEnabled = sourceList.any { it.isChecked },
        onActionClick = onActionClick,
        onCheckedChange = { row, checked -> onToggle(row.id, checked) }
    )
}

/**
 * 工器具卡片里的一行字段：最左端标签、最右端值，两端对齐，不带冒号。
 * 只给「全部」Tab 的卡片用，所以是 private。
 *
 * @param label 左侧标签
 * @param value 右侧值
 * @param valueColor 值的颜色（默认深色，状态这类可传主题色 / 红色）
 */
@Composable
private fun ToolField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = Color(0xFF222222)
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color(0xFF888888),
            maxLines = 1
        )
        Text(
            text = value,
            fontSize = 13.sp,
            color = valueColor,
            maxLines = 1,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}
