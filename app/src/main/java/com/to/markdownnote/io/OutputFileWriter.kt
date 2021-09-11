package com.to.markdownnote.io

import android.content.Context
import android.os.Environment
import com.to.markdownnote.util.nowTimestampForFileName
import java.io.File
import java.io.FileWriter

/** デフォルトの出力ファイル名パターン: memo_${currentTime}.${fileExtension} */
private const val DEFAULT_OUTPUT_FILE_NAME_PATTERN = "memo_%s.%s"

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
 * @param type 出力ファイルタイプ
 * @return デフォルトの出力ファイル名
 */
fun getDefaultOutputFileName(type: OutputFileType): String {
    return DEFAULT_OUTPUT_FILE_NAME_PATTERN.format(nowTimestampForFileName(), type.getExtension())
}