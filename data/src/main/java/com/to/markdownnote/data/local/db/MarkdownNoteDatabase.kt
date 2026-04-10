package com.to.markdownnote.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.to.markdownnote.data.local.dao.MemoDao
import com.to.markdownnote.data.local.entity.MemoEntity

@Database(entities = [MemoEntity::class], version = 1, exportSchema = false)
abstract class MarkdownNoteDatabase : RoomDatabase() {
    abstract fun memoDao(): MemoDao
}
