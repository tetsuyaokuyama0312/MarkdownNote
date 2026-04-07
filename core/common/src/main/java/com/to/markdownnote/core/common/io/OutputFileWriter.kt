package com.to.markdownnote.core.common.io

import android.content.Context
import android.os.Environment
import com.to.markdownnote.core.common.util.nowTimestampForFileName
import java.io.File
import java.io.FileWriter

private const val DEFAULT_OUTPUT_FILE_NAME_PATTERN = "memo_%s.%s"

fun Context.writeTextFile(outputFileName: String, text: String): String {
    val outDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
    val outFile = File(outDir, outputFileName)
    FileWriter(outFile).use { writer ->
        writer.append(text)
    }
    return outFile.absolutePath
}

fun OutputFileType.defaultFileName(): String =
    DEFAULT_OUTPUT_FILE_NAME_PATTERN.format(nowTimestampForFileName(), getExtension())
