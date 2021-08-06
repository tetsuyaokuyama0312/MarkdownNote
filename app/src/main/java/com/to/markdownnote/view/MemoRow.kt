package com.to.markdownnote.view

import android.content.Context
import com.to.markdownnote.R
import com.to.markdownnote.databinding.MemoRowBinding
import com.to.markdownnote.model.Memo
import com.to.markdownnote.util.getFormattedDateTime
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class MemoRow(
    private val context: Context,
    val memo: Memo
) : Item<GroupieViewHolder>() {

    private lateinit var binding: MemoRowBinding

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        binding = MemoRowBinding.bind(viewHolder.itemView)

        val textLines = memo.text.split(System.lineSeparator()).filter { it.isNotBlank() }
        val textFirstLine = textLines.firstOrNull() ?: context.getString(R.string.new_memo)
        val textSecondLine =
            textLines.drop(1).firstOrNull() ?: context.getString(R.string.no_additional_text)
        val (date, time) = getFormattedDateTime(context, memo.lastUpdatedDate)

        binding.markdownFileTitleTextview.text = textFirstLine
        binding.memoSecondLineTextview.text = textSecondLine
        binding.dateTextviewLatestMessage.text = date
        binding.timeTextviewLatestMessage.text = time
    }

    override fun getLayout(): Int {
        return R.layout.memo_row
    }
}