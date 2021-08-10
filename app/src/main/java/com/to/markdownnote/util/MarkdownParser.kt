package com.to.markdownnote.util

import com.to.markdownnote.util.Processors.parser
import com.to.markdownnote.util.Processors.renderer
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension
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
    val document = parser.parse(beforeParse(markdown))
    return renderer.render(document)
}

/**
 * パース前処理を行う。
 *
 * @param markdown Markdownのテキスト
 * @return 前処理結果のテキスト
 */
private fun beforeParse(markdown: String): String {
    val lines = markdown.split(System.lineSeparator())
    var result = mutableListOf<String>()
    // 空行が連続した回数
    var consBlankLineCnt = 0

    for (line in lines) {
        if (line.isEmpty()) {
            consBlankLineCnt++

            if (consBlankLineCnt >= 2) {
                // 空行が2個以上連続している場合はbrタグ
                result.add("<br />")
            } else {
                // 2個以上連続していない場合はそのまま
                result.add("")
            }
        } else {
            if (consBlankLineCnt >= 2) {
                // 空行が2個以上連続していた場合は最後にbrタグなしの空行を挟むため空文字
                result.add("")
            }
            consBlankLineCnt = 0
            result.add(line)
        }
    }

    return result.joinToString(System.lineSeparator())
}

private object Processors {
    private val options = MutableDataSet()

    init {
        options.set(
            Parser.EXTENSIONS,
            listOf(
                StrikethroughExtension.create(), // 打ち消し線に対応
                TablesExtension.create(), // テーブルに対応
                TocExtension.create(), // [TOC] の部分に目次を生成する
            )
        )
        options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");
    }

    val parser = Parser.builder(options).build()
    val renderer = HtmlRenderer.builder(options).build()
}