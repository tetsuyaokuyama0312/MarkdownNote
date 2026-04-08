package com.to.markdownnote.feature.memo.editor

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.to.markdownnote.core.common.io.OutputFileType
import com.to.markdownnote.core.common.io.defaultFileName
import com.to.markdownnote.core.ui.component.ConfirmDialog
import com.to.markdownnote.core.ui.component.FileOutputDialog
import com.to.markdownnote.core.ui.component.MarkdownHtmlView
import com.to.markdownnote.core.ui.theme.MarkdownNoteTheme
import com.to.markdownnote.feature.memo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoEditorScreen(
    memoId: Int?,
    onNavigateBack: () -> Unit,
    viewModel: MemoEditorViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val savedMessage = stringResource(R.string.saved_file_message)
    val textFieldFocusRequester = remember { FocusRequester() }

    LaunchedEffect(memoId) {
        viewModel.init(memoId)
    }

    LaunchedEffect(Unit) {
        if (memoId == null) {
            textFieldFocusRequester.requestFocus()
        }
    }

    LaunchedEffect(uiState.navigateBack) {
        if (uiState.navigateBack) {
            viewModel.onNavigateBackConsumed()
            onNavigateBack()
        }
    }

    LaunchedEffect(uiState.savedFilePath) {
        uiState.savedFilePath?.let { path ->
            snackbarHostState.showSnackbar(
                message = "$savedMessage\n$path",
                duration = SnackbarDuration.Long,
            )
            viewModel.onSavedFilePathConsumed()
        }
    }

    BackHandler(enabled = uiState.isTextEdited) {
        viewModel.onBackPressed()
    }

    // Dialogs
    if (uiState.showSaveConfirmDialog) {
        ConfirmDialog(
            message = stringResource(R.string.save_confirm_message),
            confirmLabel = stringResource(R.string.yes),
            dismissLabel = stringResource(R.string.no),
            cancelLabel = stringResource(R.string.cancel),
            onConfirm = { viewModel.onSaveConfirmed() },
            onDismiss = { viewModel.onDiscardChanges() },
            onCancel = { viewModel.onDialogDismissed() },
        )
    }

    if (uiState.showDeleteConfirmDialog) {
        ConfirmDialog(
            message = stringResource(R.string.delete_confirm_message),
            confirmLabel = stringResource(R.string.yes),
            dismissLabel = stringResource(R.string.no),
            onConfirm = { viewModel.onDeleteConfirmed() },
            onDismiss = { viewModel.onDialogDismissed() },
        )
    }

    if (uiState.showFileOutputDialog) {
        val type = uiState.fileOutputType ?: OutputFileType.PLAIN_TEXT
        FileOutputDialog(
            title = stringResource(R.string.file_output),
            defaultFileName = type.defaultFileName(),
            outputLabel = stringResource(R.string.output),
            cancelLabel = stringResource(R.string.cancel),
            fileNameLabel = stringResource(R.string.output_file_name),
            onOutput = { fileName -> viewModel.onFileOutputConfirmed(fileName) },
            onDismiss = { viewModel.onDialogDismissed() },
        )
    }

    val untitledMemo = stringResource(R.string.untitled_memo)
    val memoTitle = uiState.text.lines()
        .firstOrNull { it.isNotBlank() }
        ?.trimStart('#', ' ')
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?: untitledMemo

    Scaffold(
        topBar = {
            Column {
                EditorTopBar(
                    title = memoTitle,
                    onBack = { viewModel.onBackPressed() },
                    onComplete = viewModel::onCompleteClick,
                    onDelete = viewModel::onShowDeleteDialog,
                    onFileOutput = viewModel::onShowFileOutputDialog,
                )
                HorizontalDivider()
            }
        },
        bottomBar = {
            EditorModeBar(
                editorMode = uiState.editorMode,
                onModeChange = viewModel::onModeChange,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        EditorBody(
            text = uiState.text,
            previewHtml = uiState.previewHtml,
            editorMode = uiState.editorMode,
            onTextChange = viewModel::onTextChange,
            focusRequester = if (memoId == null) textFieldFocusRequester else null,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditorTopBar(
    title: String,
    onBack: () -> Unit,
    onComplete: () -> Unit,
    onDelete: () -> Unit,
    onFileOutput: (OutputFileType) -> Unit,
) {
    var showOverflow by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                )
            }
        },
        actions = {
            IconButton(onClick = onComplete) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = stringResource(R.string.complete),
                )
            }
            Box {
                IconButton(onClick = { showOverflow = true }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = null,
                    )
                }
                DropdownMenu(
                    expanded = showOverflow,
                    onDismissRequest = { showOverflow = false },
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.delete)) },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) },
                        onClick = {
                            showOverflow = false
                            onDelete()
                        },
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.plain_text)) },
                        onClick = {
                            showOverflow = false
                            onFileOutput(OutputFileType.PLAIN_TEXT)
                        },
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.markdown)) },
                        onClick = {
                            showOverflow = false
                            onFileOutput(OutputFileType.MARKDOWN)
                        },
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.html)) },
                        onClick = {
                            showOverflow = false
                            onFileOutput(OutputFileType.HTML)
                        },
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditorModeBar(
    editorMode: EditorMode,
    onModeChange: (EditorMode) -> Unit,
) {
    BottomAppBar {
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            SegmentedButton(
                selected = editorMode == EditorMode.EDIT,
                onClick = { onModeChange(EditorMode.EDIT) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3),
                icon = {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(SegmentedButtonDefaults.IconSize),
                    )
                },
            ) {
                Text(stringResource(R.string.edit))
            }
            SegmentedButton(
                selected = editorMode == EditorMode.SEPARATE,
                onClick = { onModeChange(EditorMode.SEPARATE) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3),
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_separate),
                        contentDescription = null,
                        modifier = Modifier.size(SegmentedButtonDefaults.IconSize),
                    )
                },
            ) {
                Text(stringResource(R.string.separate))
            }
            SegmentedButton(
                selected = editorMode == EditorMode.VIEW,
                onClick = { onModeChange(EditorMode.VIEW) },
                shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3),
                icon = {
                    Icon(
                        Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(SegmentedButtonDefaults.IconSize),
                    )
                },
            ) {
                Text(stringResource(R.string.view))
            }
        }
    }
}

