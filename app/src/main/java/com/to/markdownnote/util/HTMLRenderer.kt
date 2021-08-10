package com.to.markdownnote.util

import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_BLOCKQUOTE

fun renderHTML(textView: TextView, html: String) {
    textView.text = HtmlCompat.fromHtml(html, FROM_HTML_SEPARATOR_LINE_BREAK_BLOCKQUOTE)
}