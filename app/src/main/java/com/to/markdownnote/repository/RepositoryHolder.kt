package com.to.markdownnote.repository

import android.content.Context
import androidx.room.Room

private var db: MarkdownNoteDatabase? = null

private var memoRepository: MemoRepository? = null

@Synchronized
fun getMemoRepository(context: Context): MemoRepository {
    if (memoRepository == null) {
        memoRepository = getMDMemoDatabase(context).memoRepository()
    }
    return memoRepository!!
}

@Synchronized
private fun getMDMemoDatabase(context: Context): MarkdownNoteDatabase {
    if (db == null) {
        db = Room.databaseBuilder(context, MarkdownNoteDatabase::class.java, "mdmemo-db")
            .build()
    }
    return db!!
}