@Composable
private fun EditorBody(
    text: String,
    previewHtml: String,
    editorMode: EditorMode,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
) {
    when (editorMode) {
        EditorMode.EDIT -> MarkdownEditor(
            text = text,
            onTextChange = onTextChange,
            modifier = modifier.padding(12.dp),
            focusRequester = focusRequester,
        )
        EditorMode.VIEW -> MarkdownHtmlView(
            html = previewHtml,
            modifier = modifier.padding(horizontal = 12.dp),
        )
        EditorMode.SEPARATE -> Row(modifier = modifier) {
            MarkdownEditor(
                text = text,
                onTextChange = onTextChange,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(12.dp),
                focusRequester = focusRequester,
            )
            VerticalDivider()
            MarkdownHtmlView(
                html = previewHtml,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            )
        }
    }
}

@Composable
private fun MarkdownEditor(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
) {
    val scrollState = rememberScrollState()
    val hint = stringResource(R.string.editor_hint)
    Box(modifier = modifier.verticalScroll(scrollState)) {
        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .then(focusRequester?.let { Modifier.focusRequester(it) } ?: Modifier),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { innerTextField ->
                Box {
                    if (text.isEmpty()) {
                        Text(
                            text = hint,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                        )
                    }
                    innerTextField()
                }
            },
        )
    }
}

// ---- Previews ----

@Preview
@Composable
private fun EditorTopBarPreview() {
    MarkdownNoteTheme {
        EditorTopBar(
            title = "タイトルテキスト",
            onBack = {},
            onComplete = {},
            onDelete = {},
            onFileOutput = {},
        )
    }
}

@Preview
@Composable
private fun EditorTopBarUntitledPreview() {
    MarkdownNoteTheme {
        EditorTopBar(
            title = "無題のメモ",
            onBack = {},
            onComplete = {},
            onDelete = {},
            onFileOutput = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, widthDp = 360)
@Composable
private fun EditorModeBarEditPreview() {
    MarkdownNoteTheme {
        EditorModeBar(editorMode = EditorMode.EDIT, onModeChange = {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, widthDp = 360)
@Composable
private fun EditorModeBarSeparatePreview() {
    MarkdownNoteTheme {
        EditorModeBar(editorMode = EditorMode.SEPARATE, onModeChange = {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, widthDp = 360)
@Composable
private fun EditorModeBarViewPreview() {
    MarkdownNoteTheme {
        EditorModeBar(editorMode = EditorMode.VIEW, onModeChange = {})
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 600)
@Composable
private fun EditorBodyEditPreview() {
    MarkdownNoteTheme {
        EditorBody(
            text = "# タイトル\n\n本文テキスト\n\n- リスト1\n- リスト2",
            previewHtml = "<h1>タイトル</h1><p>本文テキスト</p>",
            editorMode = EditorMode.EDIT,
            onTextChange = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 600)
@Composable
private fun EditorBodyViewPreview() {
    MarkdownNoteTheme {
        EditorBody(
            text = "# タイトル\n\n本文テキスト",
            previewHtml = "<h1>タイトル</h1><p>本文テキスト</p>",
            editorMode = EditorMode.VIEW,
            onTextChange = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 600)
@Composable
private fun EditorBodySeparatePreview() {
    MarkdownNoteTheme {
        EditorBody(
            text = "# タイトル\n\n本文テキスト\n\n- リスト1\n- リスト2",
            previewHtml = "<h1>タイトル</h1><p>本文テキスト</p>",
            editorMode = EditorMode.SEPARATE,
            onTextChange = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 300)
@Composable
private fun MarkdownEditorPreview() {
    MarkdownNoteTheme {
        MarkdownEditor(
            text = "# タイトル\n\n本文テキスト\n\n**太字** と *斜体* のサンプル",
            onTextChange = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 300)
@Composable
private fun MarkdownEditorEmptyPreview() {
    MarkdownNoteTheme {
        MarkdownEditor(
            text = "",
            onTextChange = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
