package com.hxb.myandroidapp_1021.di

import com.hxb.myandroidapp_1021.core.network.HttpClient
import com.hxb.myandroidapp_1021.feature.login.data.remote.LoginApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = HttpClient.retrofit

    @Provides
    @Singleton
    fun provideLoginApi(retrofit: Retrofit): LoginApi = retrofit.create(LoginApi::class.java)
}
