package com.to.markdownnote.domain.usecase

import com.to.markdownnote.domain.model.Memo
import com.to.markdownnote.domain.repository.MemoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllMemosUseCase @Inject constructor(private val repository: MemoRepository) {
    operator fun invoke(): Flow<List<Memo>> = repository.getAllMemos()
}
