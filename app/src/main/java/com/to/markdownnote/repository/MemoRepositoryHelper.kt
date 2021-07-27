package com.to.markdownnote.repository

import android.content.Context
import com.to.markdownnote.model.Memo
import com.to.markdownnote.util.nowTimestampSec
import com.to.markdownnote.util.runAsync

fun selectAllMemo(context: Context, onFinish: (List<Memo>) -> Unit = {}) {
    runAsync({
        getMemoRepository(context).selectAll()
    }) {
        onFinish(it)
    }
}

fun insertMemo(context: Context, text: String, onFinish: (Memo) -> Unit = {}) {
    runAsync({
        val newMemo = newMemo(text)
        getMemoRepository(context).insert(newMemo)
        newMemo
    }) {
        onFinish(it)
    }
}

fun updateMemo(context: Context, newMemo: Memo, onFinish: (Memo) -> Unit = {}) {
    runAsync({
        getMemoRepository(context).update(newMemo)
        newMemo
    }) {
        onFinish(it)
    }
}

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
