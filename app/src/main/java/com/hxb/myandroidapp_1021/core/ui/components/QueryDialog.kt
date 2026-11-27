package com.hxb.myandroidapp_1021.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxb.myandroidapp_1021.core.ui.common.LocalNavigationInputLocked

/**
 * 通用查询弹窗：顶部输入框按关键字过滤候选列表，列表可滚动，点击条目回传选中值。
 * 候选列表上下各有一条分隔线，紧密贴合第一条和最后一条数据，列表本身不带额外留白。
 * 四周留白与 ConfirmDialog 保持一致，由 dialogPadding 控制。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueryDialog(
    query: String,
    options: List<String>,
    onQueryChange: (String) -> Unit,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    dialogPadding: PaddingValues = PaddingValues(16.dp),
    searchPlaceholder: String = "输入关键字搜索",
    emptyText: String = "无匹配数据"
) {
    if (LocalNavigationInputLocked.current) return

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.imePadding()
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.9f),
            shape = RectangleShape,
            color = Color.White,
            tonalElevation = 0.dp
        ) {
            Column(modifier = Modifier.padding(dialogPadding)) {
                InputFieldOne(
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = searchPlaceholder,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalPadding = 0.dp
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp)
                ) {
                    if (options.isEmpty()) {
                        item {
                            Text(
                                text = emptyText,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp)
                            )
                        }
                    } else {
                        itemsIndexed(options) { index, option ->
                            Column {
                                if (index > 0) {
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                }
                                Text(
                                    text = option,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onSelect(option) }
                                        .padding(vertical = 12.dp)
                                )
                            }
                        }
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(shape = RoundedCornerShape(4.dp), onClick = onDismiss) {
                        Text("取消", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
