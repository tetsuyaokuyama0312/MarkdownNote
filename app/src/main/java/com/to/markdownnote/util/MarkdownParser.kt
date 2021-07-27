package com.to.markdownnote.util

import com.to.markdownnote.util.Processors.parser
import com.to.markdownnote.util.Processors.renderer
import com.vladsch.flexmark.ext.anchorlink.AnchorLinkExtension
import com.vladsch.flexmark.ext.gfm.issues.GfmIssuesExtension
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension
import com.vladsch.flexmark.ext.gfm.users.GfmUsersExtension
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.ext.toc.TocExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.MutableDataSet

/**
 * MarkdownのテキストをパースしてHTMLとして返す。
 *
 * @param markdown Markdownのテキスト
 * @return パース結果のHTML
 */
fun parseMarkdownToHTML(markdown: String): String {
    val document = parser.parse(markdown)
    return renderer.render(document)
}

// TODO 最終的な形はまたあとで確定する

private object Processors {
    private val options = MutableDataSet()

    init {
        options.set(
            Parser.EXTENSIONS,
            listOf(
                AnchorLinkExtension.create(), // 見出しにアンカーを付ける
                StrikethroughExtension.create(), // 打ち消し線に対応
                TablesExtension.create(), // テーブルに対応
                TocExtension.create(), // [TOC] の部分に目次を生成する
                GfmIssuesExtension.create(),
                GfmUsersExtension.create(),
            )
        )
        options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");
    }

    val parser = Parser.builder(options).build()
    val renderer = HtmlRenderer.builder(options).build()
}