package org.groover.bar.util.app

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BigButton(
    text: String,
    color: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit = { },
    rounded: Boolean = false,
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(95.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = if (rounded) ButtonDefaults.shape else RectangleShape,
        onClick = onClick
    ) {
        Text(
            text = text,
            fontSize = 30.sp
        )
    }
}