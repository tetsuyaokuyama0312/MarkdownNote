package com.to.markdownnote.domain.repository

import com.to.markdownnote.domain.model.Memo
import kotlinx.coroutines.flow.Flow

interface MemoRepository {
    fun getAllMemos(): Flow<List<Memo>>
    fun searchMemos(query: String): Flow<List<Memo>>
    suspend fun getMemoById(id: Int): Memo?
    suspend fun saveMemo(memo: Memo): Long
    suspend fun deleteMemo(memo: Memo)
}
