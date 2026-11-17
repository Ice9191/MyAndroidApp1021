package com.hxb.myandroidapp_1021.di

import com.hxb.myandroidapp_1021.feature.login.data.repository.LoginRepositoryImpl
import com.hxb.myandroidapp_1021.feature.login.domain.repository.LoginRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    // 登录通过真实接口获取数据，ViewModel 仍只依赖仓库接口。
    @Binds
    abstract fun bindLoginRepository(repository: LoginRepositoryImpl): LoginRepository
}
