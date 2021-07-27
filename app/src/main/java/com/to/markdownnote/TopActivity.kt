package com.to.markdownnote

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
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
    private val memoListAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerview_text_list.adapter = memoListAdapter
        recyclerview_text_list.isFocusable = false
        recyclerview_text_list.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

        loadMemoList()

        memoListAdapter.setOnItemClickListener { item, view ->
            val memoRow = item as MemoRow
            startActivity(EditorActivity.createIntent(view.context, memoRow.memo))
        }

        new_memo_fab.setOnClickListener {
            startActivity(EditorActivity.createIntent(it.context))
        }
    }

    private fun loadMemoList() {
        memoListAdapter.clear()

        selectAllMemo(this) { memoList ->
            memoList.sortedByDescending { it.lastUpdatedDate }
                .forEach {
                    logDebug("Select memo: $it")
                    memoListAdapter.add(MemoRow(this, it))
                }
        }
    }
}