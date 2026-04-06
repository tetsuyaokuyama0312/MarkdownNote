package com.to.markdownnote.feature.memo.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.to.markdownnote.domain.model.Memo
import com.to.markdownnote.domain.usecase.DeleteMemoUseCase
import com.to.markdownnote.domain.usecase.GetAllMemosUseCase
import com.to.markdownnote.domain.usecase.SearchMemosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemoListViewModel @Inject constructor(
    private val getAllMemos: GetAllMemosUseCase,
    private val searchMemos: SearchMemosUseCase,
    private val deleteMemo: DeleteMemoUseCase,
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val pendingDeleteMemo = MutableStateFlow<Memo?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = combine(
        query.flatMapLatest { q ->
            if (q.isBlank()) getAllMemos() else searchMemos(q)
        },
        query,
        pendingDeleteMemo,
    ) { memos, currentQuery, pendingDelete ->
        MemoListUiState(
            memos = memos,
            query = currentQuery,
            isLoading = false,
            pendingDeleteMemo = pendingDelete,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MemoListUiState(),
    )

    fun onQueryChange(newQuery: String) {
        query.value = newQuery
    }

    fun onSwipeDelete(memo: Memo) {
        pendingDeleteMemo.value = memo
    }

    fun onDeleteConfirmed() {
        val memo = pendingDeleteMemo.value ?: return
        viewModelScope.launch {
            deleteMemo(memo)
        }
        pendingDeleteMemo.value = null
    }

    fun onDeleteDismissed() {
        pendingDeleteMemo.value = null
    }
}
