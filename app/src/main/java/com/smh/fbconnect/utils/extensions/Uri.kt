package com.smh.fbconnect.utils.extensions

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import java.io.File

fun Uri.getFileExt(context: Context): String? {

    val extension: String? = if (this.scheme.equals(ContentResolver.SCHEME_CONTENT)) {
        val mime = MimeTypeMap.getSingleton()
        mime.getExtensionFromMimeType(context.contentResolver.getType(this))
    } else {
        MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(this.path!!)).toString())
    }

    return extension
}

fun Uri.getFileName(context: Context): String {

    this.scheme?.let { scheme ->

        if (scheme == "file") {
            this.lastPathSegment?.let { lastPath ->
                return lastPath
            }
        }
    }

    val returnCursor: Cursor = context.contentResolver.query(
        this,
        null,
        null,
        null,
        null
    )!!
    val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    returnCursor.moveToFirst()
    val name = returnCursor.getString(nameIndex)
    returnCursor.close()
    return name
}