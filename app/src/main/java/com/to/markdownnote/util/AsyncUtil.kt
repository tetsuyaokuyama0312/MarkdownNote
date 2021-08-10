package com.to.markdownnote.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 指定されたアクションを非同期で実行する。
 *
 * @param T アクションの戻り値型
 * @param action アクション
 * @param onFinish アクション完了時に呼び出されるハンドラ
 * @receiver アクションの戻り値
 */
fun <T> runAsync(action: () -> T, onFinish: (T) -> Unit = {}) {
    GlobalScope.launch(Dispatchers.Main) {
        withContext(Dispatchers.Default) {
            action()
        }.let { onFinish(it) }
    }
}