package com.hxb.myandroidapp_1021.di

import com.hxb.myandroidapp_1021.data.dashboard.DashboardRepositoryImpl
import com.hxb.myandroidapp_1021.data.otherpicking.OtherPickingRepositoryImpl
import com.hxb.myandroidapp_1021.data.otherputaway.OtherPutawayRepositoryImpl
import com.hxb.myandroidapp_1021.data.overduealert.OverdueAlertRepositoryImpl
import com.hxb.myandroidapp_1021.data.picking.PickingRepositoryImpl
import com.hxb.myandroidapp_1021.data.putaway.PutawayRepositoryImpl
import com.hxb.myandroidapp_1021.data.returns.ReturnRepositoryImpl
import com.hxb.myandroidapp_1021.data.scanner.RfidReaderFactoryImpl
import com.hxb.myandroidapp_1021.data.stockdetail.StockDetailRepositoryImpl
import com.hxb.myandroidapp_1021.data.temp.TempRepositoryImpl
import com.hxb.myandroidapp_1021.domain.dashboard.DashboardRepository
import com.hxb.myandroidapp_1021.domain.otherpicking.OtherPickingRepository
import com.hxb.myandroidapp_1021.domain.otherputaway.OtherPutawayRepository
import com.hxb.myandroidapp_1021.domain.overduealert.OverdueAlertRepository
import com.hxb.myandroidapp_1021.domain.picking.PickingRepository
import com.hxb.myandroidapp_1021.domain.putaway.PutawayRepository
import com.hxb.myandroidapp_1021.domain.returns.ReturnRepository
import com.hxb.myandroidapp_1021.domain.scanner.RfidReaderFactory
import com.hxb.myandroidapp_1021.domain.stockdetail.StockDetailRepository
import com.hxb.myandroidapp_1021.domain.temp.TempRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun picking(implementation: PickingRepositoryImpl): PickingRepository =
        implementation

    @Provides
    @Singleton
    fun returns(implementation: ReturnRepositoryImpl): ReturnRepository =
        implementation

    @Provides
    @Singleton
    fun putaway(implementation: PutawayRepositoryImpl): PutawayRepository =
        implementation

    @Provides
    @Singleton
    fun otherpicking(implementation: OtherPickingRepositoryImpl): OtherPickingRepository =
        implementation

    @Provides
    @Singleton
    fun otherputaway(implementation: OtherPutawayRepositoryImpl): OtherPutawayRepository =
        implementation

    @Provides
    @Singleton
    fun stockdetail(implementation: StockDetailRepositoryImpl): StockDetailRepository =
        implementation

    @Provides
    @Singleton
    fun overduealert(implementation: OverdueAlertRepositoryImpl): OverdueAlertRepository =
        implementation

    @Provides
    @Singleton
    fun temp(implementation: TempRepositoryImpl): TempRepository =
        implementation

    @Provides
    @Singleton
    fun dashboard(implementation: DashboardRepositoryImpl): DashboardRepository =
        implementation

    @Provides
    @Singleton
    fun scanner(implementation: RfidReaderFactoryImpl): RfidReaderFactory =
        implementation
}
