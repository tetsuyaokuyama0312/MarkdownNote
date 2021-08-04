package com.to.markdownnote.view

import android.content.Context
import android.widget.Button
import androidx.core.view.isVisible
import com.to.markdownnote.R
import com.to.markdownnote.model.Memo
import com.to.markdownnote.util.getFormattedDateTime
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.memo_row.view.*

class MemoRow(
    private val context: Context,
    val memo: Memo,
    private val onDeleteButtonClick: (MemoRow) -> Unit = {}
) :
    Item<GroupieViewHolder>() {
    private lateinit var deleteButton: Button

    var deleteButtonVisible: Boolean
        get() = deleteButton.isVisible
        set(visible) {
            deleteButton.isVisible = visible
        }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val firstLine = memo.text.split("\n")[0]
        val (date, time) = getFormattedDateTime(context, memo.lastUpdatedDate)

        val titleTextView = viewHolder.itemView.markdown_file_title_textview
        titleTextView.text = firstLine
        val dateTextView = viewHolder.itemView.date_textview_latest_message
        dateTextView.text = date
        val timeTextView = viewHolder.itemView.time_textview_latest_message
        timeTextView.text = time
        deleteButton = viewHolder.itemView.delete_button_memo_row
        deleteButton.setOnClickListener {
            onDeleteButtonClick(this)
        }
        deleteButtonVisible = false
    }

    override fun getLayout(): Int {
        return R.layout.memo_row
    }
}