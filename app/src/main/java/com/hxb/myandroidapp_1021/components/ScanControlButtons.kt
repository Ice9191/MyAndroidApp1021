package com.hxb.myandroidapp_1021.components

import com.hxb.myandroidapp_1021.theme.AppColors
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 按钮文字、可用状态和点击事件由调用页面提供。
 */
data class ScanControlButton(
    val text: String,
    val onClick: () -> Unit,
    val enabled: Boolean = true
)

@Composable
fun ScanControlButtons(
    firstButton: ScanControlButton,
    secondButton: ScanControlButton,
    thirdButton: ScanControlButton,
    modifier: Modifier = Modifier
) {
    val themeBlue = MaterialTheme.colorScheme.primary
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = firstButton.onClick,
            enabled = firstButton.enabled,
            modifier = Modifier
                .weight(1f)
                .height(44.dp),
            contentPadding = PaddingValues(4.dp),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                text = firstButton.text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Button(
            onClick = secondButton.onClick,
            enabled = secondButton.enabled,
            modifier = Modifier
                .weight(1f)
                .height(44.dp),
            contentPadding = PaddingValues(4.dp),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = themeBlue)
        ) {
            Text(secondButton.text, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

        Button(
            onClick = thirdButton.onClick,
            enabled = thirdButton.enabled,
            modifier = Modifier
                .weight(1f)
                .height(44.dp),
            contentPadding = PaddingValues(4.dp),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = themeBlue,
                disabledContainerColor = AppColors.DisabledContainer
            )
        ) {
            Text(
                text = thirdButton.text,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
