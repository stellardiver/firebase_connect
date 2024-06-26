package com.smh.fbconnect.data.local.db

import com.smh.fbconnect.data.local.entity.AppEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseHelper @Inject constructor(private val appDatabase: AppDatabase) {

    suspend fun getApps(): List<AppEntity>? = appDatabase.appDao.getAllApps()

    suspend fun getAppById(appId: Int) = appDatabase.appDao.getAppById(appId = appId)

    suspend fun getAppByName(appName: String) = appDatabase.appDao.getAppByName(appName = appName)

    suspend fun insertApp(app: AppEntity) = appDatabase.appDao.insertApp(app = app)

    suspend fun deleteApp(app: AppEntity) = appDatabase.appDao.deleteApp(app = app)

    suspend fun deleteAppById(appId: Int) = appDatabase.appDao.deleteAppById(appId = appId)
}