package com.to.markdownnote.view

import android.content.Context
import com.to.markdownnote.R
import com.to.markdownnote.model.Memo
import com.to.markdownnote.util.getFormattedDateTime
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.memo_row.view.*

class MemoRow(
    private val context: Context,
    val memo: Memo
) : Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val textLines = memo.text.split(System.lineSeparator()).filter { it.isNotBlank() }
        val textFirstLine = textLines.firstOrNull() ?: context.getString(R.string.new_memo)
        val textSecondLine =
            textLines.drop(1).firstOrNull() ?: context.getString(R.string.no_additional_text)
        val (date, time) = getFormattedDateTime(context, memo.lastUpdatedDate)

        val titleTextView = viewHolder.itemView.markdown_file_title_textview
        titleTextView.text = textFirstLine
        val secondLineTextView = viewHolder.itemView.memo_second_line_textview
        secondLineTextView.text = textSecondLine
        val dateTextView = viewHolder.itemView.date_textview_latest_message
        dateTextView.text = date
        val timeTextView = viewHolder.itemView.time_textview_latest_message
        timeTextView.text = time
    }

    override fun getLayout(): Int {
        return R.layout.memo_row
    }
}