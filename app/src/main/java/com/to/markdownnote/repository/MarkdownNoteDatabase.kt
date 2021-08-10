package com.to.markdownnote.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import com.to.markdownnote.model.Memo

/**
 * MarkdownNoteのRoomDatabase
 */
@Database(entities = [Memo::class], version = 1, exportSchema = false)
abstract class MarkdownNoteDatabase : RoomDatabase() {
    abstract fun memoRepository(): MemoRepository
}