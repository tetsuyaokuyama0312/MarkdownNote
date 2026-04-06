package com.to.markdownnote.domain.usecase

import com.to.markdownnote.domain.model.Memo
import com.to.markdownnote.domain.repository.MemoRepository
import javax.inject.Inject

class SaveMemoUseCase @Inject constructor(private val repository: MemoRepository) {
    suspend operator fun invoke(memo: Memo): Long = repository.saveMemo(memo)
}
