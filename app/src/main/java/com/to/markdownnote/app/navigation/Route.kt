package com.to.markdownnote.app.navigation

import kotlinx.serialization.Serializable

@Serializable
object MemoListRoute

@Serializable
data class MemoEditorRoute(val memoId: Int?)
