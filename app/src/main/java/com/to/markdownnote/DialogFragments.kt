package com.to.markdownnote

import android.content.Context
import android.content.DialogInterface

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
    return CommonConfirmDialogFragment().apply {
        setArguments(
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
}

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

fun newFileOutputDialogFragment(
    initialFileName: String? = null,
    canceledOnTouchOutside: Boolean = false,
    onOutputButtonClickListener: FileOutputDialogFragment.OnOutputButtonClickListener? = null
): FileOutputDialogFragment {
    return FileOutputDialogFragment().apply {
        setArguments(
            initialFileName,
            canceledOnTouchOutside,
            onOutputButtonClickListener
        )
    }
}

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
