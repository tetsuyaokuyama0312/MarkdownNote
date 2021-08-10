package com.to.markdownnote.util

import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test

class MarkdownParserTest {
    @Test
    fun test_parseMarkdown() {
        assertThat(
            parseMarkdownToHTML("Some *Markdown*"),
            equalTo("<p>Some <em>Markdown</em></p>\n")
        )
        assertThat(
            parseMarkdownToHTML("Some **Markdown**"),
            equalTo("<p>Some <strong>Markdown</strong></p>\n")
        )
    }
}