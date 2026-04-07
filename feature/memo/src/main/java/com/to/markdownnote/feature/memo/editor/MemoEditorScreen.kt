package com.to.markdownnote.feature.memo.editor

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.to.markdownnote.core.common.io.OutputFileType
import com.to.markdownnote.core.common.io.defaultFileName
import com.to.markdownnote.core.ui.component.ConfirmDialog
import com.to.markdownnote.core.ui.component.FileOutputDialog
import com.to.markdownnote.core.ui.component.MarkdownHtmlView
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

    LaunchedEffect(memoId) {
        viewModel.init(memoId)
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

    Scaffold(
        topBar = {
            EditorTopBar(
                editorMode = uiState.editorMode,
                onBack = { viewModel.onBackPressed() },
                onModeChange = viewModel::onModeChange,
                onComplete = viewModel::onCompleteClick,
                onDelete = viewModel::onShowDeleteDialog,
                onFileOutput = viewModel::onShowFileOutputDialog,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        EditorBody(
            text = uiState.text,
            previewHtml = uiState.previewHtml,
            editorMode = uiState.editorMode,
            onTextChange = viewModel::onTextChange,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditorTopBar(
    editorMode: EditorMode,
    onBack: () -> Unit,
    onModeChange: (EditorMode) -> Unit,
    onComplete: () -> Unit,
    onDelete: () -> Unit,
    onFileOutput: (OutputFileType) -> Unit,
) {
    var showOverflow by remember { mutableStateOf(false) }

    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        },
        actions = {
            // Mode buttons
            IconButton(
                onClick = { onModeChange(EditorMode.EDIT) },
                enabled = editorMode != EditorMode.EDIT,
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit),
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
            IconButton(
                onClick = { onModeChange(EditorMode.SEPARATE) },
                enabled = editorMode != EditorMode.SEPARATE,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_separate),
                    contentDescription = stringResource(R.string.separate),
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
            IconButton(
                onClick = { onModeChange(EditorMode.VIEW) },
                enabled = editorMode != EditorMode.VIEW,
            ) {
                Icon(
                    Icons.Default.Visibility,
                    contentDescription = stringResource(R.string.view),
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
            IconButton(onClick = onComplete) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = stringResource(R.string.complete),
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
            Box {
                IconButton(onClick = { showOverflow = true }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
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
            containerColor = MaterialTheme.colorScheme.primary,
        ),
    )
}

@Composable
private fun EditorBody(
    text: String,
    previewHtml: String,
    editorMode: EditorMode,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (editorMode) {
        EditorMode.EDIT -> MarkdownEditor(
            text = text,
            onTextChange = onTextChange,
            modifier = modifier.padding(8.dp),
        )
        EditorMode.VIEW -> MarkdownHtmlView(
            html = previewHtml,
            modifier = modifier,
        )
        EditorMode.SEPARATE -> Row(modifier = modifier) {
            MarkdownEditor(
                text = text,
                onTextChange = onTextChange,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(8.dp),
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
) {
    val scrollState = rememberScrollState()
    Box(modifier = modifier.verticalScroll(scrollState)) {
        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        )
    }
}
