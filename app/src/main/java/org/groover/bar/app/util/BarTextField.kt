package org.groover.bar.app.util

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * The type that a BarTextField may be.
 */
enum class BarTextFieldType {
    /** Plain-text.*/
    Text,
    /** A password (turns characters into dots).*/
    Password,
    /** A decimal number (initiates the decimal number keyboard).*/
    Decimal,
}

/**
 * A simple labeled text field.
 */
@Composable
fun BarTextField(
    modifier: Modifier = Modifier,
    text: String? = null,
    value: String,
    onValueChange: (String) -> Unit,
    type: BarTextFieldType = BarTextFieldType.Text,
) {
    Column(modifier) {
        // Label
        Text("$text:",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 20.sp,
        )
        Spacer(Modifier.height(5.dp))

        // Get visual transform (for passwords)
        val visualTransformation = if (type == BarTextFieldType.Password)
            PasswordVisualTransformation()
        else VisualTransformation.None

        // Get keyboard type
        val keyboardType = when (type) {
            BarTextFieldType.Text -> KeyboardType.Text
            BarTextFieldType.Password -> KeyboardType.Text
            BarTextFieldType.Decimal -> KeyboardType.Decimal
        }

        // Text field
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            visualTransformation = visualTransformation,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
        )
    }
}