package com.to.markdownnote.feature.memo.editor

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.to.markdownnote.core.common.io.OutputFileType
import com.to.markdownnote.core.common.io.writeTextFile
import com.to.markdownnote.core.common.util.nowTimestampSec
import com.to.markdownnote.core.common.util.parseMarkdownToHTML
import com.to.markdownnote.domain.model.Memo
import com.to.markdownnote.domain.usecase.DeleteMemoUseCase
import com.to.markdownnote.domain.usecase.GetMemoByIdUseCase
import com.to.markdownnote.domain.usecase.SaveMemoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val NEW_MEMO_ID = -1

@HiltViewModel
class MemoEditorViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getMemoById: GetMemoByIdUseCase,
    private val saveMemo: SaveMemoUseCase,
    private val deleteMemo: DeleteMemoUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MemoEditorUiState())
    val uiState = _uiState.asStateFlow()

    private var originalMemo: Memo? = null

    fun init(memoId: Int) {
        if (memoId == NEW_MEMO_ID) return
        viewModelScope.launch {
            val memo = getMemoById(memoId) ?: return@launch
            originalMemo = memo
            _uiState.update {
                it.copy(
                    text = memo.text,
                    previewHtml = parseMarkdownToHTML(memo.text),
                )
            }
        }
    }

    fun onTextChange(newText: String) {
        _uiState.update {
            it.copy(
                text = newText,
                previewHtml = parseMarkdownToHTML(newText),
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
            val path = writeTextFile(context, fileName, text)
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
