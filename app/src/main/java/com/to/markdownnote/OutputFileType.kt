package com.to.markdownnote

import com.to.markdownnote.util.parseMarkdownToHTML

enum class OutputFileType {
    PLAIN_TEXT {
        override fun getExtension(): String {
            return "txt"
        }
    },
    MARKDOWN {
        override fun getExtension(): String {
            return "md"
        }
    },
    HTML {
        override fun convert(text: String): String {
            return parseMarkdownToHTML(text)
        }

        override fun getExtension(): String {
            return "html"
        }
    };

    open fun convert(text: String): String {
        return text
    }

    abstract fun getExtension(): String
}