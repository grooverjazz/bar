package org.groover.bar.util.app

import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun LazyBigList(
    height: Dp = 800.dp,
    content: LazyListScope.() -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .padding(10.dp)
            .height(height)
            .innerShadow(
                shape = RoundedCornerShape(16.dp),
                blur = 12.dp,
                offsetX = 3.dp,
                offsetY = 3.dp,
            )
            .clip(RoundedCornerShape(16.dp)),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        content = content
    )
}

@Composable
fun BigList(
    height: Dp = 800.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(modifier = Modifier
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
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            content = content,
        )
    }
}