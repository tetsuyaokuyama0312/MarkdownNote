package com.to.markdownnote.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import com.to.markdownnote.model.Memo

@Database(entities = [Memo::class], version = 1)
abstract class MarkdownNoteDatabase : RoomDatabase() {
    abstract fun memoRepository(): MemoRepository
}