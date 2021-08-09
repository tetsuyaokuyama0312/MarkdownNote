package com.to.markdownnote

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.DialogFragment
import com.to.markdownnote.databinding.DialogFileOutputBinding
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * ファイル出力ダイアログ。
 */
class FileOutputDialogFragment : DialogFragment() {
    companion object {
        private const val INITIAL_FILE_NAME_KEY = "INITIAL_FILE_NAME"
        private const val CANCELED_ON_TOUCH_OUTSIDE_KEY = "CANCELED_ON_TOUCH_OUTSIDE"
        private const val LISTENER_KEY = "LISTENER"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = activity ?: throw IllegalStateException("Activity must not be null")
        val inflater = activity.layoutInflater
        val binding = DialogFileOutputBinding.inflate(inflater)

        val initialFileName = arguments?.getString(INITIAL_FILE_NAME_KEY) ?: ""
        val canceledOnTouchOutside = arguments?.getBoolean(CANCELED_ON_TOUCH_OUTSIDE_KEY) ?: false
        val listener = arguments?.getParcelable(LISTENER_KEY) ?: OnOutputButtonClickListener()

        // 出力ファイル名テキスト
        binding.outputFileNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                // 入力されている場合は出力ボタンを有効化
                if (s.isNullOrBlank()) {
                    binding.outputButtonFileOutput.isEnabled = false
                    binding.outputButtonFileOutput.setTextColor(Color.GRAY)
                } else {
                    binding.outputButtonFileOutput.isEnabled = true
                    binding.outputButtonFileOutput.setTextColor(Color.BLACK)
                }
            }
        })
        binding.outputFileNameEditText.setText(initialFileName)
        binding.outputFileNameEditText.requestFocus()
        // 拡張子を除いたファイル名を選択状態にする
        val idxExtensionStart = initialFileName.lastIndexOf('.')
        if (idxExtensionStart >= 0) {
            binding.outputFileNameEditText.setSelection(0, idxExtensionStart)
        } else {
            // 拡張子がない場合は全選択
            binding.outputFileNameEditText.selectAll()
        }

        // 出力ボタン
        binding.outputButtonFileOutput.setOnClickListener {
            listener.onOutputButtonClick(binding.outputFileNameEditText.text.toString())
            requireDialog().cancel()
        }
        // キャンセルボタン
        binding.cancelButtonFileOutput.setOnClickListener {
            requireDialog().cancel()
        }

        return Dialog(activity).apply {
            setContentView(binding.root)
            setCanceledOnTouchOutside(canceledOnTouchOutside)
        }
    }

    fun setArguments(
        initialFileName: String? = null,
        canceledOnTouchOutside: Boolean = false,
        listener: OnOutputButtonClickListener? = null
    ) {
        arguments = Bundle().apply {
            putString(INITIAL_FILE_NAME_KEY, initialFileName)
            putBoolean(CANCELED_ON_TOUCH_OUTSIDE_KEY, canceledOnTouchOutside)
            putParcelable(LISTENER_KEY, listener)
        }
    }

    @Parcelize
    open class OnOutputButtonClickListener : EventListener, Parcelable {
        open fun onOutputButtonClick(outputFileName: String) {}
    }
}