package com.to.markdownnote.feature.memo.list

import com.to.markdownnote.domain.model.Memo

data class MemoListUiState(
    val memos: List<Memo> = emptyList(),
    val query: String = "",
    val isLoading: Boolean = true,
    val pendingDeleteMemo: Memo? = null,
)
