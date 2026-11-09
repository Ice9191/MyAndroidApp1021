package com.hxb.myandroidapp_1021.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxb.myandroidapp_1021.models.ToolInfoRow

/**
 * 确认页的工器具表格（只读）：灰底表头「工器具信息 / 数量/件」+ 每件工具两行信息（RFID、名称）+ 右侧数量。
 * 表头下、每行下各一条 1dp 分割线，表格下面不留白。
 *
 * 和 [ToolCheckListTab] 的区别：这里没有勾选框，是纯展示表格，
 * 用在「借出·出库确认 / 归还确认 / 送检确认 / 送修确认 / 报废确认 / 盘点结果」这类页面。
 *
 * 用法：
 * ```
 * ToolInfoTable(
 *     rows = toolList.map { ToolInfoRow(rfid = it.rfid, name = it.name) }
 * )
 * ```
 *
 * 右列要显示状态而不是数量时（盘点结果）：
 * ```
 * ToolInfoTable(
 *     rows = items.map {
 *         ToolInfoRow(
 *             rfid = it.tool.rfid,
 *             name = it.tool.name,
 *             value = it.result,
 *             valueColor = if (it.result == "已盘") themeBlue else Color(0xFFE53935)
 *         )
 *     },
 *     quantityHeader = "盘点结果",
 *     rowVerticalPadding = 6
 * )
 * ```
 *
 * @param rows 表格数据
 * @param infoHeader 表头左列文字
 * @param quantityHeader 表头右列文字
 * @param showName 是否显示每行的名称（第二行）；归还页只显示 RFID 时传 false
 * @param rowVerticalPadding 每行上下内边距（行越小人越紧凑）
 * @param headerHeight 表头高度
 */
@Composable
fun ToolInfoTable(
    rows: List<ToolInfoRow>,
    modifier: Modifier = Modifier,
    infoHeader: String = "工器具信息",
    quantityHeader: String = "数量/件",
    showName: Boolean = true,
    rowVerticalPadding: Int = 5,
    headerHeight: Int = 40
) {
    val textDark = Color(0xFF222222)
    val textGray = Color(0xFF888888)
    val headerBg = Color(0xFFF5F6F8)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        // 表头
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight.dp)
                .background(headerBg)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = infoHeader,
                modifier = Modifier.weight(1f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = textGray
            )
            Text(
                text = quantityHeader,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = textGray
            )
        }
        HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))

        // 数据行
        rows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = rowVerticalPadding.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = row.rfid,
                        fontSize = 14.sp,
                        lineHeight = 16.sp,
                        color = textDark
                    )
                    if (showName && row.name.isNotBlank()) {
                        Text(
                            text = row.name,
                            fontSize = 13.sp,
                            lineHeight = 14.sp,
                            color = textGray,
                            modifier = Modifier.padding(top = 1.dp)
                        )
                    }
                }
                Text(
                    text = row.value ?: "${row.quantity}",
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                    color = row.valueColor ?: textDark
                )
            }
            HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F0))
        }
    }
}