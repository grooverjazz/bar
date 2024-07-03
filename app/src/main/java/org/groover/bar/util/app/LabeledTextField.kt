package org.groover.bar.util.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LabeledTextField(
    modifier: Modifier = Modifier,
    text: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false,
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "$text:",
            fontSize = 20.sp,
        )
        Spacer(modifier = Modifier.height(5.dp))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        )
    }
}