package com.to.markdownnote

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Space
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.to.markdownnote.model.Memo
import com.to.markdownnote.repository.deleteMemo
import com.to.markdownnote.repository.insertMemo
import com.to.markdownnote.repository.updateMemo
import com.to.markdownnote.util.logDebug
import com.to.markdownnote.util.nowTimestampSec
import com.to.markdownnote.util.parseMarkdownToHTML
import com.to.markdownnote.util.renderHTML
import kotlinx.android.synthetic.main.activity_editor.*

class EditorActivity : AppCompatActivity() {
    companion object {
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
    }

    private var targetMemo: Memo? = null

    private var textEditedByUser: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.editor)

        targetMemo = intent.getParcelableExtra(MEMO_KEY)

        markdown_editor_edittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                renderMarkdown(s.toString())
                textEditedByUser = true
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                renderMarkdown(s.toString())
                textEditedByUser = true
            }

            override fun afterTextChanged(s: Editable?) {
                renderMarkdown(s.toString())
                textEditedByUser = true
            }
        })

        // 保存済みのテキストをセット
        markdown_editor_edittext.setText(targetMemo?.text ?: "")
        // ユーザーによる入力ではないのでfalseに
        textEditedByUser = false

        logDebug("target memo is $targetMemo")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (textEditedByUser) {
                    // ユーザーによって編集されていればダイアログ表示
                    showSaveConfirmDialog()
                } else {
                    finish()
                }
            }
            R.id.menu_edit ->
                ScreenMode.EDIT.apply(
                    vertical_separator,
                    markdown_editor_edittext,
                    markdown_rendering_result_textview
                )
            R.id.menu_separate ->
                ScreenMode.SEPARATE.apply(
                    vertical_separator,
                    markdown_editor_edittext,
                    markdown_rendering_result_textview
                )
            R.id.menu_view ->
                ScreenMode.VIEW.apply(
                    vertical_separator,
                    markdown_editor_edittext,
                    markdown_rendering_result_textview
                )
            R.id.menu_delete -> {
                showDeleteConfirmDialog()
            }
            R.id.menu_complete,
            R.id.menu_complete_text ->
                performComplete()
            else ->
                return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun renderMarkdown(text: String) {
        val html = parseMarkdownToHTML(text)
        renderHTML(markdown_rendering_result_textview, html)
    }

    private fun showSaveConfirmDialog() {
        // 保存確認ダイアログを起動
        val dialog = CommonConfirmDialogFragment()
        dialog.apply {
            setArguments(
                message = this@EditorActivity.getString(R.string.save_confirm_message),
                positiveText = this@EditorActivity.getString(R.string.yes),
                negativeText = this@EditorActivity.getString(R.string.no),
                neutralText = this@EditorActivity.getString(R.string.cancel),
                listener = createSaveConfirmDialogButtonClickListener()
            )
        }.show(supportFragmentManager, dialog::class.simpleName)
    }

    private fun createSaveConfirmDialogButtonClickListener(): CommonConfirmDialogFragment.OnClickListener {
        return object :
            CommonConfirmDialogFragment.OnClickListener() {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        performComplete()
                    }
                    DialogInterface.BUTTON_NEGATIVE ->
                        startActivity(TopActivity.createIntent(this@EditorActivity))
                }
            }
        }
    }

    private fun showDeleteConfirmDialog() {
        // 削除確認ダイアログを起動
        val dialog = CommonConfirmDialogFragment()
        dialog.apply {
            setArguments(
                message = this@EditorActivity.getString(R.string.delete_confirm_message),
                positiveText = this@EditorActivity.getString(R.string.yes),
                negativeText = this@EditorActivity.getString(R.string.no),
                listener = createDeleteConfirmDialogButtonClickListener()
            )
        }.show(supportFragmentManager, dialog::class.simpleName)
    }

    private fun createDeleteConfirmDialogButtonClickListener(): CommonConfirmDialogFragment.OnClickListener {
        return object :
            CommonConfirmDialogFragment.OnClickListener() {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        deleteMemo(this@EditorActivity, targetMemo!!) {
                            logDebug("Deleted memo=[$targetMemo!!]")
                            startActivity(TopActivity.createIntent(this@EditorActivity))
                        }
                    }
//                    DialogInterface.BUTTON_NEGATIVE ->
//                        startActivity(TopActivity.createIntent(this@EditorActivity))
                }
            }
        }
    }

    private fun performComplete() {
        if (!textEditedByUser) {
            // ユーザーにより編集されていない場合は何もせずエディタ画面終了
            startActivity(TopActivity.createIntent(this))
            return
        }

        val text = markdown_editor_edittext.text.toString()
        if (targetMemo == null) {
            // 新規作成の場合
            performCompleteWhenNew(text)
        } else {
            // 編集の場合
            performCompleteWhenEdit(text)
        }
    }

    private fun performCompleteWhenNew(text: String) {
        // 新規作成の場合は、入力済みの場合のみ保存

        if (text.isEmpty()) {
            // 空の場合は何もせずエディタ画面終了
            startActivity(TopActivity.createIntent(this))
        } else {
            // 入力済みの場合は保存
            insertMemo(this, text) {
                logDebug("Inserted new memo=[$it]")
                startActivity(TopActivity.createIntent(this))
            }
        }
    }

    private fun performCompleteWhenEdit(text: String) {
        // 編集の場合は、更新 or 削除

        val oldMemo = targetMemo!!
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
         * @param separator 画面を分割するセパレータ
         * @param editorText テキスト編集部のEditText
         * @param resultView テキストレンダリング結果のTextView
         */
        fun apply(separator: Space, editorText: EditText, resultView: TextView) {
            val layoutParams = separator.layoutParams as? RelativeLayout.LayoutParams ?: return
            changeSeparatorLayout(layoutParams)
            changeEditorTextLayout(editorText)
            changeResultViewLayout(resultView)
        }

        abstract fun changeSeparatorLayout(layoutParams: RelativeLayout.LayoutParams)
        abstract fun changeEditorTextLayout(editorText: EditText)
        abstract fun changeResultViewLayout(resultView: TextView)
    }
}