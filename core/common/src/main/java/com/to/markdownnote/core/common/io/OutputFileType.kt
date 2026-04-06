package com.to.markdownnote.core.common.io

import com.to.markdownnote.core.common.util.parseMarkdownToHTML

enum class OutputFileType {
    PLAIN_TEXT {
        override fun getExtension() = "txt"
    },
    MARKDOWN {
        override fun getExtension() = "md"
    },
    HTML {
        override fun convert(markdown: String) = parseMarkdownToHTML(markdown)
        override fun getExtension() = "html"
    };

    open fun convert(markdown: String): String = markdown
    abstract fun getExtension(): String
}
