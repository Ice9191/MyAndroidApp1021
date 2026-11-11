package com.hxb.myandroidapp_1021.ui.scrapout

data class ScrapOrder(
    val orderNo: String,
    val applicant: String,
    val warehouse: String,
    val toolCount: Int,
    val isApproved: Boolean = true,
    val outboundStatus: String = "待出库"
)
