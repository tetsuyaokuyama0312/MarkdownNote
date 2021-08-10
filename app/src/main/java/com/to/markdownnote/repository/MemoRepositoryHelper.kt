package com.to.markdownnote.repository

import android.content.Context
import com.to.markdownnote.model.Memo
import com.to.markdownnote.util.nowTimestampSec
import com.to.markdownnote.util.runAsync

/**
 * データベースからメモを全件取得する。
 *
 * @param context コンテキスト
 * @param onFinish 取得完了した際に呼び出されるハンドラ
 * @receiver 取得したメモリスト
 */
fun selectAllMemo(context: Context, onFinish: (List<Memo>) -> Unit = {}) {
    runAsync({
        getMemoRepository(context).selectAll()
    }) {
        onFinish(it)
    }
}

/**
 * データベースにメモを新規登録する。
 *
 * @param context コンテキスト
 * @param newMemo 登録するメモ
 * @param onFinish 登録完了した際に呼び出されるハンドラ
 * @receiver 新規登録したメモ
 */
fun insertMemo(context: Context, newMemo: Memo, onFinish: (Memo) -> Unit = {}) {
    runAsync({
        getMemoRepository(context).insert(newMemo)
        newMemo
    }) {
        onFinish(it)
    }
}

/**
 * データベースにメモを新規登録する。
 *
 * @param context コンテキスト
 * @param text 登録するメモのテキスト
 * @param onFinish 登録完了した際に呼び出されるハンドラ
 * @receiver 新規登録したメモ
 */
fun insertMemo(context: Context, text: String, onFinish: (Memo) -> Unit = {}) {
    insertMemo(context, newMemo(text), onFinish)
}

/**
 * データベースのメモを更新する。
 *
 * @param context コンテキスト
 * @param newMemo 更新対象のメモ
 * @param onFinish 更新完了した際に呼び出されるハンドラ
 * @receiver 更新したメモ
 */
fun updateMemo(context: Context, newMemo: Memo, onFinish: (Memo) -> Unit = {}) {
    runAsync({
        getMemoRepository(context).update(newMemo)
        newMemo
    }) {
        onFinish(it)
    }
}

/**
 * データベースからメモを削除する。
 *
 * @param context コンテキスト
 * @param memo 削除対象のメモ
 * @param onFinish 削除完了した際に呼び出されるハンドラ
 * @receiver 削除したメモ
 */
fun deleteMemo(context: Context, memo: Memo, onFinish: (Memo) -> Unit = {}) {
    runAsync({
        getMemoRepository(context).delete(memo)
        memo
    }) {
        onFinish(it)
    }
}

private fun newMemo(text: String): Memo {
    val nowTimeStamp = nowTimestampSec()
    return Memo(text, nowTimeStamp, nowTimeStamp)
}
