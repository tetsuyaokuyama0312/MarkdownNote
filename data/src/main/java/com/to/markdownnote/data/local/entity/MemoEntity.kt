package com.to.markdownnote.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memo")
data class MemoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val text: String,
    @ColumnInfo(name = "created_date") val createdDate: Long,
    @ColumnInfo(name = "last_updated_date") val lastUpdatedDate: Long,
)
