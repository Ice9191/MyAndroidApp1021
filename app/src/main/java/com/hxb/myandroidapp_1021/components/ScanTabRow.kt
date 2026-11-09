package com.hxb.myandroidapp_1021.components

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * 扫码页最下面的三个 Tab（全部 / 已读取 / 未读取）：
 * 白底、主题色下划线指示器、选中项加粗变蓝，其余灰色。
 * Tab 数量由 [labels] 决定（传几个就画几个）。
 *
 * 用法：
 * ```
 * ScanTabRow(
 *     labels = listOf("全部 ${allTools.size}", "已读取 ${readList.size}", "未读取 ${unreadList.size}"),
 *     selectedIndex = selectedTabIndex,
 *     onSelect = { selectedTabIndex = it }
 * )
 * ```
 *
 * @param labels 每个 Tab 的文字
 * @param selectedIndex 当前选中下标（从 0 开始）
 * @param onSelect 点击 Tab 回调，参数是下标
 */
@Composable
fun ScanTabRow(
    labels: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val themeBlue = Color(0xFF1E71C8)
    val textGray = Color(0xFF888888)
    SecondaryTabRow(
        selectedTabIndex = selectedIndex,
        modifier = modifier,
        containerColor = Color.White,
        contentColor = themeBlue,
        indicator = {
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(selectedIndex),
                color = themeBlue
            )
        },
        divider = { HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F0)) }
    ) {
        labels.forEachIndexed { index, label ->
            Tab(
                selected = selectedIndex == index,
                onClick = { onSelect(index) },
                text = {
                    Text(
                        text = label,
                        color = if (selectedIndex == index) themeBlue else textGray,
                        fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }
    }
}
