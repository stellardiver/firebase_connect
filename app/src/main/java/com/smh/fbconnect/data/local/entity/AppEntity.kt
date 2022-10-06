package com.smh.fbconnect.data.local.entity

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AppEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val credentialsPath: Uri
)