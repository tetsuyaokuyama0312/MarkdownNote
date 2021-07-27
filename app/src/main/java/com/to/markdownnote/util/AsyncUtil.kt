package com.to.markdownnote.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun <T> runAsync(action: () -> T, onFinish: (T) -> Unit = {}) {
    GlobalScope.launch(Dispatchers.Main) {
        withContext(Dispatchers.Default) {
            action()
        }.let { onFinish(it) }
    }
}