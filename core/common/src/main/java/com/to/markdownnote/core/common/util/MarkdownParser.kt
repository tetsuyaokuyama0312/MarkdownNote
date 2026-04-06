package com.to.markdownnote.core.common.util

import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

fun parseMarkdownToHTML(markdown: String): String {
    val document = MarkdownProcessor.parser.parse(preprocessMarkdown(markdown))
    return MarkdownProcessor.renderer.render(document)
}

private fun preprocessMarkdown(markdown: String): String {
    val lines = markdown.split("\n")
    val result = mutableListOf<String>()
    var consecutiveBlankLines = 0

    for (line in lines) {
        if (line.isEmpty()) {
            consecutiveBlankLines++
            if (consecutiveBlankLines >= 2) {
                result.add("<br />")
            } else {
                result.add("")
            }
        } else {
            if (consecutiveBlankLines >= 2) {
                result.add("")
            }
            consecutiveBlankLines = 0
            result.add(line)
        }
    }

    return result.joinToString("\n")
}

private object MarkdownProcessor {
    private val extensions = listOf(
        StrikethroughExtension.create(),
        TablesExtension.create(),
    )

    val parser: Parser = Parser.builder()
        .extensions(extensions)
        .build()

    val renderer: HtmlRenderer = HtmlRenderer.builder()
        .extensions(extensions)
        .softbreak("<br />\n")
        .build()
}
