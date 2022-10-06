package com.smh.fbconnect.utils.extensions

import android.content.Context
import java.io.File

val Context.appCacheDir get() = File(this.cacheDir,"/firebase_connect_files_cache").apply {
    if (!this.exists()) this.mkdir()
}