package com.hxb.myandroidapp_1021.di

import com.hxb.myandroidapp_1021.core.rfid.RfidScanner
import com.hxb.myandroidapp_1021.core.rfid.RfidScannerImpl
import com.hxb.myandroidapp_1021.feature.dashboard.data.repository.DashboardRepositoryImpl
import com.hxb.myandroidapp_1021.feature.dashboard.domain.repository.DashboardRepository
import com.hxb.myandroidapp_1021.feature.login.data.repository.LoginRepositoryImpl
import com.hxb.myandroidapp_1021.feature.login.domain.repository.LoginRepository
import com.hxb.myandroidapp_1021.feature.otherpicking.data.repository.MockOtherPickingRepository
import com.hxb.myandroidapp_1021.feature.otherpicking.domain.repository.OtherPickingRepository
import com.hxb.myandroidapp_1021.feature.otherputaway.data.repository.MockOtherPutawayRepository
import com.hxb.myandroidapp_1021.feature.otherputaway.domain.repository.OtherPutawayRepository
import com.hxb.myandroidapp_1021.feature.overduealert.data.repository.MockOverdueAlertRepository
import com.hxb.myandroidapp_1021.feature.overduealert.domain.repository.OverdueAlertRepository
import com.hxb.myandroidapp_1021.feature.picking.data.repository.MockPickingRepository
import com.hxb.myandroidapp_1021.feature.picking.domain.repository.PickingRepository
import com.hxb.myandroidapp_1021.feature.putaway.data.repository.MockPutawayRepository
import com.hxb.myandroidapp_1021.feature.putaway.domain.repository.PutawayRepository
import com.hxb.myandroidapp_1021.feature.returns.data.repository.MockReturnRepository
import com.hxb.myandroidapp_1021.feature.returns.domain.repository.ReturnRepository
import com.hxb.myandroidapp_1021.feature.serverconfig.data.repository.ServerConfigRepositoryImpl
import com.hxb.myandroidapp_1021.feature.serverconfig.domain.repository.ServerConfigRepository
import com.hxb.myandroidapp_1021.feature.stockdetail.data.repository.MockStockDetailRepository
import com.hxb.myandroidapp_1021.feature.stockdetail.domain.repository.StockDetailRepository
import com.hxb.myandroidapp_1021.feature.tempborrow.data.repository.MockTempBorrowRepository
import com.hxb.myandroidapp_1021.feature.tempborrow.domain.repository.TempBorrowRepository
import com.hxb.myandroidapp_1021.feature.tempreturn.data.repository.MockTempReturnRepository
import com.hxb.myandroidapp_1021.feature.tempreturn.domain.repository.TempReturnRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindRfidScanner(scanner: RfidScannerImpl): RfidScanner

    @Binds
    abstract fun bindDashboardRepository(repository: DashboardRepositoryImpl): DashboardRepository

    @Binds
    abstract fun bindLoginRepository(repository: LoginRepositoryImpl): LoginRepository

    @Binds
    abstract fun bindOtherPickingRepository(repository: MockOtherPickingRepository): OtherPickingRepository

    @Binds
    abstract fun bindOtherPutawayRepository(repository: MockOtherPutawayRepository): OtherPutawayRepository

    @Binds
    abstract fun bindOverdueAlertRepository(repository: MockOverdueAlertRepository): OverdueAlertRepository

    @Binds
    abstract fun bindPickingRepository(repository: MockPickingRepository): PickingRepository

    @Binds
    abstract fun bindPutawayRepository(repository: MockPutawayRepository): PutawayRepository

    @Binds
    abstract fun bindReturnRepository(repository: MockReturnRepository): ReturnRepository

    @Binds
    abstract fun bindServerConfigRepository(repository: ServerConfigRepositoryImpl): ServerConfigRepository

    @Binds
    abstract fun bindStockDetailRepository(repository: MockStockDetailRepository): StockDetailRepository

    @Binds
    abstract fun bindTempBorrowRepository(repository: MockTempBorrowRepository): TempBorrowRepository

    @Binds
    abstract fun bindTempReturnRepository(repository: MockTempReturnRepository): TempReturnRepository
}
