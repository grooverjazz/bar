package org.groover.bar.util.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BigList(
    height: Dp = 800.dp,
    content: LazyListScope.() -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .padding(10.dp)
//            .background(color = Color.LightGray)
            .height(height)
            .innerShadow(
                shape = RoundedCornerShape(16.dp),
                blur = 12.dp,
                offsetX = 3.dp,
                offsetY = 3.dp,
            )
            .clip(RoundedCornerShape(16.dp)),
        content = content
    )
}