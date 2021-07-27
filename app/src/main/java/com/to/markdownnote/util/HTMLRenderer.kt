package com.to.markdownnote.util

import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_BLOCKQUOTE

fun renderHTML(textView: TextView, html: String) {
    // TODO 最終的にフラグは何が良いかは別途検討
    textView.text = HtmlCompat.fromHtml(html, FROM_HTML_SEPARATOR_LINE_BREAK_BLOCKQUOTE)
}