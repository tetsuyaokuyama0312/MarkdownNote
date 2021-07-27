package com.to.markdownnote.repository

import androidx.room.*
import com.to.markdownnote.model.Memo

@Dao
interface MemoRepository {
    @Query("select * from memo where id = :id")
    fun select(id: Int): Memo

    @Query("select * from memo")
    fun selectAll(): List<Memo>

    @Insert
    fun insert(memo: Memo)

    @Update
    fun update(memo: Memo)

    @Delete
    fun delete(memo: Memo)
}