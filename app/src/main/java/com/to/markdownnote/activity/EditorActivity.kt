package com.to.markdownnote.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.to.markdownnote.R
import com.to.markdownnote.activity.dialog.newDeleteConfirmDialogFragment
import com.to.markdownnote.activity.dialog.newFileOutputDialogFragment
import com.to.markdownnote.activity.dialog.newSaveConfirmDialogFragment
import com.to.markdownnote.databinding.ActivityEditorBinding
import com.to.markdownnote.model.Memo
import com.to.markdownnote.repository.deleteMemo
import com.to.markdownnote.repository.insertMemo
import com.to.markdownnote.repository.updateMemo
import com.to.markdownnote.util.*
import kotlin.math.max

/**
 * エディタ画面のアクティビティ
 */
class EditorActivity : AppCompatActivity() {
    companion object {
        /** IntentからMemoを取得する際のキー */
        private const val MEMO_KEY = "MEMO_KEY"

        /**
         * `EditorActivity`を起動するためのIntentを作成する。
         *
         * @param context コンテキスト
         * @param memo 編集対象のメモオブジェクト(新規作成の場合はnull)
         * @return `EditorActivity`を起動するためのIntent
         */
        fun createIntent(context: Context, memo: Memo? = null): Intent {
            val intent = Intent(context, EditorActivity::class.java)
            intent.putExtra(MEMO_KEY, memo)
            return intent
        }

        /**
         * Viewのスクロール位置を調整する割合を示す値。
         *
         * テキストの変更に応じてViewをスクロールする際、変更された行が画面表示領域のどの位置に来るようにスクロールするかを示す値。
         *
         * この値は0～1の範囲内の小数値を取り、0であれば画面表示領域の最上部、0.5であれば画面表示領域の中心部、1であれば画面表示領域の最下部に
         * 変更行が来るようにスクロールする。
         */
        private const val VIEW_SCROLL_RATIO = 0.2
    }

    /** EditorActivityのViewBinding */
    private lateinit var binding: ActivityEditorBinding

    /** 編集対象のメモ(新規作成の場合はnull) */
    private var targetMemo: Memo? = null

    /** ユーザーによりテキストが編集されたかどうか */
    private var textEditedByUser: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null

        // 編集対象メモを取得
        targetMemo = intent.getParcelableExtra(MEMO_KEY)

        binding.markdownRenderingResultTextView.movementMethod =
            ScrollingMovementMethod.getInstance()

