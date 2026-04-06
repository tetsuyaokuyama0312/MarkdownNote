package com.to.markdownnote.core.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ConfirmDialog(
    message: String,
    confirmLabel: String,
    dismissLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    cancelLabel: String? = null,
    onCancel: (() -> Unit)? = null,
) {
    AlertDialog(
        onDismissRequest = { onCancel?.invoke() ?: onDismiss() },
        text = { Text(text = message) },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(confirmLabel) }
        },
        dismissButton = {
            if (cancelLabel != null && onCancel != null) {
                TextButton(onClick = onDismiss) { Text(dismissLabel) }
                TextButton(onClick = onCancel) { Text(cancelLabel) }
            } else {
                TextButton(onClick = onDismiss) { Text(dismissLabel) }
            }
        },
    )
}
