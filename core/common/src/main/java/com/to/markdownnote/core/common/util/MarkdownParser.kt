package com.to.markdownnote.core.common.util

import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

fun String.toMarkdownHtml(): String {
    val document = MarkdownProcessor.parser.parse(preprocess())
    return MarkdownProcessor.renderer.render(document)
}

private fun String.preprocess(): String {
    val lines = split("\n")
    val result = mutableListOf<String>()
    var consecutiveBlankLines = 0

    lines.forEach { line ->
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
