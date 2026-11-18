package com.hxb.myandroidapp_1021.di

import com.hxb.myandroidapp_1021.feature.dashboard.data.repository.MockDashboardRepository
import com.hxb.myandroidapp_1021.feature.dashboard.domain.repository.DashboardRepository
import com.hxb.myandroidapp_1021.feature.login.data.repository.MockLoginRepository
import com.hxb.myandroidapp_1021.feature.login.domain.repository.LoginRepository
import com.hxb.myandroidapp_1021.feature.putaway.data.repository.MockPutawayRepository
import com.hxb.myandroidapp_1021.feature.putaway.domain.repository.PutawayRepository
import com.hxb.myandroidapp_1021.feature.serverconfig.data.repository.MockServerConfigRepository
import com.hxb.myandroidapp_1021.feature.serverconfig.domain.repository.ServerConfigRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindDashboardRepository(repository: MockDashboardRepository): DashboardRepository

    @Binds
    abstract fun bindLoginRepository(repository: MockLoginRepository): LoginRepository

    @Binds
    abstract fun bindPutawayRepository(repository: MockPutawayRepository): PutawayRepository

    @Binds
    abstract fun bindServerConfigRepository(repository: MockServerConfigRepository): ServerConfigRepository
}
