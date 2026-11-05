package com.hxb.myandroidapp_1021.components.index.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 扫码页中间的读取控制区：左边「开始读取 / 停止读取」，右边「下一步」。
 * 两个按钮等宽（weight 1f），正在读取时左边按钮变红。
 *
 * 用法：
 * ```
 * ScanControlBar(
 *     isScanning = isScanning,
 *     onScanClick = { if (isScanning) scanner.stopScan() else scanner.startScan() },
 *     nextEnabled = readList.isNotEmpty() && unreadList.isEmpty(),
 *     onNextClick = onNextClick
 * )
 * ```
 *
 * @param isScanning 是否正在读取，true 时按钮显示「停止读取」且变红
 * @param onScanClick 左边按钮点击（开始 / 停止读取由调用方决定）
 * @param nextEnabled 下一步是否可点
 * @param onNextClick 下一步点击
 * @param scanText 左边按钮文字，默认按 [isScanning] 自动切换
 * @param nextText 右边按钮文字
 */
@Composable
fun ScanControlBar(
    isScanning: Boolean,
    onScanClick: () -> Unit,
    nextEnabled: Boolean,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
    scanText: String = if (isScanning) "停止读取" else "开始读取",
    nextText: String = "下一步"
) {
    val themeBlue = Color(0xFF1E71C8)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onScanClick,
            modifier = Modifier
                .weight(1f)
                .height(44.dp),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isScanning) Color(0xFFE53935) else themeBlue
            )
        ) {
            Icon(
                imageVector = Icons.Filled.QrCodeScanner,
                contentDescription = "Scan",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = scanText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Button(
            onClick = onNextClick,
            enabled = nextEnabled,
            modifier = Modifier
                .weight(1f)
                .height(44.dp),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = themeBlue,
                disabledContainerColor = Color(0xFFB0BEC5)
            )
        ) {
            Text(
                text = nextText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
