package com.to.markdownnote.domain.usecase

import com.to.markdownnote.domain.model.Memo
import com.to.markdownnote.domain.repository.MemoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchMemosUseCase @Inject constructor(private val repository: MemoRepository) {
    operator fun invoke(query: String): Flow<List<Memo>> = repository.searchMemos(query)
}
