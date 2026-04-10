package com.to.markdownnote.domain.model

data class Memo(
    val id: Int = 0,
    val text: String,
    val createdDate: Long,
    val lastUpdatedDate: Long,
)
