package com.to.markdownnote

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.fragment.app.DialogFragment

/**
 * 共通確認ダイアログクラス
 *
 * このクラスを利用する場合、[show] を呼び出す前に、初期処理として [setArguments]を呼び出す必要がある。
 * [setArguments]を呼び出すことで、ダイアログの作成時に使用される以下の引数を設定することができる。
 *
 * - ダイアログのタイトル(省略可)
 * - ダイアログのメッセージ(必須)
 * - ポジティブボタンのテキスト(省略可※1)
 * - ネガティブボタンのテキスト(省略可※1)
 * - ニュートラルボタンのテキスト(省略可※1)
 * - ボタンクリック時に呼び出されるコールバックリスナ(省略可)
 *
 * ※1 ただしポジティブボタン、ネガティブボタン、ニュートラルボタンのいずれかは設定されている必要がある。
 */
class CommonConfirmDialogFragment : DialogFragment() {
    companion object {
        private const val TITLE_KEY = "TITLE"
        private const val MESSAGE_KEY = "MESSAGE"
        private const val POSITIVE_TEXT_KEY = "POSITIVE_TEXT"
        private const val NEGATIVE_TEXT_KEY = "NEGATIVE_TEXT"
        private const val NEUTRAL_TEXT_KEY = "NEUTRAL_TEXT"
        private const val LISTENER_KEY = "LISTENER"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val message = arguments?.getString(MESSAGE_KEY)
            ?: throw IllegalArgumentException("Please call setArguments() before calling show()")
        val title = arguments?.getString(TITLE_KEY)
        val positiveText = arguments?.getString(POSITIVE_TEXT_KEY)
        val negativeText = arguments?.getString(NEGATIVE_TEXT_KEY)
        val neutralText = arguments?.getString(NEUTRAL_TEXT_KEY)
        val listener =
            arguments?.getParcelable(LISTENER_KEY) ?: OnClickListener()

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(title)
            builder.setMessage(message)
            builder.setPositiveButton(positiveText, listener)
            builder.setNegativeButton(negativeText, listener)
            builder.setNeutralButton(neutralText, listener)
            builder.create()
        } ?: throw IllegalStateException("Activity must not be null")
    }

    /**
     * このクラスの引数を設定する。
     *
     * @param title ダイアログのタイトル
     * @param message ダイアログのメッセージ
     * @param positiveText ポジティブボタンのテキスト
     * @param negativeText ネガティブボタンのテキスト
     * @param neutralText ニュートラルボタンのテキスト
     * @param listener ダイアログのボタンクリック時に呼び出されるコールバックリスナ
     */
    fun setArguments(
        title: String? = null, message: String, positiveText: String? = null,
        negativeText: String? = null, neutralText: String? = null,
        listener: OnClickListener? = null
    ) {
        if (positiveText == null && negativeText == null && neutralText == null) {
            throw IllegalArgumentException("Either positiveText, negativeText or neutralText must not be null")
        }

        arguments = Bundle().apply {
            putString(TITLE_KEY, title)
            putString(MESSAGE_KEY, message)
            putString(POSITIVE_TEXT_KEY, positiveText)
            putString(NEGATIVE_TEXT_KEY, negativeText)
            putString(NEUTRAL_TEXT_KEY, neutralText)
            putParcelable(LISTENER_KEY, listener)
        }
    }

    /**
     * 共通確認ダイアログのコールバックリスナ
     *
     * 共通確認ダイアログを利用するクラスは、このクラスの`onClick`メソッドをオーバーライドして、
     * ダイアログがクリックされた時のコールバック処理を実装する。
     */
    open class OnClickListener() : DialogInterface.OnClickListener,
        Parcelable {
        constructor(parcel: Parcel) : this()

        override fun onClick(dialog: DialogInterface?, which: Int) {}

        override fun writeToParcel(dest: Parcel?, flags: Int) {}

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<OnClickListener> {
            override fun createFromParcel(parcel: Parcel): OnClickListener {
                return OnClickListener(parcel)
            }

            override fun newArray(size: Int): Array<OnClickListener?> {
                return arrayOfNulls(size)
            }
        }
    }
}