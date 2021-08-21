package com.to.markdownnote.activity.dialog

import android.content.Context
import android.content.DialogInterface
import com.to.markdownnote.R

/**
 * 指定された引数を使用して、共通確認ダイアログを新規作成する。
 *
 * @param title タイトル
 * @param message メッセージ
 * @param positiveText ポジティブボタンのテキスト
 * @param negativeText ネガティブボタンのテキスト
 * @param neutralText ニュートラルボタンのテキスト
 * @param onPositiveClick ポジティブボタンクリックのハンドラ
 * @param onNegativeClick ネガティブボタンクリックのハンドラ
 * @param onNeutralClick ニュートラルボタンクリックのハンドラ
 * @param canceledOnTouchOutside ダイアログ外の領域をタッチした際にキャンセル扱いにするかどうか
 * @return 共通確認ダイアログ
 */
fun newCommonConfirmDialogFragment(
    title: String? = null,
    message: String,
    positiveText: String? = null,
    negativeText: String? = null,
    neutralText: String? = null,
    onPositiveClick: () -> Unit = {},
    onNegativeClick: () -> Unit = {},
    onNeutralClick: () -> Unit = {},
    canceledOnTouchOutside: Boolean = false
): CommonConfirmDialogFragment {
    return CommonConfirmDialogFragment.newInstance(
        title = title,
        message = message,
        positiveText = positiveText,
        negativeText = negativeText,
        neutralText = neutralText,
        listener = createCommonConfirmListener(
            onPositiveClick,
            onNegativeClick,
            onNeutralClick
        ),
        canceledOnTouchOutside = canceledOnTouchOutside
    )
}

/**
 * 指定された引数を使用して、保存確認ダイアログを新規作成する。
 *
 * @param context コンテキスト
 * @param onPositiveClick ポジティブボタンクリックのハンドラ
 * @param onNegativeClick ネガティブボタンクリックのハンドラ
 * @param onNeutralClick ニュートラルボタンクリックのハンドラ
 * @param canceledOnTouchOutside ダイアログ外の領域をタッチした際にキャンセル扱いにするかどうか
 * @return 保存確認ダイアログ
 */
fun newSaveConfirmDialogFragment(
    context: Context,
    onPositiveClick: () -> Unit = {},
    onNegativeClick: () -> Unit = {},
    onNeutralClick: () -> Unit = {},
    canceledOnTouchOutside: Boolean = false
): CommonConfirmDialogFragment {
    return newCommonConfirmDialogFragment(
        message = context.getString(R.string.save_confirm_message),
        positiveText = context.getString(R.string.yes),
        negativeText = context.getString(R.string.no),
        neutralText = context.getString(R.string.cancel),
        onPositiveClick = onPositiveClick,
        onNegativeClick = onNegativeClick,
        onNeutralClick = onNeutralClick,
        canceledOnTouchOutside = canceledOnTouchOutside
    )
}

/**
 * 指定された引数を使用して、削除確認ダイアログを新規作成する。
 *
 * @param context コンテキスト
 * @param onPositiveClick ポジティブボタンクリックのハンドラ
 * @param onNegativeClick ネガティブボタンクリックのハンドラ
 * @param canceledOnTouchOutside ダイアログ外の領域をタッチした際にキャンセル扱いにするかどうか
 * @return 削除確認ダイアログ
 */
fun newDeleteConfirmDialogFragment(
    context: Context,
    onPositiveClick: () -> Unit = {},
    onNegativeClick: () -> Unit = {},
    canceledOnTouchOutside: Boolean = false
): CommonConfirmDialogFragment {
    return newCommonConfirmDialogFragment(
        message = context.getString(R.string.delete_confirm_message),
        positiveText = context.getString(R.string.yes),
        negativeText = context.getString(R.string.no),
        onPositiveClick = onPositiveClick,
        onNegativeClick = onNegativeClick,
        canceledOnTouchOutside = canceledOnTouchOutside
    )
}

/**
 * 指定された引数を使用して、ファイル出力ダイアログを新規作成する。
 *
 * @param initialFileName 初期表示ファイル名
 * @param canceledOnTouchOutside ダイアログ外の領域をタッチした際にキャンセル扱いにするかどうか
 * @param onOutputButtonClickListener 出力ボタンクリックのコールバックリスナ
 * @return ファイル出力ダイアログ
 */
fun newFileOutputDialogFragment(
    initialFileName: String? = null,
    canceledOnTouchOutside: Boolean = false,
    onOutputButtonClickListener: FileOutputDialogFragment.OnOutputButtonClickListener? = null
): FileOutputDialogFragment {
    return FileOutputDialogFragment.newInstance(
        initialFileName,
        canceledOnTouchOutside,
        onOutputButtonClickListener
    )
}

/**
 * 指定された引数を使用して、ファイル出力ダイアログを新規作成する。
 *
 * @param initialFileName 初期表示ファイル名
 * @param canceledOnTouchOutside ダイアログ外の領域をタッチした際にキャンセル扱いにするかどうか
 * @param onOutputButtonClick 出力ボタンクリックのハンドラ
 * @return ファイル出力ダイアログ
 */
fun newFileOutputDialogFragment(
    initialFileName: String? = null,
    canceledOnTouchOutside: Boolean = false,
    onOutputButtonClick: (String) -> Unit = {}
): FileOutputDialogFragment {
    return newFileOutputDialogFragment(
        initialFileName,
        canceledOnTouchOutside,
        createFileOutputListener(onOutputButtonClick)
    )
}

private fun createCommonConfirmListener(
    onPositiveClick: () -> Unit = {},
    onNegativeClick: () -> Unit = {},
    onNeutralClick: () -> Unit = {}
): CommonConfirmDialogFragment.OnClickListener {
    return object :
        CommonConfirmDialogFragment.OnClickListener() {
        override fun onClick(dialog: DialogInterface?, which: Int) {
            when (which) {
                DialogInterface.BUTTON_POSITIVE ->
                    onPositiveClick()
                DialogInterface.BUTTON_NEGATIVE ->
                    onNegativeClick()
                DialogInterface.BUTTON_NEUTRAL ->
                    onNeutralClick()
            }
        }
    }
}

private fun createFileOutputListener(onOutputButtonClick: (String) -> Unit): FileOutputDialogFragment.OnOutputButtonClickListener {
    return object : FileOutputDialogFragment.OnOutputButtonClickListener() {
        override fun onOutputButtonClick(outputFileName: String) {
            onOutputButtonClick(outputFileName)
        }
    }
}
