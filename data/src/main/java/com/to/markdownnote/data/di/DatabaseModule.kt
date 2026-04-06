package com.to.markdownnote.data.di

import android.content.Context
import androidx.room.Room
import com.to.markdownnote.data.local.dao.MemoDao
import com.to.markdownnote.data.local.db.MarkdownNoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MarkdownNoteDatabase =
        Room.databaseBuilder(
            context,
            MarkdownNoteDatabase::class.java,
            "markdown_note_database",
        ).build()

    @Provides
    fun provideMemoDao(database: MarkdownNoteDatabase): MemoDao = database.memoDao()
}
