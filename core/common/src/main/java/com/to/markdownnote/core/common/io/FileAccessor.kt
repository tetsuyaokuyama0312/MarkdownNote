package com.to.markdownnote.core.common.io

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileWriter

class FileAccessor(private val context: Context) {

    fun writeTextFile(outputFileName: String, text: String): String {
        val outDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val outFile = File(outDir, outputFileName)
        FileWriter(outFile).use { writer ->
            writer.append(text)
        }
        return outFile.absolutePath
    }
}
