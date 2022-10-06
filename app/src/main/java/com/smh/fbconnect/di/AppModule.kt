package com.smh.fbconnect.di

import android.content.Context
import com.smh.fbconnect.utils.FileManager
import com.smh.fbconnect.utils.dispatchers.DispatcherProvider
import com.smh.fbconnect.utils.dispatchers.Dispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideDispatcherProvider(): DispatcherProvider {
        return Dispatchers()
    }

}