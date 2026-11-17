package com.hxb.myandroidapp_1021.di

import android.content.Context
import androidx.room.Room
import com.hxb.myandroidapp_1021.data.session.SessionRepositoryImpl
import com.hxb.myandroidapp_1021.domain.session.SessionRepository
import com.hxb.myandroidapp_1021.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun json(): Json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    @Provides
    @Singleton
    fun database(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "tool_pda.db").build()

    @Provides
    fun cacheDao(database: AppDatabase) = database.queryCacheDao()

    @Provides
    @Singleton
    fun client(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS)
        .callTimeout(30, TimeUnit.SECONDS).followRedirects(false).followSslRedirects(false)
        .retryOnConnectionFailure(false).build()

    @Provides
    @Singleton
    fun session(implementation: SessionRepositoryImpl): SessionRepository = implementation
}
