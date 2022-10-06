package com.smh.fbconnect.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.smh.fbconnect.data.local.entity.AppEntity
import com.smh.fbconnect.data.local.dao.AppDao

@Database(
    entities = [ AppEntity::class ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract val appDao: AppDao
}