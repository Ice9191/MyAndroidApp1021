package com.hxb.myandroidapp_1021.components.index.models

import androidx.compose.ui.graphics.Color

data class InfoRow(
    val left: DisplayField,
    val right: DisplayField,
    val leftBold: Boolean = false,
    val rightColor: Color? = null
)