package com.to.markdownnote

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.to.markdownnote.repository.deleteMemo
import com.to.markdownnote.repository.selectAllMemo
import com.to.markdownnote.util.logDebug
import com.to.markdownnote.view.MemoRow
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_main.*

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

        loadAllMemo()

        memoRowAdapter.setOnItemClickListener { item, view ->
            val memoRow = item as MemoRow
            startActivity(EditorActivity.createIntent(view.context, memoRow.memo))
        }

        // スワイプのコールバック設定
        val swipeCallback = object : SwipeToDeleteCallback(this@TopActivity) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val memoRow = memoRowAdapter.getItem(position) as MemoRow
                showDeleteConfirmDialog(memoRow, position)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeCallback)
        itemTouchHelper.attachToRecyclerView(recyclerview_text_list)

        new_memo_fab.setOnClickListener {
            startActivity(EditorActivity.createIntent(it.context))
        }
    }

    private fun loadAllMemo() {
        memoRowAdapter.clear()

        selectAllMemo(this) { memoList ->
            memoList.sortedByDescending { it.lastUpdatedDate }
                .forEach {
                    logDebug("Select memo: $it")
                    memoRowAdapter.add(MemoRow(this, it))
                }
        }
    }

    private fun showDeleteConfirmDialog(memoRow: MemoRow, memoRowPos: Int) {
        val memo = memoRow.memo
        // 削除確認ダイアログを起動
        val dialog = newDeleteConfirmDialogFragment(this, {
            // メモを削除
            deleteMemo(this, memo) {
                logDebug("Deleted memo=[$memo]")
                memoRowAdapter.remove(memoRow)
            }
        }, {
            // レコードのスワイプを元に戻す
            memoRowAdapter.notifyItemChanged(memoRowPos)
        })
        dialog.show(supportFragmentManager, dialog::class.simpleName)
    }
}