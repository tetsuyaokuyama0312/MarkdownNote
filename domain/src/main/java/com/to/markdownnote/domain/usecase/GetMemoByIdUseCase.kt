package com.to.markdownnote.domain.usecase

import com.to.markdownnote.domain.model.Memo
import com.to.markdownnote.domain.repository.MemoRepository
import javax.inject.Inject

class GetMemoByIdUseCase @Inject constructor(private val repository: MemoRepository) {
    suspend operator fun invoke(id: Int): Memo? = repository.getMemoById(id)
}
