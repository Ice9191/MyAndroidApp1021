package com.hxb.myandroidapp_1021.feature.tempreturn.data.repository

import com.hxb.myandroidapp_1021.feature.tempborrow.data.repository.TempBorrowReturnStore
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnConfirmRequest
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.model.TempReturnOrder
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.repository.TempReturnRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

/**
 * 临时归还的本地演示数据源；归还单与临时借出共用一份内存数据，进入列表时重新读取。
 */
class MockTempReturnRepository @Inject constructor(
    private val store: TempBorrowReturnStore
) : TempReturnRepository {

    private val mockOperator = "演示材料员"

    override suspend fun loadOrders(): List<TempReturnOrder> {
        delay(500.milliseconds)
        return store.loadReturnOrders()
    }

    override suspend fun submitReturn(request: TempReturnConfirmRequest): TempReturnOrder? {
        delay(800.milliseconds)
        return store.submitReturn(request, mockOperator, now())
    }

    private fun now(): String =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
}
