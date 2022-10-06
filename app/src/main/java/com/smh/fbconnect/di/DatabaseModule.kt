package com.smh.fbconnect.di

import android.content.Context
import androidx.room.Room
import com.smh.fbconnect.data.local.dao.AppDao
import com.smh.fbconnect.data.local.db.AppDatabase
import com.smh.fbconnect.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            Constants.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideUserDao(appDatabase: AppDatabase): AppDao {
        return appDatabase.appDao
    }

}