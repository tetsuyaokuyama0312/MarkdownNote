package com.to.markdownnote

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout

fun newCommonConfirmDialogFragment(
    title: String? = null, message: String, positiveText: String? = null,
    negativeText: String? = null, neutralText: String? = null, onPositiveClick: () -> Unit = {},
    onNegativeClick: () -> Unit = {},
    onNeutralClick: () -> Unit = {}
): CommonConfirmDialogFragment {
    return CommonConfirmDialogFragment().apply {
        setArguments(
            title = title,
            message = message,
            positiveText = positiveText,
            negativeText = negativeText,
            neutralText = neutralText,
            listener = createListener(onPositiveClick, onNegativeClick, onNeutralClick)
        )
    }
}

fun newSaveConfirmDialogFragment(
    context: Context,
    onPositiveClick: () -> Unit = {},
    onNegativeClick: () -> Unit = {},
    onNeutralClick: () -> Unit = {}
): CommonConfirmDialogFragment {
    return newCommonConfirmDialogFragment(
        message = context.getString(R.string.save_confirm_message),
        positiveText = context.getString(R.string.yes),
        negativeText = context.getString(R.string.no),
        neutralText = context.getString(R.string.cancel),
        onPositiveClick = onPositiveClick,
        onNegativeClick = onNegativeClick,
        onNeutralClick = onNeutralClick
    )
}

fun newDeleteConfirmDialogFragment(
    context: Context,
    onPositiveClick: () -> Unit = {},
    onNegativeClick: () -> Unit = {}
): CommonConfirmDialogFragment {
    return newCommonConfirmDialogFragment(
        message = context.getString(R.string.delete_confirm_message),
        positiveText = context.getString(R.string.yes),
        negativeText = context.getString(R.string.no),
        onPositiveClick = onPositiveClick,
        onNegativeClick = onNegativeClick
    )
}

fun newConfirmDialogWithView(
    context: Context,
    view: View,
    title: String? = null, message: String, positiveText: String? = null,
    negativeText: String? = null, neutralText: String? = null,
    onPositiveClick: () -> Unit = {},
    onNegativeClick: () -> Unit = {},
    onNeutralClick: () -> Unit = {}
): AlertDialog {
    val listener = createListenerWithView(view, onPositiveClick, onNegativeClick, onNeutralClick)
    return AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(message)
        .setView(view)
        .setPositiveButton(positiveText, listener)
        .setNegativeButton(negativeText, listener)
        .setNeutralButton(neutralText, listener)
        .create()
}

fun newFileOutputConfirmDialog(
    context: Context,
    initialFileName: String,
    onPositiveClick: (String) -> Unit = {},
    onNegativeClick: (String) -> Unit = {}
): AlertDialog {
    // TODO ちょっとここらへんは最終的にはもう少し整理する
    // 正直、独自レイアウトを作った方が良い気がする
    val layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.MATCH_PARENT
    ).apply {
        setMargins(16, 8, 16, 8)
    }
    val editText = EditText(context).apply {
        setLayoutParams(layoutParams)
        setText(initialFileName)
    }

    return newConfirmDialogWithView(
        context,
        editText,
        message = context.getString(R.string.file_output_dialog_message),
        positiveText = context.getString(R.string.output),
        negativeText = context.getString(R.string.cancel),
        onPositiveClick = { onPositiveClick(editText.text.toString()) },
        onNegativeClick = { onNegativeClick(editText.text.toString()) }
    )
}

private fun createListener(
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

private fun createListenerWithView(
    view: View,
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