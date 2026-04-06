package com.to.markdownnote.data.repository

import com.to.markdownnote.data.local.dao.MemoDao
import com.to.markdownnote.data.mapper.toDomain
import com.to.markdownnote.data.mapper.toEntity
import com.to.markdownnote.domain.model.Memo
import com.to.markdownnote.domain.repository.MemoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MemoRepositoryImpl @Inject constructor(
    private val dao: MemoDao,
) : MemoRepository {

    override fun getAllMemos(): Flow<List<Memo>> =
        dao.getAllMemos().map { list -> list.map { it.toDomain() } }

    override fun searchMemos(query: String): Flow<List<Memo>> =
        dao.searchMemos(query).map { list -> list.map { it.toDomain() } }

    override suspend fun getMemoById(id: Int): Memo? =
        dao.getMemoById(id)?.toDomain()

    override suspend fun saveMemo(memo: Memo): Long =
        dao.upsert(memo.toEntity())

    override suspend fun deleteMemo(memo: Memo) =
        dao.delete(memo.toEntity())
}
