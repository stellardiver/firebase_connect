package com.smh.fbconnect.data.local.db

import android.net.Uri
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class Converters {

    @TypeConverter
    fun fromMUri(uri: Uri?): String? {
        val gson = Gson()
        return gson.toJson(uri)
    }

    @TypeConverter
    fun fromUriString(value: String?): Uri? {
        val listType: Type = object : TypeToken<Uri?>() {}.type
        return Gson().fromJson<Uri?>(value, listType)
    }
}