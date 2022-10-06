package com.smh.fbconnect.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.smh.fbconnect.data.local.entity.AppEntity

@Dao
interface AppDao {

    @Query("SELECT * FROM appEntity")
    suspend fun getAllApps(): List<AppEntity>?

    @Query("SELECT * FROM appEntity WHERE id = :appId")
    suspend fun getAppById(appId: Int): AppEntity?
}