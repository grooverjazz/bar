package org.groover.bar.app.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A simple, styled, list that evaluates its items lazily.
 *   (handy for large lists)
 */
@Composable
fun BarListLazy(
    height: Dp = 800.dp,
    spacing: Dp = 10.dp,
    content: LazyListScope.() -> Unit,
) {
    LazyColumn(
        Modifier
            .padding(10.dp)
            .height(height)
            .innerShadow(
                shape = RoundedCornerShape(16.dp),
                blur = 12.dp,
                offsetX = 3.dp,
                offsetY = 3.dp,
            )
            .clip(RoundedCornerShape(16.dp)),
        verticalArrangement = Arrangement.spacedBy(spacing),
        content = content,
    )
}