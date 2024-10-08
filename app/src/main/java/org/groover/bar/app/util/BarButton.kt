package org.groover.bar.app.util

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A simple styled button.
 */
@Composable
fun BarButton(
    text: String,
    color: Color = MaterialTheme.colorScheme.primary,
    fontColor: Color = Color.White,
    fontSize: TextUnit = 30.sp,
    onClick: () -> Unit = { },
    rounded: Boolean = false,
    enabled: Boolean = true,
) {
    Button(onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(95.dp),
        colors = ButtonDefaults.buttonColors(color),
        shape = if (rounded) ButtonDefaults.shape else RectangleShape,
        enabled = enabled,
    ) {
        Text(text,
            fontSize = fontSize,
            color = fontColor,
            textAlign = TextAlign.Center,
        )
    }
}

