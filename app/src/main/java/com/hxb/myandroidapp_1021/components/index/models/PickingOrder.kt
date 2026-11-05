package com.hxb.myandroidapp_1021.components.index.models

/** 拣选单（借出列表 / 拣选页 / 出库确认页共用） */
data class PickingOrder(
    val orderNo: String,
    val team: String,
    val person: String,
    val toolCount: Int,
    val expectedTime: String,
    val status: String
)
