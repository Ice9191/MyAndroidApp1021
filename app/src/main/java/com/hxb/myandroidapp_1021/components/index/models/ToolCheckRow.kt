package com.hxb.myandroidapp_1021.components.index.models

data class ToolCheckRow(
    val id: String,
    val rfid: String,
    val subtitle: String = "",
    val quantity: Int = 1,
    val checked: Boolean = false
)
