package com.to.markdownnote.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.to.markdownnote.activity.dialog.newDeleteConfirmDialogFragment
import com.to.markdownnote.databinding.ActivityTopBinding
import com.to.markdownnote.model.Memo
import com.to.markdownnote.repository.deleteMemo
import com.to.markdownnote.repository.selectAllMemo
import com.to.markdownnote.util.logDebug
import com.to.markdownnote.view.MemoRow
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder


/**
 * Top画面のアクティビティ
 */
class TopActivity : AppCompatActivity() {
    companion object {
        /** 空白文字の正規表現 */
        private val SPACE_REGEX = "\\s+".toRegex()

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

    /** TopActivityのViewBinding */
    private lateinit var binding: ActivityTopBinding

    /** 保存済みメモのAdapter */
    private val memoRowAdapter = GroupAdapter<GroupieViewHolder>()

    /** 保存済みの全メモを保持するリスト */
    private val allMemoList: MutableList<Memo> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.memoListRecyclerView.adapter = memoRowAdapter
        binding.memoListRecyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.memoListRecyclerView.addOnItemTouchListener(
            object : RecyclerView.OnItemTouchListener {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    exitSearch()
                    // メモタップ時の画面遷移やスワイプ時の削除処理を続行するためfalse
                    return false
                }

                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
            }
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
        itemTouchHelper.attachToRecyclerView(binding.memoListRecyclerView)

        binding.newMemoFab.setOnClickListener {
            startActivity(EditorActivity.createIntent(it.context))
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                val text = s.toString()
                filterMemo(text)

                binding.searchTextClearButton.isVisible = text.isNotEmpty()
            }
        })

        binding.searchTextClearButton.setOnClickListener { binding.searchEditText.text.clear() }
    }

    override fun onResume() {
        super.onResume()

        if (binding.searchEditText.text.isNotEmpty()) {
            enterSearch()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        exitSearch()
        return true
    }

    /**
     * 保存済みの全メモをロードする。
     */
    private fun loadAllMemo() {
        // メモをロードし、allMemoListを初期化する
        selectAllMemo(this) { memoList ->
            memoList.sortedByDescending { memo -> memo.lastUpdatedDate }
                .forEach { memo ->
                    logDebug("Select memo: $memo")
                    allMemoList.add(memo)
                }

            // 初期化が終わったら表示
            displayMemo(allMemoList)
        }
    }

    /**
     * 指定されたテキストでメモをフィルタリングする
     *
     * @param filterText フィルタリングに使用するテキスト
     */
    private fun filterMemo(filterText: String) {
        // 空白文字だけの場合はフィルタリングしない
        if (filterText.isBlank()) {
            displayMemo(allMemoList)
            return
        }

        logDebug("Filter memo by $filterText")

        // 大文字小文字を区別せずにメモをフィルタリング
        val filterTextList = filterText.split(SPACE_REGEX).map { it.toLowerCase() }

        val filteredMemoList = allMemoList.filter { memo ->
            val memoText = memo.text.toLowerCase()

            filterTextList.all { filterText ->
                filterText in memoText
            }
        }

        // フィルタリング結果を表示
        displayMemo(filteredMemoList)
    }

    /**
     * メモを表示する
     *
     * @param memoList 表示するメモリスト
     */
    private fun displayMemo(memoList: List<Memo>) {
        memoRowAdapter.clear()
        memoList.sortedByDescending { memo -> memo.lastUpdatedDate }
            .forEach { memo -> memoRowAdapter.add(MemoRow(this, memo)) }
    }

    /**
     * 削除確認ダイアログを表示する。
     *
     * @param memoRow 削除対象メモのMemoRow
     * @param memoRowPos memoRowのAdapter内での位置
     */
    private fun showDeleteConfirmDialog(memoRow: MemoRow, memoRowPos: Int) {
        val memo = memoRow.memo
        // 削除確認ダイアログを起動
        val dialog = newDeleteConfirmDialogFragment(this, onPositiveClick = {
            // メモを削除
            deleteMemo(this, memo) {
                logDebug("Deleted memo=[$memo]")
                memoRowAdapter.remove(memoRow)
            }
        }, onNegativeClick = {
            // レコードのスワイプを元に戻す
            memoRowAdapter.notifyItemChanged(memoRowPos)
        })
        dialog.show(supportFragmentManager, dialog::class.simpleName)
    }

    /**
     * 検索動作に入る
     */
    private fun enterSearch() {
        binding.searchEditText.postDelayed({
            // テキストにフォーカスを移す
            binding.searchEditText.requestFocus()

            // キーボードを表示する
            val inputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(
                binding.searchEditText,
                InputMethodManager.SHOW_IMPLICIT
            )
        }, 100) // 遅延なしだと正しく表示されない
    }

    /**
     * 検索動作から離脱する
     */
    private fun exitSearch() {
        // キーボードを隠す
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            binding.topLayout.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )

        // 背景にフォーカスを移す
        binding.topLayout.requestFocus()
    }
}