package com.hxb.myandroidapp_1021.feature.tempreturn.domain.repository

import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnConfirmRequest
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnOrder

interface TempReturnRepository {
    /**
     * 读取已经生成的归还单；归还单由临时借出确认到最后一件时自动生成，不在此处新建。
     */
    suspend fun loadOrders(): List<TempReturnOrder>

    /**
     * 确认单件归还并保存整单，返回更新后的归还单；找不到单据时返回 null。
     */
    suspend fun submitReturn(request: TempReturnConfirmRequest): TempReturnOrder?
}
