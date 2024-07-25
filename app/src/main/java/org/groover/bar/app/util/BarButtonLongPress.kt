package org.groover.bar.app.util

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A styled button that allows for a callback on long press.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BarButtonLongPress(
    text: String,
    color: Color = MaterialTheme.colorScheme.primary,
    fontColor: Color = Color.White,
    fontSize: TextUnit = 30.sp,
    onClick: () -> Unit = { },
    onLongClick: () -> Unit = { },
    rounded: Boolean = false,
    enabled: Boolean = true,
) {
    // (the button is implemented by heavily modifying a Card)
    ElevatedCard(
        Modifier
        .height(95.dp)
        .clip(if (rounded) ButtonDefaults.shape else RectangleShape)
        .combinedClickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(),
            onClick = onClick,
            onLongClick = onLongClick,
            enabled = enabled,
        ),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(color),
    ) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text,
                fontSize = fontSize,
                color = fontColor,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}
