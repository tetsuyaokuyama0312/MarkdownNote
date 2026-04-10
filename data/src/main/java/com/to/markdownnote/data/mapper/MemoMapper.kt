package com.to.markdownnote.data.mapper

import com.to.markdownnote.data.local.entity.MemoEntity
import com.to.markdownnote.domain.model.Memo

fun MemoEntity.toDomain(): Memo = Memo(
    id = id,
    text = text,
    createdDate = createdDate,
    lastUpdatedDate = lastUpdatedDate,
)

fun Memo.toEntity(): MemoEntity = MemoEntity(
    id = id,
    text = text,
    createdDate = createdDate,
    lastUpdatedDate = lastUpdatedDate,
)
