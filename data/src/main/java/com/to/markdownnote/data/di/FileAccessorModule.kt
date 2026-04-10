package com.to.markdownnote.data.di

import android.content.Context
import com.to.markdownnote.core.common.io.FileAccessor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FileAccessorModule {

    @Provides
    @Singleton
    fun provideFileAccessor(@ApplicationContext context: Context): FileAccessor =
        FileAccessor(context)
}
