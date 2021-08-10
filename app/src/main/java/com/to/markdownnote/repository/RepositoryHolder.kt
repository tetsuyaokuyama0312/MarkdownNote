package com.to.markdownnote.repository

import android.content.Context
import androidx.room.Room

private const val DATABASE_NAME = "markdownnote-db"

private var db: MarkdownNoteDatabase? = null

private var memoRepository: MemoRepository? = null

/**
 * MemoRepositoryを取得する。
 *
 * @param context コンテキスト
 * @return MemoRepository
 */
@Synchronized
fun getMemoRepository(context: Context): MemoRepository {
    if (memoRepository == null) {
        memoRepository = getMarkdownNoteDatabase(context).memoRepository()
    }
    return memoRepository!!
}

@Synchronized
private fun getMarkdownNoteDatabase(context: Context): MarkdownNoteDatabase {
    if (db == null) {
        db = Room.databaseBuilder(context, MarkdownNoteDatabase::class.java, DATABASE_NAME).build()
    }
    return db!!
}