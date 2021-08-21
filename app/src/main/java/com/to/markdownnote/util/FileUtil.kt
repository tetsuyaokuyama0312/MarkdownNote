package com.to.markdownnote.util

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileWriter

/** デフォルトの出力ファイル名パターン: memo_${currentTime}.${fileExtension} */
private const val DEFAULT_OUTPUT_FILE_NAME_PATTERN = "memo_%s.%s"

/** デフォルトの出力ファイル拡張子: txt */
private const val DEFAULT_OUTPUT_FILE_EXTENSION = "txt"

/**
 * テキストファイルを出力する。
 *
 * @param context コンテキスト
 * @param outputFileName 出力ファイル名
 * @param text 出力するテキスト
 * @return 出力ファイルパス
 */
fun writeTextFile(context: Context, outputFileName: String, text: String): String {
    val outDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
    val outFile = File(outDir, outputFileName)
    val writer = FileWriter(outFile)
    writer.append(text)
    writer.flush()
    writer.close()
    return outFile.absolutePath
}

/**
 * デフォルトの出力ファイル名を取得する。
 *
 * @param extension 出力ファイルの拡張子(デフォルト: txt)
 * @return デフォルトの出力ファイル名
 */
fun getDefaultOutputFileName(extension: String = DEFAULT_OUTPUT_FILE_EXTENSION): String {
    return DEFAULT_OUTPUT_FILE_NAME_PATTERN.format(nowTimestampForFileName(), extension)
}