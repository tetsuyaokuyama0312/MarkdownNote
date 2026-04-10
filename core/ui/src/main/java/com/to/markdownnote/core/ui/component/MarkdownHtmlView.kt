package com.to.markdownnote.core.ui.component

import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.to.markdownnote.core.ui.theme.MarkdownNoteTheme

@Composable
fun MarkdownHtmlView(
    html: String,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = false
                settings.builtInZoomControls = false
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
        },
        modifier = modifier,
    )
}

// ---- Previews ----

@Preview(showBackground = true, widthDp = 360, heightDp = 400)
@Composable
private fun MarkdownHtmlViewPreview() {
    MarkdownNoteTheme {
        MarkdownHtmlView(
            html = """
                <h1>タイトル</h1>
                <p>本文テキスト <em>斜体</em> <strong>太字</strong></p>
                <ul><li>リスト1</li><li>リスト2</li></ul>
            """.trimIndent(),
            modifier = Modifier.fillMaxSize(),
        )
    }
}
