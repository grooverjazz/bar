package org.groover.bar.util.app

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BigButton(
    text: String,
    color: Color = MaterialTheme.colorScheme.primary,
    fontColor: Color = Color.White,
    fontSize: TextUnit = 30.sp,
    onClick: () -> Unit = { },
    rounded: Boolean = false,
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(95.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = if (rounded) ButtonDefaults.shape else RectangleShape,
        onClick = onClick,
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            color = fontColor,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LongPressBigButton(
    text: String,
    color: Color = MaterialTheme.colorScheme.primary,
    fontColor: Color = Color.White,
    fontSize: TextUnit = 30.sp,
    onClick: () -> Unit = { },
    onLongClick: () -> Unit = { },
    rounded: Boolean = false,
) {
    ElevatedCard(
        modifier = Modifier
            .height(95.dp)
            .clip(if (rounded) ButtonDefaults.shape else RectangleShape)
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = onClick,
                onLongClick = onLongClick,
            ),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            containerColor = color,
        ),
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontSize = fontSize,
                color = fontColor,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}
