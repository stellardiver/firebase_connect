package com.smh.fbconnect.data.local.db

import android.net.Uri
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class Converters {

    @TypeConverter
    fun fromMUri(uri: Uri?): String? {
        return uri?.toString()
    }

    @TypeConverter
    fun fromUriString(value: String?): Uri? {
        return if (value == null) null else Uri.parse(value)
    }
}