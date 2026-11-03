package com.hxb.myandroidapp_1021.components.index.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RepairPhotoInput() {
    val themeBlue = Color(0xFF1E71C8)
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedButton(
            onClick = {
                // 拍照功能暂留占位。
            },
            modifier = Modifier.weight(1f).height(44.dp), shape = RoundedCornerShape(4.dp)
        ) {
            Icon(Icons.Outlined.CameraAlt, contentDescription = null, tint = themeBlue, modifier = Modifier.size(18.dp))
            Text(" 拍照", fontSize = 13.sp, color = themeBlue)
        }
        OutlinedButton(
            onClick = {
                // 相册选择功能暂留占位。
            },
            modifier = Modifier.weight(1f).height(44.dp), shape = RoundedCornerShape(4.dp)
        ) {
            Icon(Icons.Outlined.PhotoLibrary, contentDescription = null, tint = themeBlue, modifier = Modifier.size(18.dp))
            Text(" 从相册选择", fontSize = 13.sp, color = themeBlue)
        }
    }
}
