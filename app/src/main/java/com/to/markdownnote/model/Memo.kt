package com.to.markdownnote.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * メモを表すデータクラス
 *
 * @property id ID(自動採番)
 * @property text テキスト
 * @property createdDate 作成日時
 * @property lastUpdatedDate 最終更新日時
 */
@Parcelize
@Entity
data class Memo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val text: String,
    @ColumnInfo(name = "created_date") val createdDate: Long,
    @ColumnInfo(name = "last_updated_date") val lastUpdatedDate: Long
) : Parcelable {
    constructor(text: String, createdDate: Long, lastUpdatedDate: Long) : this(
        0, text, createdDate, lastUpdatedDate
    )
}