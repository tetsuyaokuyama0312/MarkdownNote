package com.to.markdownnote.util

import android.util.Log
import java.io.File

/**
 * 呼び出し元ファイル名をタグとして使用して、デバッグレベルでログを出力する。
 *
 * @param msg ログ出力するメッセージ
 */
fun logDebug(msg: String?) {
    val callerFileName = getCallerFileName()
    Log.d(callerFileName, msg ?: "")
}

/**
 * 呼び出し元ファイル名を取得する。
 *
 * @return 呼び出し元ファイル名
 */
private fun getCallerFileName(): String {
    // 自ファイルを除き、最も呼び出し階層が近いファイル名を取得
    val thisFileName = Throwable().stackTrace[0].fileName
    val callerFileName =
        Throwable().stackTrace.map { it.fileName }.firstOrNull { it != thisFileName }
        // 自ファイル内での呼び出しのみの場合は自ファイル名
            ?: thisFileName
    // 拡張子を除去して返す
    return File(callerFileName).nameWithoutExtension
}
