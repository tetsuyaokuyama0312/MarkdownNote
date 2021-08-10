package com.to.markdownnote.repository

import androidx.room.*
import com.to.markdownnote.model.Memo

/**
 * MemoテーブルのDAO
 */
@Dao
interface MemoRepository {

    /**
     * 指定されたIDのメモを取得する。
     *
     * @param id ID
     * @return メモ
     */
    @Query("select * from memo where id = :id")
    fun select(id: Int): Memo

    /**
     * メモを全件取得する。
     *
     * @return 全てのメモを保持するリスト
     */
    @Query("select * from memo")
    fun selectAll(): List<Memo>

    /**
     * 指定されたメモを新規登録する。
     *
     * @param memo メモ
     */
    @Insert
    fun insert(memo: Memo)

    /**
     * 指定されたメモを更新する。
     *
     * @param memo メモ
     */
    @Update
    fun update(memo: Memo)

    /**
     * 指定されたメモを削除する。
     *
     * @param memo メモ
     */
    @Delete
    fun delete(memo: Memo)
}