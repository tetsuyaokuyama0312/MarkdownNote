package com.to.markdownnote.feature.memo.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.to.markdownnote.core.common.io.FileAccessor
import com.to.markdownnote.core.common.io.OutputFileType
import com.to.markdownnote.core.common.util.nowTimestampSec
import com.to.markdownnote.core.common.util.toMarkdownHtml
import com.to.markdownnote.domain.model.Memo
import com.to.markdownnote.domain.usecase.DeleteMemoUseCase
import com.to.markdownnote.domain.usecase.GetMemoByIdUseCase
import com.to.markdownnote.domain.usecase.SaveMemoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemoEditorViewModel @Inject constructor(
    private val fileAccessor: FileAccessor,
    private val getMemoById: GetMemoByIdUseCase,
    private val saveMemo: SaveMemoUseCase,
    private val deleteMemo: DeleteMemoUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MemoEditorUiState())
    val uiState = _uiState.asStateFlow()

    private var originalMemo: Memo? = null

    fun init(memoId: Int?) {
        if (memoId == null) return
        viewModelScope.launch {
            val memo = getMemoById(memoId) ?: return@launch
            originalMemo = memo
            _uiState.update {
                it.copy(
                    text = memo.text,
                    previewHtml = memo.text.toMarkdownHtml(),
                )
            }
        }
    }

    fun onTextChange(newText: String) {
        _uiState.update {
            it.copy(
                text = newText,
                previewHtml = newText.toMarkdownHtml(),
                isTextEdited = true,
            )
        }
    }

    fun onModeChange(mode: EditorMode) {
        _uiState.update { it.copy(editorMode = mode) }
    }

    fun onCompleteClick() {
        val state = _uiState.value
        if (!state.isTextEdited) {
            _uiState.update { it.copy(navigateBack = true) }
            return
        }
        viewModelScope.launch {
            val text = state.text
            val now = nowTimestampSec()
            when {
                originalMemo == null && text.isEmpty() -> {
                    _uiState.update { it.copy(navigateBack = true) }
                }
                originalMemo == null -> {
                    saveMemo(Memo(text = text, createdDate = now, lastUpdatedDate = now))
                    _uiState.update { it.copy(navigateBack = true) }
                }
                text.isEmpty() -> {
                    deleteMemo(originalMemo!!)
                    _uiState.update { it.copy(navigateBack = true) }
                }
                else -> {
                    saveMemo(originalMemo!!.copy(text = text, lastUpdatedDate = now))
                    _uiState.update { it.copy(navigateBack = true) }
                }
            }
        }
    }

    fun onBackPressed() {
        if (_uiState.value.isTextEdited) {
            _uiState.update { it.copy(showSaveConfirmDialog = true) }
        } else {
            _uiState.update { it.copy(navigateBack = true) }
        }
    }

    fun onSaveConfirmed() {
        _uiState.update { it.copy(showSaveConfirmDialog = false) }
        onCompleteClick()
    }

    fun onDiscardChanges() {
        _uiState.update { it.copy(showSaveConfirmDialog = false, navigateBack = true) }
    }

    fun onShowDeleteDialog() {
        _uiState.update { it.copy(showDeleteConfirmDialog = true) }
    }

    fun onDeleteConfirmed() {
        viewModelScope.launch {
            originalMemo?.let { deleteMemo(it) }
            _uiState.update { it.copy(showDeleteConfirmDialog = false, navigateBack = true) }
        }
    }

    fun onShowFileOutputDialog(type: OutputFileType) {
        _uiState.update {
            it.copy(showFileOutputDialog = true, fileOutputType = type)
        }
    }

    fun onFileOutputConfirmed(fileName: String) {
        val type = _uiState.value.fileOutputType ?: return
        val text = type.convert(_uiState.value.text)
        viewModelScope.launch {
            val path = fileAccessor.writeTextFile(fileName, text)
            _uiState.update {
                it.copy(showFileOutputDialog = false, fileOutputType = null, savedFilePath = path)
            }
        }
    }

    fun onDialogDismissed() {
        _uiState.update {
            it.copy(
                showSaveConfirmDialog = false,
                showDeleteConfirmDialog = false,
                showFileOutputDialog = false,
                fileOutputType = null,
            )
        }
    }

    fun onSavedFilePathConsumed() {
        _uiState.update { it.copy(savedFilePath = null) }
    }

    fun onNavigateBackConsumed() {
        _uiState.update { it.copy(navigateBack = false) }
    }
}