        binding.markdownEditorEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                // Markdownレンダリング
                renderAsMarkdown(text)
                // Viewをスクロール
                scrollViewOnTextChanged(text)
                textEditedByUser = true
            }
        })

        // 保存済みのテキストをセット
        binding.markdownEditorEditText.setText(targetMemo?.text ?: "")
        // ユーザーによる入力ではないためfalseに
        textEditedByUser = false

        logDebug("target memo is $targetMemo")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.editor_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->
                performToTop()
            R.id.menu_edit ->
                ScreenMode.EDIT.apply(binding)
            R.id.menu_separate ->
                ScreenMode.SEPARATE.apply(binding)
            R.id.menu_view ->
                ScreenMode.VIEW.apply(binding)
            R.id.menu_file_output_plain_text ->
                showFileOutputDialog(OutputFileType.PLAIN_TEXT)
            R.id.menu_file_output_markdown ->
                showFileOutputDialog(OutputFileType.MARKDOWN)
            R.id.menu_file_output_html ->
                showFileOutputDialog(OutputFileType.HTML)
            R.id.menu_delete ->
                showDeleteConfirmDialog()
            R.id.menu_complete,
            R.id.menu_complete_text ->
                performComplete()
            R.id.menu_cancel ->
                // キャンセルは何もせず閉じる
                return true
            else ->
                return super.onOptionsItemSelected(item)
        }
        return true
    }

    /**
     * 入力テキストをMarkdownとしてレンダリング結果表示部にレンダリングする。
     *
     * @param text 入力テキスト
     */
    private fun renderAsMarkdown(text: String) {
        val html = parseMarkdownToHTML(text)
        renderHTML(binding.markdownRenderingResultTextView, html)
    }

    /**
     * テキストの変更に応じてViewをスクロールする。
     *
     * @param text 入力テキスト
     */
    private fun scrollViewOnTextChanged(text: String) {
        // EditTextのカーソル位置を取得
        val cursorPos = binding.markdownEditorEditText.selectionEnd

        if (cursorPos == text.length) {
            // 末尾への追記の場合は最下部へスクロール
            binding.markdownEditorEditText.gravity = Gravity.BOTTOM
            binding.markdownRenderingResultTextView.gravity = Gravity.BOTTOM
        } else {
            // それ以外の場合は編集した行番号に応じてスクロール
            binding.markdownEditorEditText.gravity = Gravity.NO_GRAVITY
            binding.markdownRenderingResultTextView.gravity = Gravity.NO_GRAVITY

            // 編集した行番号を取得
            val lineNr = text.substring(0, cursorPos).split(System.lineSeparator()).size
            // テキスト入力部をスクロール
            scrollViewByLineNr(binding.markdownEditorEditText, lineNr)
            // レンダリング結果表示部をスクロール
            scrollViewByLineNr(binding.markdownRenderingResultTextView, lineNr)
        }
    }

    /**
     * 変更されたテキストの行数に応じてViewをスクロールする。
     *
     * @param textView スクロール対象のView
     * @param lineNr 変更されたテキストの行数
     */
    private fun scrollViewByLineNr(textView: TextView, lineNr: Int) {
        textView.post {
            // 行番号からスクロール位置を求める
            val lineTop = textView.layout?.getLineTop(lineNr - 1) ?: return@post
            // 対象行の表示位置を調整
            val scrollY = lineTop - (textView.height * VIEW_SCROLL_RATIO).toInt()

            // 算出した方向にスクロール可能であればスクロール
            if (textView.canScrollVertically(scrollY)) {
                // 最上部より上にスクロールしないよう、マイナスは0に補正
                textView.scrollTo(0, max(scrollY, 0))
            }
        }
    }

    /**
     * Top画面へ戻る操作を行う。
     */
    private fun performToTop() {
        if (textEditedByUser) {
            // ユーザーによって編集されていればダイアログ表示
            showSaveConfirmDialog()
        } else {
            finish()
        }
    }

    /**
     * ファイル出力ダイアログを表示する。
     *
     * @param type 出力ファイルタイプ
     */
    private fun showFileOutputDialog(type: OutputFileType) {
        val dialog =
            newFileOutputDialogFragment(getDefaultOutputFileName(type.getExtension())) { outputFileName ->
                // 出力形式のテキストに変換
                val outText = type.convert(binding.markdownEditorEditText.text.toString())
                runAsync({
                    // ファイル出力
                    writeTextFile(this@EditorActivity, outputFileName, outText)
                }) {
                    // 出力完了メッセージ表示
                    logDebug("Saved file, location=$it, text=$outText")
                    val msg =
                        "${getString(R.string.saved_file_message)}${System.lineSeparator()}$it"
                    Snackbar.make(binding.root, msg, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.close) {}
                        .apply { view.findViewById<TextView>(R.id.snackbar_text).maxLines = 5 }
                        .show()
                }
            }
        dialog.show(supportFragmentManager, dialog::class.simpleName)
    }

    /**
     * 保存確認ダイアログを表示する。
     */
    private fun showSaveConfirmDialog() {
        val dialog = newSaveConfirmDialogFragment(this,
            onPositiveClick = { performComplete() },
            onNegativeClick = { startActivity(TopActivity.createIntent(this)) })
        dialog.show(supportFragmentManager, dialog::class.simpleName)
    }

    /**
     * 削除確認ダイアログを表示する。
     */
    private fun showDeleteConfirmDialog() {
        val dialog = newDeleteConfirmDialogFragment(this, onPositiveClick = {
            // メモを削除
            deleteMemo(this, targetMemo!!) {
                logDebug("Deleted memo=[$targetMemo!!]")
                startActivity(TopActivity.createIntent(this))
            }
        })
        dialog.show(supportFragmentManager, dialog::class.simpleName)
    }

    /**
     * エディタ画面の完了操作を行う。
     */
    private fun performComplete() {
        if (!textEditedByUser) {
            // ユーザーにより編集されていない場合は何もせずエディタ画面終了
            finish()
            return
        }

        val text = binding.markdownEditorEditText.text.toString()
        if (targetMemo == null) {
            // 新規作成の場合
            performCompleteWhenNew(text)
        } else {
            // 編集の場合
            performCompleteWhenEdit(text, targetMemo!!)
        }
    }

    /**
     * メモ新規作成時のエディタ画面の完了操作を行う。
     *
     * @param text 入力テキスト
     */
    private fun performCompleteWhenNew(text: String) {
        // 新規作成の場合は、入力済みの場合のみ保存

        if (text.isEmpty()) {
            // 空の場合は何もせずエディタ画面終了
            finish()
        } else {
            // 入力済みの場合は保存
            insertMemo(this, text) {
                logDebug("Inserted new memo=[$it]")
                startActivity(TopActivity.createIntent(this))
            }
        }
    }

    /**
     * メモ編集時のエディタ画面の完了操作を行う。
     *
     * @param text 入力テキスト
     * @param oldMemo 更新対象のメモ
     */
    private fun performCompleteWhenEdit(text: String, oldMemo: Memo) {
        // 編集の場合は、更新 or 削除

        if (text.isEmpty()) {
            // 空の場合は削除
            deleteMemo(this, oldMemo) {
                logDebug("Deleted memo=[$oldMemo]")
                startActivity(TopActivity.createIntent(this))
            }
        } else {
            // 入力済みの場合は更新
            val newMemo = oldMemo.copy(text = text, lastUpdatedDate = nowTimestampSec())
            updateMemo(
                this,
                newMemo
            ) {
                logDebug("Updated old memo=[$targetMemo] to new memo=[$it]")
                startActivity(TopActivity.createIntent(this))
            }
        }
    }

    /**
     * 画面モードを表す列挙型
     */
    private enum class ScreenMode {
        /** 編集モード */
        EDIT {
            override fun changeSeparatorLayout(layoutParams: RelativeLayout.LayoutParams) {
                layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_START)
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END)
            }

            override fun changeEditorTextLayout(editorText: EditText) {
                editorText.width = ViewGroup.LayoutParams.MATCH_PARENT
            }

            override fun changeResultViewLayout(resultView: TextView) {
                resultView.width = 0
            }
        },

        /** 分割モード */
        SEPARATE {
            override fun changeSeparatorLayout(layoutParams: RelativeLayout.LayoutParams) {
                layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_START)
                layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_END)
            }

            override fun changeEditorTextLayout(editorText: EditText) {
                editorText.width = 0
            }

            override fun changeResultViewLayout(resultView: TextView) {
                resultView.width = 0
            }
        },

        /** 閲覧モード */
        VIEW {
            override fun changeSeparatorLayout(layoutParams: RelativeLayout.LayoutParams) {
                layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_END)
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START)
            }

            override fun changeEditorTextLayout(editorText: EditText) {
                editorText.width = 0
            }

            override fun changeResultViewLayout(resultView: TextView) {
                resultView.width = ViewGroup.LayoutParams.MATCH_PARENT
            }
        };

        /**
         * このモードを適用する。
         *
         * @param binding EditorActivityのViewBinding
         */
        fun apply(binding: ActivityEditorBinding) {
            val separator = binding.editorVerticalSeparator
            val layoutParams = separator.layoutParams as? RelativeLayout.LayoutParams ?: return
            changeSeparatorLayout(layoutParams)
            changeEditorTextLayout(binding.markdownEditorEditText)
            changeResultViewLayout(binding.markdownRenderingResultTextView)
        }

        /**
         * セパレータのレイアウト変更を行う。
         *
         * @param layoutParams セパレータのLayoutParams
         */
        abstract fun changeSeparatorLayout(layoutParams: RelativeLayout.LayoutParams)

        /**
         * テキスト入力部のレイアウト変更を行う。
         *
         * @param editorText テキスト入力部のEditText
         */
        abstract fun changeEditorTextLayout(editorText: EditText)

        /**
         * レンダリング結果表示部のレイアウト変更を行う。
         *
         * @param resultView レンダリング結果表示部のTextView
         */
        abstract fun changeResultViewLayout(resultView: TextView)
    }
}