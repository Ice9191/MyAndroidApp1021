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
 * 标签、当前选中项由页面传入，点击只上报索引。
 */
@Composable
fun ScanTabs(
    labels: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val themeBlue = Color(0xFF1E71C8)
    val textGray = Color(0xFF888888)
    SecondaryTabRow(
        modifier = modifier,
        selectedTabIndex = selectedIndex,
        containerColor = Color.White,
        contentColor = themeBlue,
        indicator = {
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(selectedIndex),
                color = themeBlue
            )
        },
        divider = {
            HorizontalDivider(
                thickness = 1.dp,
                color = Color(0xFFF0F0F0)
            )
        }
    ) {
        labels.forEachIndexed { index, label ->
            Tab(
                selected = selectedIndex == index,
                onClick = { onTabSelected(index) },
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
