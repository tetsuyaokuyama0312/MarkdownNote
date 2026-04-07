package com.to.markdownnote.feature.memo.editor

import com.to.markdownnote.core.common.io.OutputFileType

data class MemoEditorUiState(
    val text: String = "",
    val previewHtml: String = "",
    val editorMode: EditorMode = EditorMode.SEPARATE,
    val isTextEdited: Boolean = false,
    val showSaveConfirmDialog: Boolean = false,
    val showDeleteConfirmDialog: Boolean = false,
    val showFileOutputDialog: Boolean = false,
    val fileOutputType: OutputFileType? = null,
    val savedFilePath: String? = null,
    val navigateBack: Boolean = false,
)
