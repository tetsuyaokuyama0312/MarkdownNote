package com.to.markdownnote

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.to.markdownnote.repository.deleteMemo
import com.to.markdownnote.repository.selectAllMemo
import com.to.markdownnote.util.logDebug
import com.to.markdownnote.view.MemoRow
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.memo_row.*

class TopActivity : AppCompatActivity() {
    companion object {
        /**
         * `TopActivity`を起動するためのIntentを作成する。
         *
         * @param context コンテキスト
         * @return `TopActivity`を起動するためのIntent
         */
        fun createIntent(context: Context): Intent {
            return Intent(context, TopActivity::class.java)
        }
    }

    private var memoRowList: MutableList<MemoRow> = mutableListOf()

    /** メモリストのAdapter */
    private val memoRowAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerview_text_list.adapter = memoRowAdapter
        recyclerview_text_list.isFocusable = false
        recyclerview_text_list.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

        loadMemoList()

        memoRowAdapter.setOnItemClickListener { item, view ->
            if (isAnyDeleteButtonVisible()) {
                setAllDeleteButtonInvisible()
            } else {
                val memoRow = item as MemoRow
                startActivity(EditorActivity.createIntent(view.context, memoRow.memo))
            }
        }
        memoRowAdapter.setOnItemLongClickListener { item, _ ->
            val memoRow = item as MemoRow
            memoRow.deleteButtonVisible = !memoRow.deleteButtonVisible
            return@setOnItemLongClickListener true
        }

        new_memo_fab.setOnClickListener {
            startActivity(EditorActivity.createIntent(it.context))
        }
    }

    private fun loadMemoList() {
        memoRowAdapter.clear()

        selectAllMemo(this) { memoList ->
            memoList.sortedByDescending { it.lastUpdatedDate }
                .forEach {
                    logDebug("Select memo: $it")
                    val memoRow = MemoRow(this, it, ::showDeleteConfirmDialog)
                    memoRowList.add(memoRow)
                    memoRowAdapter.add(memoRow)
                }
        }
    }

    private fun showDeleteConfirmDialog(memoRow: MemoRow) {
        val memo = memoRow.memo
        // 削除確認ダイアログを起動
        val dialog = newDeleteConfirmDialogFragment(this, {
            deleteMemo(this, memo) {
                logDebug("Deleted memo=[$memo]")
                memoRowAdapter.remove(memoRow)
            }
        })
        dialog.show(supportFragmentManager, dialog::class.simpleName)
    }

    private fun isAnyDeleteButtonVisible(): Boolean {
        return memoRowList.any { it.deleteButtonVisible }
    }

    private fun setAllDeleteButtonInvisible() {
        memoRowList.forEach {
            it.deleteButtonVisible = false
        }
    }
}