package com.to.markdownnote.util

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileWriter

fun writeTextFile(context: Context, outputFileName: String, text: String): String {
    val outDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
    val out = File(outDir, outputFileName)
    val writer = FileWriter(out)
    writer.append(text)
    writer.flush()
    writer.close()
    return out.absolutePath
}