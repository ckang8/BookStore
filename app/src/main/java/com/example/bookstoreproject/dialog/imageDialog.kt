package com.example.bookstoreproject.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun imageDialog(
    onDismiss: () -> Unit,
    onPickImage: @Composable () -> Unit,
    onTakePicture: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose an option") },
        text = {
            Column {
                Button(onClick = {
                }) {
                    Text("Pick from Gallery")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                }) {
                    Text("Take a Picture")
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
