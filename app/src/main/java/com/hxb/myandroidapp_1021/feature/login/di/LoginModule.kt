package com.hxb.myandroidapp_1021.feature.login.di

import com.hxb.myandroidapp_1021.feature.login.data.repository.MockLoginRepository
import com.hxb.myandroidapp_1021.feature.login.domain.repository.LoginRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LoginModule {
    // 登录模块当前使用模拟数据，ViewModel 通过接口获取该实现。
    @Binds
    abstract fun bindLoginRepository(repository: MockLoginRepository): LoginRepository
}
