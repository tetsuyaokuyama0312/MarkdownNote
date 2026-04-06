package com.to.markdownnote.core.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FileOutputDialog(
    title: String,
    defaultFileName: String,
    outputLabel: String,
    cancelLabel: String,
    fileNameLabel: String,
    onOutput: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var fileName by rememberSaveable { mutableStateOf(defaultFileName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                Text(fileNameLabel)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    singleLine = true,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onOutput(fileName) },
                enabled = fileName.isNotBlank(),
            ) { Text(outputLabel) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(cancelLabel) }
        },
    )
}
