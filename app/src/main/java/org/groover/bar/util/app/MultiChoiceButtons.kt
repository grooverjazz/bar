package org.groover.bar.util.app

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
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
fun <T> MultiChoiceButtons(
    state: T,
    setState: (T) -> Unit,
    options: List<String>,
    values: List<T>,
    pressedColor: Color = MaterialTheme.colorScheme.primary,
    depressedColor: Color = MaterialTheme.colorScheme.secondary
) {
    Row {
        options.zip(values).forEach { (option, value) ->
            val modifier = Modifier
                .weight(1f)
                .height(80.dp)
            val modifierShadow = modifier.innerShadow(
                shape = RectangleShape,
                blur = 12.dp,
                offsetX = 3.dp,
                offsetY = 3.dp,
            )

            Button(
                modifier = if (state == value) modifierShadow else modifier,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state == value)
                        pressedColor
                    else depressedColor
                ),
                shape = RectangleShape,
                onClick = { setState(value) }
            ) { Text(text = option, fontSize = 25.sp) }
        }
    }
}