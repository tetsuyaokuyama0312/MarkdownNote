package com.to.markdownnote.activity.dialog

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
 * このクラスのインスタンスは、[CommonConfirmDialogFragment.newInstance]を呼び出すことで作成できる。
 */
class CommonConfirmDialogFragment private constructor() : DialogFragment() {
    companion object {
        private const val TITLE_KEY = "TITLE"
        private const val MESSAGE_KEY = "MESSAGE"
        private const val POSITIVE_TEXT_KEY = "POSITIVE_TEXT"
        private const val NEGATIVE_TEXT_KEY = "NEGATIVE_TEXT"
        private const val NEUTRAL_TEXT_KEY = "NEUTRAL_TEXT"
        private const val LISTENER_KEY = "LISTENER"
        private const val CANCELED_ON_TOUCH_OUTSIDE_KEY = "CANCELED_ON_TOUCH_OUTSIDE"

        /**
         * 指定された引数を使用して、`CommonConfirmDialogFragment`を作成する。
         *
         * 引数のうち、`message`は省略不可である。
         * また`positiveText`, `negativeText`, `neutralText`のいずれかは設定されている必要がある。
         *
         * @param title ダイアログのタイトル
         * @param message ダイアログのメッセージ
         * @param positiveText ポジティブボタンのテキスト
         * @param negativeText ネガティブボタンのテキスト
         * @param neutralText ニュートラルボタンのテキスト
         * @param listener ダイアログのボタンクリック時に呼び出されるコールバックリスナ
         * @param canceledOnTouchOutside ダイアログ外の領域をタッチした際にキャンセル扱いにするかどうか
         * @return `CommonConfirmDialogFragment`
         * @throws IllegalArgumentException `positiveText`, `negativeText`, `neutralText`がすべて`null`の場合
         */
        fun newInstance(
            title: String? = null, message: String, positiveText: String? = null,
            negativeText: String? = null, neutralText: String? = null,
            listener: OnClickListener? = null, canceledOnTouchOutside: Boolean = false
        ): CommonConfirmDialogFragment {
            if (positiveText == null && negativeText == null && neutralText == null) {
                throw IllegalArgumentException("Either positiveText, negativeText or neutralText must not be null")
            }

            return CommonConfirmDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(TITLE_KEY, title)
                    putString(MESSAGE_KEY, message)
                    putString(POSITIVE_TEXT_KEY, positiveText)
                    putString(NEGATIVE_TEXT_KEY, negativeText)
                    putString(NEUTRAL_TEXT_KEY, neutralText)
                    putParcelable(LISTENER_KEY, listener)
                    putBoolean(CANCELED_ON_TOUCH_OUTSIDE_KEY, canceledOnTouchOutside)
                }
            }
        }
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
        val canceledOnTouchOutside = arguments?.getBoolean(CANCELED_ON_TOUCH_OUTSIDE_KEY) ?: false

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(title)
            builder.setMessage(message)
            builder.setPositiveButton(positiveText, listener)
            builder.setNegativeButton(negativeText, listener)
            builder.setNeutralButton(neutralText, listener)
            builder.create()
        }?.apply {
            setCanceledOnTouchOutside(canceledOnTouchOutside)
        } ?: throw IllegalStateException("Activity must not be null")
    }

    /**
     * 共通確認ダイアログのコールバックリスナ
     *
     * 共通確認ダイアログを利用するクラスは、このクラスの`onClick`メソッドをオーバーライドして、
     * ダイアログがクリックされた時のコールバック処理を実装する。
     */
    open class OnClickListener : DialogInterface.OnClickListener,
        Parcelable {

        override fun onClick(dialog: DialogInterface?, which: Int) {}

        override fun writeToParcel(dest: Parcel?, flags: Int) {}

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<OnClickListener> {
            override fun createFromParcel(parcel: Parcel): OnClickListener {
                return OnClickListener()
            }

            override fun newArray(size: Int): Array<OnClickListener?> {
                return arrayOfNulls(size)
            }
        }
    }
}