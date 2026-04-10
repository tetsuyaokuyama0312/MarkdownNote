package com.to.markdownnote.feature.memo.editor

import com.to.markdownnote.core.common.io.OutputFileType

sealed interface DialogState {
    data object None : DialogState
    data object SaveConfirm : DialogState
    data object DeleteConfirm : DialogState
    data class FileOutput(val type: OutputFileType) : DialogState
}

data class MemoEditorUiState(
    val text: String = "",
    val previewHtml: String = "",
    val editorMode: EditorMode = EditorMode.SEPARATE,
    val isTextEdited: Boolean = false,
    val dialog: DialogState = DialogState.None,
    val savedFilePath: String? = null,
    val navigateBack: Boolean = false,
)
