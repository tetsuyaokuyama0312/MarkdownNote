package com.to.markdownnote.core.common.util

import android.util.Log
import java.io.File

fun logDebug(msg: String?) {
    val thisFileName = Throwable().stackTrace[0].fileName
    val tag = Throwable().stackTrace
        .map { it.fileName }
        .firstOrNull { it != thisFileName }
        ?.let { File(it).nameWithoutExtension }
        ?: "MarkdownNote"
    Log.d(tag, msg ?: "")
}
