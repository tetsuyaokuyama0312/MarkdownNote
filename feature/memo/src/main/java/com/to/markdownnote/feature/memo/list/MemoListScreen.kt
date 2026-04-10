package com.to.markdownnote.feature.memo.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
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
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    uiState.pendingDeleteMemo?.let {
        ConfirmDialog(
            message = stringResource(R.string.delete_confirm_message),
            confirmLabel = stringResource(R.string.yes),
            dismissLabel = stringResource(R.string.no),
            onConfirm = { viewModel.onDeleteConfirmed() },
            onDismiss = { viewModel.onDeleteDismissed() },
        )
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                ),
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNewMemo,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(stringResource(R.string.new_memo)) },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            MemoSearchBar(
                query = uiState.query,
                onQueryChange = viewModel::onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )

            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                uiState.memos.isEmpty() -> {
                    EmptyState(
                        hasQuery = uiState.query.isNotBlank(),
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 4.dp, bottom = 88.dp),
                    ) {
                        items(uiState.memos, key = { it.id }) { memo ->
                            MemoListItem(
                                memo = memo,
                                onMemoClick = { onMemoClick(memo) },
                                onSwipeDelete = { viewModel.onSwipeDelete(memo) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(
    hasQuery: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = if (hasQuery) Icons.Default.Search else Icons.Default.Edit,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = if (hasQuery) stringResource(R.string.empty_search_result)
                   else stringResource(R.string.empty_memo_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (!hasQuery) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.empty_memo_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp),
            )
        }
    }
}

@Composable
private fun MemoSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
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
        shape = RoundedCornerShape(50.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
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
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp)),
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.error),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        color = MaterialTheme.colorScheme.onError,
                        style = MaterialTheme.typography.labelLarge,
                    )
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete),
                        tint = MaterialTheme.colorScheme.onError,
                    )
                }
            }
        },
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 2.dp,
        ) {
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
            .padding(horizontal = 12.dp, vertical = 10.dp),
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
        Spacer(Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text(text = dateStr, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = timeStr, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
private fun EmptyStateMemoPreview() {
    MarkdownNoteTheme {
        EmptyState(
            hasQuery = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun EmptyStateSearchPreview() {
    MarkdownNoteTheme {
        EmptyState(
            hasQuery = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun MemoSearchBarEmptyPreview() {
    MarkdownNoteTheme {
        MemoSearchBar(
            query = "",
            onQueryChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun MemoSearchBarWithQueryPreview() {
    MarkdownNoteTheme {
        MemoSearchBar(
            query = "検索テキスト",
            onQueryChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )
    }
}
