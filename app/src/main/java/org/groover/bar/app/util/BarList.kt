package org.groover.bar.app.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A simple styled list.
 */
@Composable
fun BarList(
    height: Dp = 800.dp,
    spacing: Dp = 10.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(Modifier
        .padding(10.dp)
        .height(height)
        .innerShadow(
            shape = RoundedCornerShape(16.dp),
            blur = 12.dp,
            offsetX = 3.dp,
            offsetY = 3.dp,
        )
        .clip(RoundedCornerShape(16.dp)),
    ) {
        Column(Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(spacing),
            content = content,
        )
    }
}