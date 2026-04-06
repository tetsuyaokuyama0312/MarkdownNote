package com.to.markdownnote.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.to.markdownnote.data.local.entity.MemoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoDao {
    @Query("SELECT * FROM memo ORDER BY last_updated_date DESC")
    fun getAllMemos(): Flow<List<MemoEntity>>

    @Query("SELECT * FROM memo WHERE text LIKE '%' || :query || '%' ORDER BY last_updated_date DESC")
    fun searchMemos(query: String): Flow<List<MemoEntity>>

    @Query("SELECT * FROM memo WHERE id = :id")
    suspend fun getMemoById(id: Int): MemoEntity?

    @Upsert
    suspend fun upsert(memo: MemoEntity): Long

    @Delete
    suspend fun delete(memo: MemoEntity)
}
