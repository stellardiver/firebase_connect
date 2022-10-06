package com.smh.fbconnect.data.local.db

import com.smh.fbconnect.data.local.entity.AppEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseHelper @Inject constructor(private val appDatabase: AppDatabase) {

    suspend fun getApps(): List<AppEntity>? = appDatabase.appDao.getAllApps()

    suspend fun getAppById(appId: Int) = appDatabase.appDao.getAppById(appId = appId)
}