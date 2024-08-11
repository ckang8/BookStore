package com.example.bookstoreproject.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomTextField (
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false, // Optional parameter for password field
    textStyle: TextStyle = TextStyle(
        fontSize = 16.sp,
        color = Color.Black
    ),
    labelWidth: Int = 300
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .widthIn(max= labelWidth.dp)
            .padding(bottom = 16.dp),
        label = { Text(label, style = textStyle) },
        textStyle = textStyle,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
    )
}