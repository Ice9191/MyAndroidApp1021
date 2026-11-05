package com.hxb.myandroidapp_1021.components.index.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxb.myandroidapp_1021.components.index.models.DisplayField
import com.hxb.myandroidapp_1021.components.index.models.InfoRow

/**
 * 扫码类页面的顶部信息卡片：白底、内边距 16dp、行间距 10dp，
 * 每一行都是左键右值两端对齐（内部由私有的 [InfoLine] 画）。
 *
 * 用法：
 * ```
 * InfoCard(
 *     rows = listOf(
 *         InfoRow(DisplayField("拣选单号", no), DisplayField("状态", status), leftBold = true, rightColor = themeBlue),
 *         InfoRow(DisplayField("班组", team), DisplayField("领用人", person)),
 *         InfoRow(DisplayField("工具数量", "${count}件"), DisplayField("期望领用", time))
 *     )
 * )
 * ```
 *
 * @param rows 卡片内容，一行一对左右字段
 * @param containerColor 卡片底色
 * @param horizontalPadding 卡片左右内边距
 * @param verticalPadding 卡片上下内边距
 * @param lineSpacing 行与行之间的间距
 */
@Composable
fun InfoCard(
    rows: List<InfoRow>,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.White,
    horizontalPadding: Int = 16,
    verticalPadding: Int = 16,
    lineSpacing: Int = 10
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(containerColor)
            .padding(
                horizontal = horizontalPadding.dp,
                vertical = verticalPadding.dp
            ),
        verticalArrangement = Arrangement.spacedBy(lineSpacing.dp)
    ) {
        rows.forEach { row ->
            InfoLine(
                left = row.left,
                right = row.right,
                leftBold = row.leftBold,
                rightColor = row.rightColor
            )
        }
    }
}

/**
 * 信息卡片里的一行：左边一组「标签：值」、右边一组「标签：值」，两端对齐（SpaceBetween）。
 * 冒号用中文全角「：」，和页面原来的样式一致。
 *
 * 只给 [InfoCard] 用，所以是 private；要单独画一行就把左右字段包成 [InfoRow] 传给 [InfoCard]。
 *
 * @param left 左半部分的「标签 + 值」
 * @param right 右半部分的「标签 + 值」
 * @param leftBold 左值是否加粗（单号、金额这类要突出的值）
 * @param rightColor 右值颜色，null 表示用默认深色（状态文字可以传主题蓝 / 红色）
 * @param textDark 值的默认颜色
 * @param textGray 标签的颜色
 */
@Composable
private fun InfoLine(
    left: DisplayField,
    right: DisplayField,
    modifier: Modifier = Modifier,
    leftBold: Boolean = false,
    rightColor: Color? = null,
    textDark: Color = Color(0xFF222222),
    textGray: Color = Color(0xFF888888)
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("${left.label}：", fontSize = 13.sp, color = textGray)
            Text(
                text = left.value,
                fontSize = if (leftBold) 15.sp else 13.sp,
                fontWeight = if (leftBold) FontWeight.Bold else FontWeight.Normal,
                color = textDark
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("${right.label}：", fontSize = 13.sp, color = textGray)
            Text(
                text = right.value,
                fontSize = 13.sp,
                fontWeight = if (rightColor != null) FontWeight.Medium else FontWeight.Normal,
                color = rightColor ?: textDark
            )
        }
    }
}
