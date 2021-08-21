package com.to.markdownnote.activity

import com.to.markdownnote.util.parseMarkdownToHTML

/**
 * 出力ファイルタイプを表す列挙型
 */
enum class OutputFileType {
    /** プレーンテキスト */
    PLAIN_TEXT {
        override fun getExtension(): String {
            return "txt"
        }
    },

    /** Markdown */
    MARKDOWN {
        override fun getExtension(): String {
            return "md"
        }
    },

    /** HTML */
    HTML {
        override fun convert(markdown: String): String {
            return parseMarkdownToHTML(markdown)
        }

        override fun getExtension(): String {
            return "html"
        }
    };

    /**
     * Markdownテキストをこの出力ファイルタイプの形式に変換する。
     *
     * デフォルトでは入力テキストをそのまま返却する。
     *
     * @param markdown Markdownテキスト
     * @return 変換後のテキスト
     */
    open fun convert(markdown: String): String {
        return markdown
    }

    /**
     * この出力ファイルタイプの拡張子を取得する。
     *
     * @return 拡張子
     */
    abstract fun getExtension(): String
}