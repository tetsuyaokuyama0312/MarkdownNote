package com.to.markdownnote.feature.memo.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.to.markdownnote.core.common.util.toFormattedDateTime
import com.to.markdownnote.core.ui.component.ConfirmDialog
import com.to.markdownnote.core.ui.theme.MarkdownNoteTheme
import com.to.markdownnote.domain.model.Memo
import com.to.markdownnote.feature.memo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoListScreen(
    onMemoClick: (Memo) -> Unit,
    onNewMemo: () -> Unit,
    viewModel: MemoListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    uiState.pendingDeleteMemo?.let { memo ->
        ConfirmDialog(
            message = stringResource(R.string.delete_confirm_message),
            confirmLabel = stringResource(R.string.yes),
            dismissLabel = stringResource(R.string.no),
            onConfirm = { viewModel.onDeleteConfirmed() },
            onDismiss = { viewModel.onDeleteDismissed() },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNewMemo) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.new_memo))
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            SearchBar(
                query = uiState.query,
                onQueryChange = viewModel::onQueryChange,
                modifier = Modifier.fillMaxWidth(),
            )

            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(uiState.memos, key = { it.id }) { memo ->
                            MemoListItem(
                                memo = memo,
                                onMemoClick = { onMemoClick(memo) },
                                onSwipeDelete = { viewModel.onSwipeDelete(memo) },
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text(stringResource(R.string.search_hint)) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = null)
                }
            }
        },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
        ),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MemoListItem(
    memo: Memo,
    onMemoClick: () -> Unit,
    onSwipeDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onSwipeDelete()
            }
            false // always snap back; deletion is handled via dialog
        },
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.error),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = MaterialTheme.colorScheme.onError,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        },
    ) {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
            MemoItemContent(memo = memo, onClick = onMemoClick)
        }
    }
}

@Composable
private fun MemoItemContent(
    memo: Memo,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
    val lines = memo.text.lines()
    val firstLine = lines.firstOrNull().orEmpty()
    val secondLine = lines.drop(1).firstOrNull { it.isNotBlank() }
    val (dateStr, timeStr) = memo.lastUpdatedDate.toFormattedDateTime(context)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = firstLine.ifEmpty { stringResource(R.string.no_additional_text) },
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (secondLine != null) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = secondLine,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(text = dateStr, fontSize = 12.sp)
            Text(text = timeStr, fontSize = 12.sp)
        }
    }
}

// ---- Previews ----

private val previewMemo = Memo(
    id = 1,
    text = "タイトルテキスト\n本文の2行目です。少し長めのテキストを入れてみます。",
    createdDate = 1_700_000_000L,
    lastUpdatedDate = 1_700_000_000L,
)

private val previewMemoSingleLine = Memo(
    id = 2,
    text = "1行だけのメモです",
    createdDate = 1_700_000_000L,
    lastUpdatedDate = 1_700_000_000L,
)

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun MemoItemContentPreview() {
    MarkdownNoteTheme {
        MemoItemContent(memo = previewMemo, onClick = {})
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun MemoItemContentSingleLinePreview() {
    MarkdownNoteTheme {
        MemoItemContent(memo = previewMemoSingleLine, onClick = {})
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun MemoListItemPreview() {
    MarkdownNoteTheme {
        MemoListItem(
            memo = previewMemo,
            onMemoClick = {},
            onSwipeDelete = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun SearchBarEmptyPreview() {
    MarkdownNoteTheme {
        SearchBar(
            query = "",
            onQueryChange = {},
            modifier = Modifier.width(360.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun SearchBarWithQueryPreview() {
    MarkdownNoteTheme {
        SearchBar(
            query = "検索テキスト",
            onQueryChange = {},
            modifier = Modifier.width(360.dp),
        )
    }
}
