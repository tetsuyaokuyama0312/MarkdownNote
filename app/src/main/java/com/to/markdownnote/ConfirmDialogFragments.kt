package com.to.markdownnote

import android.content.Context
import android.content.DialogInterface

fun newSaveConfirmDialogFragment(
    context: Context,
    onPositiveClick: () -> Unit = {},
    onNegativeClick: () -> Unit = {},
    onNeutralClick: () -> Unit = {}
): CommonConfirmDialogFragment {
    return CommonConfirmDialogFragment().apply {
        setArguments(
            message = context.getString(R.string.save_confirm_message),
            positiveText = context.getString(R.string.yes),
            negativeText = context.getString(R.string.no),
            neutralText = context.getString(R.string.cancel),
            listener = createListener(onPositiveClick, onNegativeClick, onNeutralClick)
        )
    }
}

fun newDeleteConfirmDialogFragment(
    context: Context,
    onPositiveClick: () -> Unit = {},
    onNegativeClick: () -> Unit = {}
): CommonConfirmDialogFragment {
    return CommonConfirmDialogFragment().apply {
        setArguments(
            message = context.getString(R.string.delete_confirm_message),
            positiveText = context.getString(R.string.yes),
            negativeText = context.getString(R.string.no),
            listener = createListener(onPositiveClick, onNegativeClick)
        )
    }
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