package com.to.markdownnote

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
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.to.markdownnote.databinding.ActivityEditorBinding
import com.to.markdownnote.model.Memo
import com.to.markdownnote.repository.deleteMemo
import com.to.markdownnote.repository.insertMemo
import com.to.markdownnote.repository.updateMemo
import com.to.markdownnote.util.*

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

    private lateinit var binding: ActivityEditorBinding

    private var targetMemo: Memo? = null

    private var textEditedByUser: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null

        targetMemo = intent.getParcelableExtra(MEMO_KEY)

        binding.markdownRenderingResultTextView.movementMethod =
            ScrollingMovementMethod.getInstance()

        binding.markdownEditorEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                // Markdownレンダリング
                renderMarkdown(text)
                // ResultViewをスクロール
                scrollResultTextView(text)
                textEditedByUser = true
            }
        })

        // 保存済みのテキストをセット
        binding.markdownEditorEditText.setText(targetMemo?.text ?: "")
        // ユーザーによる入力ではないのでfalseに
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
                performToHome()
            R.id.menu_edit ->
                ScreenMode.EDIT.apply(
                    binding.editorVerticalSeparator,
                    binding.markdownEditorEditText,
                    binding.markdownRenderingResultTextView
                )
            R.id.menu_separate ->
                ScreenMode.SEPARATE.apply(
                    binding.editorVerticalSeparator,
                    binding.markdownEditorEditText,
                    binding.markdownRenderingResultTextView
                )
            R.id.menu_view ->
                ScreenMode.VIEW.apply(
                    binding.editorVerticalSeparator,
                    binding.markdownEditorEditText,
                    binding.markdownRenderingResultTextView
                )
            R.id.menu_file_output_plain_text ->
                showFileOutputConfirmDialog(OutputFileType.PLAIN_TEXT)
            R.id.menu_file_output_markdown ->
                showFileOutputConfirmDialog(OutputFileType.MARKDOWN)
            R.id.menu_file_output_html ->
                showFileOutputConfirmDialog(OutputFileType.HTML)
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

    private fun renderMarkdown(text: String) {
        val html = parseMarkdownToHTML(text)
        renderHTML(binding.markdownRenderingResultTextView, html)
    }

    private fun scrollResultTextView(text: String) {
        // EditTextのカーソル位置を取得
        val cursorPos = binding.markdownEditorEditText.selectionEnd

        if (cursorPos == text.length) {
            // 末尾への追記の場合は最下部へスクロール
            binding.markdownRenderingResultTextView.gravity = Gravity.BOTTOM
        } else {
            // それ以外の場合は編集した行番号に応じてスクロール

            // 編集した行番号を取得
            val lineNr = text.substring(0, cursorPos).split(System.lineSeparator()).size
            binding.markdownRenderingResultTextView.post {
                // 行番号からスクロール位置を求めてスクロール
                val scrollY =
                    binding.markdownRenderingResultTextView.layout.getLineTop(lineNr - 1)
                binding.markdownRenderingResultTextView.scrollTo(0, scrollY)
            }
        }
    }

    private fun performToHome() {
        if (textEditedByUser) {
            // ユーザーによって編集されていればダイアログ表示
            showSaveConfirmDialog()
        } else {
            finish()
        }
    }

    private fun showFileOutputConfirmDialog(outputFileType: OutputFileType) {
        newFileOutputConfirmDialog(this, getOutputFileName(outputFileType),
            {
                val outText = outputFileType.convert(binding.markdownEditorEditText.text.toString())
                runAsync(
                    {
                        writeTextFile(this, it, outText)
                    },
                    {
                        logDebug("Saved file, location=$it, text=$outText")
                        val msg =
                            "${getString(R.string.saved_file_message)}\n$it"
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                    }
                )
            }
        ).show()
    }

    private fun getOutputFileName(outputFileType: OutputFileType): String {
        return "memo_${nowTimestampForFileName()}.${outputFileType.getExtension()}"
    }

    private fun showSaveConfirmDialog() {
        // 保存確認ダイアログを起動
        val dialog = newSaveConfirmDialogFragment(this,
            { performComplete() },
            { startActivity(TopActivity.createIntent(this)) })
        dialog.show(supportFragmentManager, dialog::class.simpleName)
    }

    private fun showDeleteConfirmDialog() {
        // 削除確認ダイアログを起動
        val dialog = newDeleteConfirmDialogFragment(this, {
            deleteMemo(this, targetMemo!!) {
                logDebug("Deleted memo=[$targetMemo!!]")
                startActivity(TopActivity.createIntent(this))
            }
        })
        dialog.show(supportFragmentManager, dialog::class.simpleName)
    }

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